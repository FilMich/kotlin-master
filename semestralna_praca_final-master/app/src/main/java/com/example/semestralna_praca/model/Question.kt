package com.example.semestralna_praca.model

data class Question(
    val id: Int,
    val category: String,
    val text: String,
    val options: List<String>,
    val scores: List<Int>
)