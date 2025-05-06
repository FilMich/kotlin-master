package com.example.semestralna_praca.data.QuestData

import com.example.semestralna_praca.model.Quest

object QuestData {
    val allQuests = listOf(

        // 💪 Strength
        Quest(1, "strength", "Zacvič si {x} minút", 15, 10, 5, 12, "daily"),
        Quest(2, "strength", "Urob {x} drepov", 30, 10, 10, 10, "daily"),
        Quest(3, "strength", "Zdvihni ťažký objekt {x} krát", 5, 10, 3, 8, "daily"),
        Quest(4, "strength", "Urob {x} klikov", 10, 10, 5, 7, "daily"),
        Quest(5, "strength", "Vyskúšaj nový silový cvik {x} krát", 1, 10, 1, 11, "daily"),

        // 🧠 Intelligence
        Quest(6, "intelligence", "Prečítaj {x} strán knihy", 10, 10, 5, 11, "daily"),
        Quest(7, "intelligence", "Pozri si vzdelávacie video s dĺžkou {x} min", 5, 10, 5, 8, "daily"),
        Quest(8, "intelligence", "Nauč sa {x} nových slov v cudzom jazyku", 1, 10, 1, 6, "daily"),
        Quest(9, "intelligence", "Vyrieš {x} logických úloh", 1, 10, 1, 10, "daily"),
        Quest(10, "intelligence", "Zaznamenaj si {x} zaujímavé myšlienky", 1, 10, 1, 7, "daily"),

        // 🎨 Creativity
        Quest(11, "creativity", "Nakresli niečo s {x} detailmi", 3, 10, 2, 8, "daily"),
        Quest(12, "creativity", "Napíš krátky text s aspoň {x} vetami", 3, 10, 2, 10, "daily"),
        Quest(13, "creativity", "Navrhni {x} nové nápady (appky, recepty...)", 1, 10, 1, 11, "daily"),
        Quest(14, "creativity", "Zahraj na nástroj aspoň {x} minút", 10, 10, 5, 9, "daily"),
        Quest(15, "creativity", "Vyskúšaj {x} nové kreatívne aktivity", 1, 10, 1, 12, "daily"),

        // 🏃 Agility
        Quest(16, "agility", "Behaj alebo tancuj {x} minút", 10, 10, 5, 11, "daily"),
        Quest(17, "agility", "Drž rovnováhu {x} sekúnd na jednej nohe", 20, 10, 10, 6, "daily"),
        Quest(18, "agility", "Zahraj si hru s rýchlou reakciou na {x} minút", 10, 10, 5, 8, "daily"),
        Quest(19, "agility", "Urob {x} výpadov", 10, 10, 5, 9, "daily"),
        Quest(20, "agility", "Vyskúšaj koordinačné cvičenie {x} krát", 2, 10, 1, 10, "daily"),

        // 📅 Discipline
        Quest(21, "discipline", "Dodrž rannú rutinu aspoň {x} krokov", 3, 10, 1, 12, "daily"),
        Quest(22, "discipline", "Napíš si zoznam {x} úloh na dnes", 3, 10, 1, 7, "daily"),
        Quest(23, "discipline", "Dokonči nechuťovú úlohu {x} krát", 1, 10, 1, 13, "daily"),
        Quest(24, "discipline", "Pracuj sústredene {x} minút", 25, 10, 5, 10, "daily"),
        Quest(25, "discipline", "Vyhni sa rušeniu počas {x} minút", 20, 10, 5, 9, "daily"),
    )
}