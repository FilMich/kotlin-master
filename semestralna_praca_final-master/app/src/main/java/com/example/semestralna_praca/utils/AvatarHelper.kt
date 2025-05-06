package com.example.semestralna_praca.utils

object AvatarHelper {
    fun getPlayerAvatar(level: Int): String {
        return when (level) {
            in 1..9 -> "🧑"     // bežný človek
            in 10..19 -> "🥷"   // assassin
            in 20..29 -> "🧝‍♂️" // elf
            in 30..39 -> "🧛‍♂️" // upír / temný lord
            in 40..49 -> "🧙‍♂️" // mág
            50 -> "👑"          // kráľ
            else -> "❓"
        }
    }
}
