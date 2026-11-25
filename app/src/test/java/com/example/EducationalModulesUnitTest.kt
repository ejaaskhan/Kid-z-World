package com.example

import com.example.core.audio.BanglaAudioManifest
import com.example.core.localization.AppLanguage
import com.example.core.localization.toLocalizedDigits
import com.example.core.util.ExternalLinkOpener
import com.example.data.local.BanglaPoemsCatalog
import com.example.data.local.SeedContentProvider
import com.example.domain.engine.MathOperation
import com.example.domain.engine.MathProblemGenerator
import com.example.domain.model.AgeDifficulty
import com.example.domain.model.AppSettings
import com.example.domain.model.DailyStreak
import com.example.domain.model.LearningPillar
import com.example.domain.model.ModuleType
import com.example.domain.model.ThemeMode
import com.example.ui.components.PatternPuzzleGenerator
import com.example.ui.components.SortingCategory
import com.example.ui.components.SortingPuzzleCatalog
import com.example.ui.components.generateShuffledJigsawBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class EducationalModulesUnitTest {

    @Test
    fun `every module category has real non-empty bilingual lessons and unique ids`() {
        val allLessons = SeedContentProvider.buildAllLessons()
        assertTrue("Expected rich lesson catalog", allLessons.size >= 240)

        // All IDs must be strictly unique
        val distinctIds = allLessons.map { it.id }.toSet()
        assertEquals("Lesson IDs must be unique", allLessons.size, distinctIds.size)

        // Every single ModuleType must be populated with real non-empty bilingual content
        ModuleType.entries.forEach { moduleType ->
            val items = allLessons.filter { it.moduleType == moduleType.key }
            assertTrue("Category ${moduleType.key} must not be empty", items.isNotEmpty())
            items.forEach { lesson ->
                assertTrue("titleEn blank for ${lesson.id}", lesson.titleEn.isNotBlank())
                assertTrue("titleBn blank for ${lesson.id}", lesson.titleBn.isNotBlank())
                assertTrue("exampleEn blank for ${lesson.id}", lesson.exampleEn.isNotBlank())
                assertTrue("exampleBn blank for ${lesson.id}", lesson.exampleBn.isNotBlank())
                assertTrue("visualBadge blank for ${lesson.id}", lesson.visualBadge.isNotBlank())
            }
        }
    }

    @Test
    fun `module 1 alphabet covers English A-Z and Bangla অ-ঔ completely`() {
        val allLessons = SeedContentProvider.buildAllLessons()
        val enLetters = allLessons.filter { it.moduleType == ModuleType.ALPHABET_EN.key }
        assertEquals(26, enLetters.size)
        assertEquals("A", enLetters.first().symbol)
        assertEquals("Z", enLetters.last().symbol)

        val bnLetters = allLessons.filter { it.moduleType == ModuleType.ALPHABET_BN.key }
        val expectedVowels = listOf("অ", "আ", "ই", "ঈ", "উ", "ঊ", "ঋ", "এ", "ঐ", "ও", "ঔ")
        val bnSymbols = bnLetters.map { it.symbolBn }
        expectedVowels.forEach { vowel ->
            assertTrue("Missing Bangla vowel $vowel", vowel in bnSymbols)
        }
    }

    @Test
    fun `module 2 numbers covers complete 1 to 100 in English and Bangla`() {
        val numbers = SeedContentProvider.buildAllLessons()
            .filter { it.moduleType == ModuleType.NUMBERS.key }
            .sortedBy { it.countValue }

        assertEquals(100, numbers.size)
        assertEquals(1, numbers.first().countValue)
        assertEquals(100, numbers.last().countValue)
        assertEquals("One", numbers.first().titleEn)
        assertEquals("এক", numbers.first().titleBn)
        assertEquals("১", numbers.first().symbolBn)
        assertEquals("One Hundred", numbers.last().titleEn)
        assertEquals("একশো", numbers.last().titleBn)
        assertEquals("১০০", numbers.last().symbolBn)

        // Verify continuous 1..100 coverage and exact Bangla digit mapping
        numbers.forEachIndexed { idx, item ->
            val expectedValue = idx + 1
            assertEquals(expectedValue, item.countValue)
            assertEquals(expectedValue.toString(), item.symbol)
            assertEquals(expectedValue.toLocalizedDigits(AppLanguage.BANGLA), item.symbolBn)
        }
    }

    @Test
    fun `module 3 world exploration and module 4 language have all sub-decks populated`() {
        val allLessons = SeedContentProvider.buildAllLessons()
        LearningPillar.WORLD.moduleTypes().forEach { type ->
            val count = allLessons.count { it.moduleType == type.key }
            assertTrue("World category ${type.key} should have at least 8 lessons, found $count", count >= 8)
        }
        LearningPillar.LANGUAGE.moduleTypes().forEach { type ->
            val count = allLessons.count { it.moduleType == type.key }
            assertTrue("Language category ${type.key} should have at least 8 lessons, found $count", count >= 8)
        }
    }

    @Test
    fun `age difficulty auto-matches ages 3-4, 5-6, and 7-8 accurately`() {
        assertEquals(AgeDifficulty.STARTER, AgeDifficulty.fromAge(3))
        assertEquals(AgeDifficulty.STARTER, AgeDifficulty.fromAge(4))
        assertEquals(AgeDifficulty.EXPLORER, AgeDifficulty.fromAge(5))
        assertEquals(AgeDifficulty.EXPLORER, AgeDifficulty.fromAge(6))
        assertEquals(AgeDifficulty.SCHOLAR, AgeDifficulty.fromAge(7))
        assertEquals(AgeDifficulty.SCHOLAR, AgeDifficulty.fromAge(8))
    }

    @Test
    fun `math problem generator enforces non-negative results and 4 distinct options across all difficulties`() {
        val rng = Random(42)
        AgeDifficulty.entries.forEach { diff ->
            MathOperation.entries.forEach { op ->
                repeat(40) {
                    val problem = MathProblemGenerator.generate(diff, op, rng)
                    assertTrue("Answer must be non-negative", problem.correctAnswer >= 0)
                    assertEquals("Must provide 4 options", 4, problem.options.size)
                    assertEquals("Options must be distinct", 4, problem.options.toSet().size)
                    assertTrue("Correct answer must be in options", problem.correctAnswer in problem.options)
                    if (op == MathOperation.ADDITION) {
                        assertTrue("Addition left operand bound", problem.left in 1..diff.maxMathOperand)
                        assertTrue("Addition right operand bound", problem.right in 1..diff.maxMathOperand)
                        assertEquals(problem.left + problem.right, problem.correctAnswer)
                    } else {
                        val maxLeft = (diff.maxMathOperand * 2).coerceAtMost(99)
                        assertTrue("Subtraction left operand bound", problem.left in 2..maxLeft)
                        assertTrue("Subtraction right operand bound", problem.right in 1..problem.left)
                        assertEquals(problem.left - problem.right, problem.correctAnswer)
                    }
                    assertTrue(problem.formattedEquation(AppLanguage.ENGLISH).contains("="))
                    assertTrue(problem.formattedEquation(AppLanguage.BANGLA).contains("="))
                }
            }
        }
    }

    @Test
    fun `daily streak logic rewards consistency and caps bonus at 25 stars`() {
        val today = 20_000L
        val activeStreak = DailyStreak(
            currentStreak = 3,
            bestStreak = 3,
            lastActiveEpochDay = today,
            lastRewardClaimedEpochDay = today - 1
        )
        assertTrue(activeStreak.isActiveToday(today))
        assertTrue(activeStreak.canClaimDailyBonus(today))
        assertEquals(15, activeStreak.dailyBonusStars(today))

        val claimedStreak = activeStreak.copy(lastRewardClaimedEpochDay = today)
        assertFalse(claimedStreak.canClaimDailyBonus(today))
        assertTrue(claimedStreak.isBonusClaimedToday(today))

        val highStreak = DailyStreak(currentStreak = 10, bestStreak = 10, lastActiveEpochDay = today)
        assertEquals(25, highStreak.dailyBonusStars(today))

        val expiredStreak = DailyStreak(currentStreak = 4, bestStreak = 4, lastActiveEpochDay = today - 2)
        assertEquals(0, expiredStreak.effectiveStreak(today))
        assertFalse(expiredStreak.isActiveToday(today))
    }

    @Test
    fun `all illustrated stories have complete bilingual titles pages and morals`() {
        val (stories, pages) = SeedContentProvider.buildStories()
        assertTrue("Expected at least 3 stories", stories.size >= 3)

        stories.forEach { story ->
            assertTrue("Missing English title for ${story.id}", story.titleEn.isNotBlank())
            assertTrue("Missing Bangla title for ${story.id}", story.titleBn.isNotBlank())
            assertTrue("Missing English moral for ${story.id}", story.moralEn.isNotBlank())
            assertTrue("Missing Bangla moral for ${story.id}", story.moralBn.isNotBlank())
            val storyPages = pages.filter { it.storyId == story.id }
            assertTrue("Story ${story.id} must have multiple pages", storyPages.size >= 3)
            storyPages.forEach { page ->
                assertTrue("Blank English page text in ${story.id}", page.textEn.isNotBlank())
                assertTrue("Blank Bangla page text in ${story.id}", page.textBn.isNotBlank())
            }
        }
    }

    @Test
    fun `english and bangla localization strings and numerals are completely populated`() {
        val en = com.example.core.localization.stringsFor(AppLanguage.ENGLISH)
        val bn = com.example.core.localization.stringsFor(AppLanguage.BANGLA)

        assertEquals("KID'Z WORLD", en.brandTitle)
        assertTrue("English developer credit must include Dev. E", en.developerCredit.contains("Dev. E"))
        assertTrue("Bangla developer credit must include Dev. E", bn.developerCredit.contains("Dev. E"))
        assertTrue(en.alphabetAdventureTitle.isNotBlank())
        assertTrue(bn.alphabetAdventureTitle.isNotBlank())
        assertTrue(en.continueLearningTitle.isNotBlank())
        assertTrue(bn.continueLearningTitle.isNotBlank())
        assertTrue(en.dailyChallengeTitle.isNotBlank())
        assertTrue(bn.dailyChallengeTitle.isNotBlank())
        assertEquals("১২৩৪৫৬৭৮৯০", 1234567890.toLocalizedDigits(AppLanguage.BANGLA))
        assertEquals("1234567890", 1234567890.toLocalizedDigits(AppLanguage.ENGLISH))
        assertEquals(AppLanguage.BANGLA, AppSettings().language)
        assertEquals(AppLanguage.BANGLA, AppLanguage.fromCode(null))
        assertTrue(en.poemsShelfTitle.isNotBlank())
        assertTrue(bn.poemsShelfTitle.isNotBlank())
    }

    @Test
    fun `bangla poems catalog provides 8 unique classic poems with rhythmic stanzas`() {
        val poems = BanglaPoemsCatalog.poems
        assertEquals("Expected 8 classic Bangla children's poems", 8, poems.size)
        assertEquals("Poem IDs must be unique", poems.size, poems.map { it.id }.toSet().size)

        poems.forEach { poem ->
            assertTrue("Missing Bangla title for ${poem.id}", poem.titleBn.isNotBlank())
            assertTrue("Missing English title for ${poem.id}", poem.titleEn.isNotBlank())
            assertTrue("Missing emoji for ${poem.id}", poem.emoji.isNotBlank())
            assertTrue("Poem ${poem.id} must have at least 3 stanzas", poem.stanzasBn.size >= 3)
            poem.stanzasBn.forEach { stanza ->
                assertTrue("Blank stanza in ${poem.id}", stanza.isNotBlank())
            }
            assertTrue("Missing meaning summary for ${poem.id}", poem.meaningEn.isNotBlank())
        }
    }

    @Test
    fun `new child-friendly puzzle generators produce valid playable puzzles`() {
        // 1. Picture Jigsaw Puzzle (2x2 and 3x3 never start solved and retain exact tiles)
        listOf(4, 9).forEach { tileCount ->
            repeat(20) {
                val board = generateShuffledJigsawBoard(tileCount)
                assertEquals(tileCount, board.size)
                assertEquals((0 until tileCount).toList(), board.sorted())
                assertFalse("Board of $tileCount must not start already solved", board == (0 until tileCount).toList())
            }
        }

        // 2. Animal & Fruit Sorting Puzzle covers all 3 baskets with bilingual labels
        assertTrue("Expected at least 15 sorting items", SortingPuzzleCatalog.items.size >= 15)
        SortingCategory.entries.forEach { cat ->
            val count = SortingPuzzleCatalog.items.count { it.category == cat }
            assertTrue("Category $cat must have at least 5 items", count >= 5)
        }
        SortingPuzzleCatalog.items.forEach { item ->
            assertTrue(item.nameBn.isNotBlank())
            assertTrue(item.nameEn.isNotBlank())
            assertTrue(item.emoji.isNotBlank())
        }

        // 3. Pattern & Sequence Puzzle produces 5 rounds with valid choices containing the answer
        val rounds = PatternPuzzleGenerator.generateRounds(count = 5)
        assertEquals(5, rounds.size)
        rounds.forEach { round ->
            assertTrue("Sequence must have at least 4 tokens", round.sequence.size >= 4)
            assertEquals("Each round must offer 3 choices", 3, round.choices.size)
            assertEquals("Choices must be distinct", 3, round.choices.map { it.emoji }.toSet().size)
            assertTrue("Answer must be in choices", round.choices.any { it.emoji == round.answer.emoji })
        }
    }

    private fun resolveAssetsDir(): File {
        val candidates = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        )
        return candidates.firstOrNull { it.isDirectory }
            ?: error("Could not locate app/src/main/assets from ${File(".").absolutePath}")
    }

    private fun assertValidMp3Asset(assetsDir: File, assetPath: String, context: String) {
        val file = File(assetsDir, assetPath)
        assertTrue("Missing bundled MP3 for $context at ${file.path}", file.isFile)
        assertTrue(
            "Bundled MP3 too small (${file.length()} bytes) for $context at ${file.path}",
            file.length() >= 1200L
        )
        val header = file.inputStream().use { stream ->
            val buf = ByteArray(3)
            stream.read(buf)
            buf
        }
        val isId3 = header[0] == 'I'.code.toByte() && header[1] == 'D'.code.toByte() && header[2] == '3'.code.toByte()
        val isMpegSync = (header[0].toInt() and 0xFF) == 0xFF && (header[1].toInt() and 0xE0) == 0xE0
        assertTrue("Invalid MP3 header for $context at $assetPath", isId3 || isMpegSync)
    }

    @Test
    fun `bundled offline bangla audio manifest covers every lesson number poem story and puzzle with real mp3s`() {
        val assetsDir = resolveAssetsDir()
        val manifestFile = File(assetsDir, BanglaAudioManifest.MANIFEST_ASSET_PATH)
        assertTrue("manifest.json must exist at ${manifestFile.path}", manifestFile.isFile)

        val manifest = manifestFile.inputStream().use { BanglaAudioManifest.parseJsonStream(it) }
        assertTrue("Expected at least 330 bundled Bangla clips, found ${manifest.totalEntries}", manifest.totalEntries >= 330)

        // 1. Every manifest entry points to a real, non-empty, valid MP3 file
        manifest.entries.forEach { entry ->
            assertValidMp3Asset(assetsDir, entry.assetPath, "manifest entry ${entry.id}")
        }

        // 2. Every single Room lesson (all 247 lessons across alphabets, numbers, world, language) has an exact MP3
        val allLessons = SeedContentProvider.buildAllLessons()
        allLessons.forEach { lesson ->
            val path = manifest.assetForLesson(lesson.id)
            assertNotNull("Missing bundled Bangla audio for lesson ${lesson.id}", path)
            assertValidMp3Asset(assetsDir, path!!, "lesson ${lesson.id}")
        }

        // 3. Numbers 0 to 100 all resolve to real MP3 files
        for (n in 0..100) {
            val path = manifest.assetForNumber(n)
            assertNotNull("Missing bundled Bangla audio for number $n", path)
            assertValidMp3Asset(assetsDir, path!!, "number $n")
        }

        // 4. Every Bangla poem title and every individual stanza resolves to a real MP3 file
        BanglaPoemsCatalog.poems.forEach { poem ->
            val fullSequence = manifest.assetsForPoem(poem.id, poem.stanzasBn.size)
            assertEquals(
                "Expected title + ${poem.stanzasBn.size} stanzas for ${poem.id}",
                poem.stanzasBn.size + 1,
                fullSequence.size
            )
            fullSequence.forEach { assetPath ->
                assertValidMp3Asset(assetsDir, assetPath, "poem ${poem.id}")
            }
            poem.stanzasBn.indices.forEach { idx ->
                val stanzaAsset = manifest.assetForPoemStanza(poem.id, idx)
                assertNotNull("Missing stanza $idx audio for ${poem.id}", stanzaAsset)
                assertValidMp3Asset(assetsDir, stanzaAsset!!, "poem ${poem.id} stanza $idx")
            }
        }

        // 5. Every story page and moral resolves to a real MP3 file
        val (stories, pages) = SeedContentProvider.buildStories()
        stories.forEach { story ->
            val moralAsset = manifest.assetForStoryMoral(story.id)
            assertNotNull("Missing moral audio for ${story.id}", moralAsset)
            assertValidMp3Asset(assetsDir, moralAsset!!, "story ${story.id} moral")

            val storyPages = pages.filter { it.storyId == story.id }
            storyPages.forEach { page ->
                val pageAsset = manifest.assetForStoryPage(story.id, page.pageNumber)
                assertNotNull("Missing page ${page.pageNumber} audio for ${story.id}", pageAsset)
                assertValidMp3Asset(assetsDir, pageAsset!!, "story ${story.id} page ${page.pageNumber}")
            }
        }

        // 6. Dynamic Bangla text resolution (Visual Math, Picture Quiz, Sorting & Pattern Puzzles, Instructions)
        val addSeq = manifest.resolveAssetsForBanglaText("৩ যোগ ২ সমান কত?")
        assertEquals("Math addition equation must map to 4 bundled clips", 4, addSeq.size)
        addSeq.forEach { assertValidMp3Asset(assetsDir, it, "math add $it") }

        val subSeq = manifest.resolveAssetsForBanglaText("১০ বিয়োগ ৪ সমান কত?")
        assertEquals("Math subtraction equation must map to 4 bundled clips", 4, subSeq.size)
        subSeq.forEach { assertValidMp3Asset(assetsDir, it, "math sub $it") }

        val quizSeq = manifest.resolveAssetsForBanglaText("খুঁজে বের করো আম")
        assertEquals("Picture quiz prompt must map to find + target clips", 2, quizSeq.size)
        quizSeq.forEach { assertValidMp3Asset(assetsDir, it, "quiz $it") }

        SortingPuzzleCatalog.items.forEach { item ->
            val resolved = manifest.resolveAssetsForBanglaText(item.nameBn)
            assertTrue("Sorting item '${item.nameBn}' must resolve to bundled MP3", resolved.isNotEmpty())
            resolved.forEach { assertValidMp3Asset(assetsDir, it, "sorting ${item.nameBn}") }
        }

        val patternRounds = PatternPuzzleGenerator.generateRounds(count = 5)
        patternRounds.forEach { round ->
            (round.sequence + round.choices).forEach { token ->
                val resolved = manifest.resolveAssetsForBanglaText(token.labelBn)
                assertTrue("Pattern token '${token.labelBn}' must resolve to bundled MP3", resolved.isNotEmpty())
                resolved.forEach { assertValidMp3Asset(assetsDir, it, "pattern ${token.labelBn}") }
            }
        }

        assertTrue(
            "Pattern prompt must resolve to bundled MP3",
            manifest.resolveAssetsForBanglaText("এর পরে কী আসবে?").isNotEmpty()
        )
    }

    @Test
    fun `theme mode resolves light dark and system default accurately and keeps ui sound independent`() {
        assertFalse(ThemeMode.LIGHT.resolveDarkTheme(isSystemDark = false))
        assertFalse(ThemeMode.LIGHT.resolveDarkTheme(isSystemDark = true))
        assertTrue(ThemeMode.DARK.resolveDarkTheme(isSystemDark = false))
        assertTrue(ThemeMode.DARK.resolveDarkTheme(isSystemDark = true))
        assertFalse(ThemeMode.SYSTEM.resolveDarkTheme(isSystemDark = false))
        assertTrue(ThemeMode.SYSTEM.resolveDarkTheme(isSystemDark = true))

        assertEquals(ThemeMode.LIGHT, ThemeMode.fromKey("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromKey("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey("system"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromKey("unknown"))

        // UI sound FX toggle must remain independent from educational voice narration
        val mutedUiOnly = AppSettings(soundFxEnabled = false, ttsEnabled = true, themeMode = ThemeMode.DARK)
        assertFalse(mutedUiOnly.soundFxEnabled)
        assertTrue(mutedUiOnly.ttsEnabled)
        assertTrue(mutedUiOnly.darkModeEnabled)

        val en = com.example.core.localization.stringsFor(AppLanguage.ENGLISH)
        val bn = com.example.core.localization.stringsFor(AppLanguage.BANGLA)
        assertTrue(en.settingsTitle.isNotBlank() && bn.settingsTitle.isNotBlank())
        assertTrue(en.themeModeSystem.isNotBlank() && bn.themeModeSystem.isNotBlank())
        assertTrue(en.developerWebsiteLabel.isNotBlank() && bn.developerWebsiteLabel.isNotBlank())
        assertTrue(en.openWebsiteError.isNotBlank() && bn.openWebsiteError.isNotBlank())
    }

    @Test
    fun `developer website link constant and url validator match official Dev E url`() {
        assertEquals("Dev. E", ExternalLinkOpener.DEVELOPER_NAME)
        assertEquals("https://ebrahimjamansumon.blogspot.com", ExternalLinkOpener.DEVELOPER_WEBSITE_URL)
        assertTrue(ExternalLinkOpener.isValidWebUrl(ExternalLinkOpener.DEVELOPER_WEBSITE_URL))
        assertFalse(ExternalLinkOpener.isValidWebUrl(""))
        assertFalse(ExternalLinkOpener.isValidWebUrl("javascript:alert(1)"))
    }

    @Test
    fun `bundled offline developer photo drawable exists and app logo remains distinct`() {
        val drawableCandidates = listOf(
            File("src/main/res/drawable/img_dev_e_photo.webp"),
            File("app/src/main/res/drawable/img_dev_e_photo.webp")
        )
        val photoFile = drawableCandidates.firstOrNull { it.isFile }
        assertNotNull("Bundled developer photo drawable must exist for offline APK packaging", photoFile)
        val bytes = photoFile!!.readBytes()
        assertTrue("Developer photo must be non-empty", bytes.size > 10_000)
        assertEquals("RIFF", String(bytes.copyOfRange(0, 4)))
        assertEquals("WEBP", String(bytes.copyOfRange(8, 12)))
        assertTrue(
            "Developer photo resource must remain distinct from KID'Z WORLD app logo",
            com.example.core.assets.LocalAssetRegistry.developerPhoto !=
                com.example.core.assets.LocalAssetRegistry.appCrestArtwork
        )
    }
}
