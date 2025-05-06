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

        val steps = level / quest.scalingStep
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