package com.example.domain.engine

import com.example.core.localization.AppLanguage
import com.example.core.localization.toLocalizedDigits
import com.example.domain.model.AgeDifficulty
import kotlin.random.Random

enum class MathOperation(val symbol: String) {
    ADDITION("+"),
    SUBTRACTION("−")
}

data class MathProblem(
    val left: Int,
    val right: Int,
    val operation: MathOperation,
    val correctAnswer: Int,
    val options: List<Int>,
    val visualEmoji: String
) {
    fun formattedEquation(language: AppLanguage): String {
        val l = left.toLocalizedDigits(language)
        val r = right.toLocalizedDigits(language)
        return "$l ${operation.symbol} $r = ?"
    }

    fun spokenPrompt(language: AppLanguage): String {
        val l = left.toLocalizedDigits(language)
        val r = right.toLocalizedDigits(language)
        return when (operation) {
            MathOperation.ADDITION -> if (language == AppLanguage.BANGLA) {
                "$l যোগ $r সমান কত?"
            } else {
                "What is $left plus $right?"
            }

            MathOperation.SUBTRACTION -> if (language == AppLanguage.BANGLA) {
                "$l বিয়োগ $r সমান কত?"
            } else {
                "What is $left minus $right?"
            }
        }
    }
}

object MathProblemGenerator {

    private val mathEmojis = listOf("🍎", "⭐", "🎈", "🍓", "🦋", "🥭")

    fun generate(
        difficulty: AgeDifficulty,
        operation: MathOperation,
        random: Random = Random.Default
    ): MathProblem {
        val maxVal = difficulty.maxMathOperand
        val (left, right, answer) = when (operation) {
            MathOperation.ADDITION -> {
                val a = random.nextInt(1, maxVal + 1)
                val b = random.nextInt(1, maxVal + 1)
                Triple(a, b, a + b)
            }

            MathOperation.SUBTRACTION -> {
                val a = random.nextInt(2, (maxVal * 2).coerceAtMost(99) + 1)
                val b = random.nextInt(1, a + 1) // Guarantees answer >= 0 for kids!
                Triple(a, b, a - b)
            }
        }

        val distractors = buildSet {
            add(answer)
            var attempts = 0
            while (size < 4 && attempts < 50) {
                attempts++
                val spread = when (difficulty) {
                    AgeDifficulty.STARTER -> 3
                    AgeDifficulty.EXPLORER -> 6
                    AgeDifficulty.SCHOLAR -> 12
                }
                val candidate = (answer + random.nextInt(-spread, spread + 1)).coerceAtLeast(0)
                if (candidate != answer) add(candidate)
            }
            var fallback = answer + 1
            while (size < 4) {
                add(fallback++)
            }
        }.toList().shuffled(random)

        return MathProblem(
            left = left,
            right = right,
            operation = operation,
            correctAnswer = answer,
            options = distractors,
            visualEmoji = mathEmojis[random.nextInt(mathEmojis.size)]
        )
    }
}
