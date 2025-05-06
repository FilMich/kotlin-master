package com.example.semestralna_praca.model

data class QuestEntity(
    val id: String,
    val category: String,
    val title: String,
    val done: Boolean,
    val xpReward: Int,
    val frequency: String
)