package com.example.semestralna_praca.utils

import com.example.semestralna_praca.model.Quest
import com.example.semestralna_praca.model.QuestDisplay

object QuestUtils {

    fun getQuestForLevel(quest: Quest, level: Int, frequency: String): QuestDisplay {
        val scalingMultiplier = when (frequency) {
            "weekly" -> 3
            "monthly" -> 12
            else -> 1
        }

        // 👉 Ak ide o weekly alebo monthly quest, vynútim aspoň 1 krok škálovania
        val baseSteps = level / quest.scalingStep
        val steps = if (frequency != "daily") baseSteps.coerceAtLeast(1) else baseSteps

        val finalValue = quest.baseValue + (steps * quest.increment) * scalingMultiplier
        val finalXP = quest.baseXP * scalingMultiplier

        val description = quest.baseDescription.replace("{x}", finalValue.toString())

        return QuestDisplay(
            id = quest.id,
            description = description,
            xp = finalXP,
            frequency = frequency,
            category = quest.category
        )
    }
}
