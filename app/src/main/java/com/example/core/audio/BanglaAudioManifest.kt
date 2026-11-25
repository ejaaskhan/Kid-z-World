package com.example.core.audio

import android.content.res.AssetManager
import android.util.Log
import java.io.InputStream

data class BanglaAudioEntry(
    val id: String,
    val category: String,
    val assetPath: String,
    val scriptBn: String,
    val aliases: List<String>
)

data class BundledAudioAudit(
    val verifiedCount: Int,
    val totalCount: Int,
    val missingAssetPaths: List<String>
) {
    val isComplete: Boolean get() = totalCount > 0 && verifiedCount == totalCount
}

/**
 * Centralized offline Bangla audio manifest mapping every KID'Z WORLD curriculum ID
 * (alphabets, numbers 0–100, fruits, animals, colors, shapes, common words, stories,
 * Bangla poems + stanzas, puzzles, and instructions) to its exact bundled MP3 asset under
 * assets/audio/bangla/.
 *
 * Single source of truth: loaded directly from assets/audio/bangla/manifest.json.
 */
class BanglaAudioManifest private constructor(
    val entries: List<BanglaAudioEntry>
) {
    private val byId: Map<String, BanglaAudioEntry> = entries.associateBy { it.id }

    private val byNormalizedAlias: Map<String, BanglaAudioEntry> = buildMap {
        // Put explicit curriculum entries first, then allow shorter puzzle/word tokens to map cleanly
        this@BanglaAudioManifest.entries.forEach { entry ->
            putIfAbsent(normalizeKey(entry.scriptBn), entry)
            entry.aliases.forEach { alias ->
                val norm = normalizeKey(alias)
                if (norm.isNotEmpty()) {
                    putIfAbsent(norm, entry)
                }
            }
        }
    }

    val totalEntries: Int get() = entries.size

    fun entryById(clipId: String): BanglaAudioEntry? = byId[clipId]

    fun assetForClipId(clipId: String): String? = byId[clipId]?.assetPath

    fun assetForLesson(lessonId: String): String? = byId[lessonId]?.assetPath

    fun assetForNumber(number: Int): String? {
        if (number !in 0..100) return null
        return byId["num_$number"]?.assetPath ?: "audio/bangla/numbers/number_$number.mp3"
    }

    fun assetsForPoem(poemId: String, stanzaCount: Int): List<String> {
        val list = mutableListOf<String>()
        byId["${poemId}_title"]?.assetPath?.let { list.add(it) }
        for (i in 0 until stanzaCount) {
            byId["${poemId}_stanza_$i"]?.assetPath?.let { list.add(it) }
        }
        return list
    }

    fun assetForPoemStanza(poemId: String, stanzaIndex: Int): String? =
        byId["${poemId}_stanza_$stanzaIndex"]?.assetPath

    fun assetForStoryPage(storyId: String, pageNumber: Int): String? =
        byId["${storyId}_page_$pageNumber"]?.assetPath

    fun assetForStoryMoral(storyId: String): String? =
        byId["${storyId}_moral"]?.assetPath

    fun assetForInstruction(instructionId: String): String? =
        byId[instructionId]?.assetPath

    /**
     * Resolves a Bangla text string (from quizzes, visual math problems, memory cards,
     * sorting/pattern puzzles, or greetings) into 1 or more bundled Bangla MP3 asset paths.
     * Never falls back to Android System TTS.
     */
    fun resolveAssetsForBanglaText(rawText: String): List<String> {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) return emptyList()

        // 1. Direct normalized alias match (handles exact phrases, story pages, stanzas, puzzle tokens, greetings)
        val norm = normalizeKey(trimmed)
        byNormalizedAlias[norm]?.let { return listOf(it.assetPath) }

        // 2. Numeric string in Bangla (০–১০০) or ASCII digits (0–100)
        parseBanglaOrAsciiInt(trimmed)?.let { n ->
            assetForNumber(n)?.let { return listOf(it) }
        }

        // 3. Picture Quiz prompt prefix: "খুঁজে বের করো <target>"
        val quizPrefixes = listOf("খুঁজে বের করো:", "খুঁজে বের করো")
        for (prefix in quizPrefixes) {
            if (trimmed.startsWith(prefix)) {
                val rest = trimmed.removePrefix(prefix).trim()
                val findAsset = assetForInstruction(INST_QUIZ_FIND)
                val targetAssets = resolveAssetsForBanglaText(rest)
                if (targetAssets.isNotEmpty()) {
                    return buildList {
                        if (findAsset != null) add(findAsset)
                        addAll(targetAssets)
                    }
                }
            }
        }

        // 4. Visual Math Lab spoken equation: "<left> যোগ <right> সমান কত?" or "<left> বিয়োগ <right> সমান কত?"
        val mathMatch = MATH_EQUATION_REGEX.find(trimmed)
        if (mathMatch != null) {
            val leftNum = parseBanglaOrAsciiInt(mathMatch.groupValues[1])
            val opWord = mathMatch.groupValues[2]
            val rightNum = parseBanglaOrAsciiInt(mathMatch.groupValues[3])
            if (leftNum != null && rightNum != null) {
                val leftAsset = assetForNumber(leftNum)
                val opAsset = assetForInstruction(
                    if (opWord == "যোগ") INST_MATH_PLUS else INST_MATH_MINUS
                )
                val rightAsset = assetForNumber(rightNum)
                val eqAsset = assetForInstruction(INST_MATH_EQUALS_WHAT)
                if (leftAsset != null && opAsset != null && rightAsset != null && eqAsset != null) {
                    return listOf(leftAsset, opAsset, rightAsset, eqAsset)
                }
            }
        }

        // 5. Fallback: match first sentence/segment before punctuation (e.g., "আম। মিষ্টি...")
        val firstClause = trimmed.substringBefore('।').substringBefore('—').substringBefore('(').trim()
        if (firstClause.isNotEmpty() && firstClause != trimmed) {
            byNormalizedAlias[normalizeKey(firstClause)]?.let { return listOf(it.assetPath) }
        }

        return emptyList()
    }

    /**
     * Audits that every manifest asset file physically exists in the APK's bundled assets.
     */
    fun auditBundledAssets(assetManager: AssetManager): BundledAudioAudit {
        var found = 0
        val missing = mutableListOf<String>()
        for (entry in entries) {
            val exists = try {
                assetManager.openFd(entry.assetPath).use { afd ->
                    afd.length >= MIN_VALID_AUDIO_BYTES
                }
            } catch (_: Exception) {
                // Fallback stream open check in case asset was compressed
                try {
                    assetManager.open(entry.assetPath).use { stream ->
                        stream.available() > 0 || stream.read() != -1
                    }
                } catch (_: Exception) {
                    false
                }
            }
            if (exists) {
                found++
            } else {
                missing.add(entry.assetPath)
            }
        }
        return BundledAudioAudit(
            verifiedCount = found,
            totalCount = entries.size,
            missingAssetPaths = missing
        )
    }

    companion object {
        private const val TAG = "BanglaAudioManifest"
        const val MANIFEST_ASSET_PATH = "audio/bangla/manifest.json"
        private const val MIN_VALID_AUDIO_BYTES = 1000L

        const val INST_WELCOME = "inst_welcome"
        const val INST_TEST_VOICE = "inst_test_voice"
        const val INST_TAP_TO_HEAR = "inst_tap_to_hear"
        const val INST_GREAT_JOB = "inst_great_job"
        const val INST_TRY_AGAIN = "inst_try_again"
        const val INST_JIGSAW_SOLVED = "inst_jigsaw_solved"
        const val INST_SORTING_PROMPT = "inst_sorting_prompt"
        const val INST_PATTERN_PROMPT = "inst_pattern_prompt"
        const val INST_QUIZ_FIND = "inst_quiz_find"
        const val INST_MATH_PLUS = "inst_math_plus"
        const val INST_MATH_MINUS = "inst_math_minus"
        const val INST_MATH_EQUALS_WHAT = "inst_math_equals_what"

        private val MATH_EQUATION_REGEX = Regex(
            """([0-9০-৯]+)\s+(যোগ|বিয়োগ)\s+([0-9০-৯]+)"""
        )

        fun loadFromAssets(assetManager: AssetManager): BanglaAudioManifest {
            return try {
                assetManager.open(MANIFEST_ASSET_PATH).use { parseJsonStream(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load $MANIFEST_ASSET_PATH: ${e.message}")
                BanglaAudioManifest(emptyList())
            }
        }

        fun parseJsonStream(inputStream: InputStream): BanglaAudioManifest {
            val jsonText = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            return parseJsonString(jsonText)
        }

        fun parseJsonString(jsonText: String): BanglaAudioManifest {
            val root = SimpleJsonParser(jsonText).parseValue() as? Map<*, *>
                ?: return BanglaAudioManifest(emptyList())
            val entriesRaw = root["entries"] as? List<*> ?: return BanglaAudioManifest(emptyList())
            val list = ArrayList<BanglaAudioEntry>(entriesRaw.size)
            for (item in entriesRaw) {
                val obj = item as? Map<*, *> ?: continue
                val id = obj["id"] as? String ?: continue
                val category = obj["category"] as? String ?: continue
                val assetPath = obj["assetPath"] as? String ?: continue
                val scriptBn = obj["scriptBn"] as? String ?: continue
                val aliasesRaw = obj["aliases"] as? List<*>
                val aliases = aliasesRaw?.mapNotNull { (it as? String)?.takeIf { s -> s.isNotBlank() } }
                    ?: emptyList()
                list.add(
                    BanglaAudioEntry(
                        id = id,
                        category = category,
                        assetPath = assetPath,
                        scriptBn = scriptBn,
                        aliases = aliases
                    )
                )
            }
            return BanglaAudioManifest(list)
        }

        private class SimpleJsonParser(private val src: String) {
            private var pos = 0

            fun parseValue(): Any? {
                skipWhitespace()
                if (pos >= src.length) return null
                return when (src[pos]) {
                    '{' -> parseObject()
                    '[' -> parseArray()
                    '"' -> parseString()
                    't' -> { pos += 4; true }
                    'f' -> { pos += 5; false }
                    'n' -> { pos += 4; null }
                    else -> parseNumber()
                }
            }

            private fun parseObject(): Map<String, Any?> {
                val map = LinkedHashMap<String, Any?>()
                pos++ // '{'
                skipWhitespace()
                if (pos < src.length && src[pos] == '}') {
                    pos++
                    return map
                }
                while (pos < src.length) {
                    skipWhitespace()
                    val key = parseString()
                    skipWhitespace()
                    if (pos < src.length && src[pos] == ':') pos++
                    val value = parseValue()
                    map[key] = value
                    skipWhitespace()
                    if (pos < src.length && src[pos] == ',') {
                        pos++
                    } else {
                        if (pos < src.length && src[pos] == '}') pos++
                        break
                    }
                }
                return map
            }

            private fun parseArray(): List<Any?> {
                val list = ArrayList<Any?>()
                pos++ // '['
                skipWhitespace()
                if (pos < src.length && src[pos] == ']') {
                    pos++
                    return list
                }
                while (pos < src.length) {
                    list.add(parseValue())
                    skipWhitespace()
                    if (pos < src.length && src[pos] == ',') {
                        pos++
                    } else {
                        if (pos < src.length && src[pos] == ']') pos++
                        break
                    }
                }
                return list
            }

            private fun parseString(): String {
                pos++ // opening '"'
                val sb = StringBuilder()
                while (pos < src.length) {
                    val c = src[pos++]
                    if (c == '"') break
                    if (c == '\\' && pos < src.length) {
                        when (val esc = src[pos++]) {
                            '"', '\\', '/' -> sb.append(esc)
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (pos + 4 <= src.length) {
                                    val hex = src.substring(pos, pos + 4)
                                    pos += 4
                                    hex.toIntOrNull(16)?.let { sb.append(it.toChar()) }
                                }
                            }
                            else -> sb.append(esc)
                        }
                    } else {
                        sb.append(c)
                    }
                }
                return sb.toString()
            }

            private fun parseNumber(): Number {
                val start = pos
                while (pos < src.length && src[pos] in "-+0123456789.eE") pos++
                val token = src.substring(start, pos)
                return token.toLongOrNull() ?: token.toDoubleOrNull() ?: 0L
            }

            private fun skipWhitespace() {
                while (pos < src.length && src[pos] <= ' ') pos++
            }
        }

        private fun normalizeKey(raw: String): String {
            return raw
                .replace("\n", " ")
                .replace("।", " ")
                .replace(",", " ")
                .replace("!", " ")
                .replace("?", " ")
                .replace(":", " ")
                .replace("—", " ")
                .replace("–", " ")
                .replace("'", "")
                .replace("’", "")
                .replace(Regex("\\s+"), " ")
                .trim()
                .lowercase()
        }

        private fun parseBanglaOrAsciiInt(raw: String): Int? {
            val cleaned = raw.trim().trimEnd('।', '.', '!', '?')
            if (cleaned.isEmpty()) return null
            val sb = StringBuilder(cleaned.length)
            for (ch in cleaned) {
                when (ch) {
                    in '0'..'9' -> sb.append(ch)
                    in '০'..'৯' -> sb.append(('0'.code + (ch.code - '০'.code)).toChar())
                    else -> return null
                }
            }
            return sb.toString().toIntOrNull()
        }
    }
}
