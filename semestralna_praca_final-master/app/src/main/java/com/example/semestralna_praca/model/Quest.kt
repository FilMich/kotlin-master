package com.example.semestralna_praca.model

data class Quest(
    val id: Int,
    val category: String,
    val baseDescription: String,
    val baseValue: Int,
    val scalingStep: Int,
    val increment: Int,
    val baseXP: Int,
    val frequency: String
)

data class QuestDisplay(
    val id: Int,
    val description: String,
    val xp: Int,
    val frequency: String,
    val category: String
)