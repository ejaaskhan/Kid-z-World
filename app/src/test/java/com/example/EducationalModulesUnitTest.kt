package com.example

import com.example.core.localization.AppLanguage
import com.example.core.localization.toLocalizedDigits
import com.example.data.local.SeedContentProvider
import com.example.domain.engine.MathOperation
import com.example.domain.engine.MathProblemGenerator
import com.example.domain.model.AgeDifficulty
import com.example.domain.model.DailyStreak
import com.example.domain.model.LearningPillar
import com.example.domain.model.ModuleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
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
    }
}

