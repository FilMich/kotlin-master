package com.example.semestralna_praca.viewmodel

import androidx.lifecycle.ViewModel
import com.example.semestralna_praca.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AssessmentViewModel : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _answers = mutableMapOf<Int, Int>()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        _questions.value = listOf(
            Question(1, "strength", "Ako často cvičíš?", listOf("Nikdy", "Občas", "Pravidelne"), listOf(1, 3, 5)),
            Question(2, "strength", "Vieš urobiť 10 klikov?", listOf("Nie", "Možno s prestávkami", "Bez problémov"), listOf(1, 3, 5)),
            Question(3, "strength", "Ako často robíš silový tréning (váhy, kalistenika)?", listOf("Nikdy", "1× týždenne", "3× a viac"), listOf(1, 3, 5)),
            Question(4, "strength", "Ako hodnotíš svoju fyzickú kondíciu?", listOf("Slabá", "Priemerná", "Dobrá"), listOf(1, 3, 5)),
            Question(5, "strength", "Ako ďaleko dokážeš prejsť/chodiť bez pauzy?", listOf("Menej než 1 km", "2–3 km", "Viac než 5 km"), listOf(1, 3, 5)),
            Question(6, "intelligence", "Ako často čítaš knihy?", listOf("Nikdy", "Občas", "Denne"), listOf(1, 3, 5)),
            Question(7, "intelligence", "Vieš plynulo komunikovať aspoň v jednom cudzom jazyku?", listOf("Nie", "Základy", "Áno"), listOf(1, 3, 5)),
            Question(8, "intelligence", "Ako často sa učíš nové veci (online kurzy, články)?", listOf("Zriedkavo", "Mesačne", "Týždenne"), listOf(1, 3, 5)),
            Question(9, "intelligence", "Ako riešiš problémy?", listOf("Vzdám sa", "Skúšam rôzne prístupy", "Systematicky hľadám riešenie"), listOf(1, 3, 5)),
            Question(10, "intelligence", "Ako sa ti darilo v škole?", listOf("Zle", "Priemerne", "Výborne"), listOf(1, 3, 5)),
            Question(11, "creativity", "Venuješ sa tvorbe (hudba, kresba...)?", listOf("Vôbec", "Zriedkavo", "Často"), listOf(1, 2, 5)),
            Question(12, "creativity", "Ako často mávaš nové nápady?", listOf("Zriedkavo", "Občas", "Pravidelne"), listOf(1, 3, 5)),
            Question(13, "creativity", "Skúšaš nové veci len zo zvedavosti?", listOf("Skoro nikdy", "Občas", "Často"), listOf(1, 3, 5)),
            Question(14, "creativity", "Ako reaguješ na nové výzvy?", listOf("S odporom", "S opatrnosťou", "S nadšením"), listOf(1, 3, 5)),
            Question(15, "creativity", "Tvoríš niečo vo voľnom čase?", listOf("Nie", "Občas", "Áno, často"), listOf(1, 3, 5)),
            Question(16, "agility", "Ako často športuješ (beh, tanec, bojové umenia...)?", listOf("Nikdy", "Občas", "Pravidelne"), listOf(1, 3, 5)),
            Question(17, "agility", "Aká je tvoja koordinácia pohybov?", listOf("Slabá", "Priemerná", "Výborná"), listOf(1, 3, 5)),
            Question(18, "agility", "Ako sa ti darí v hrách alebo športoch vyžadujúcich rýchlosť a presnosť?", listOf("Zle", "Priemerne", "Výborne"), listOf(1, 3, 5)),
            Question(19, "agility", "Ako reaguješ na nečakané situácie (napr. padnutý predmet)?", listOf("Pomaly", "Niekedy zachytím", "Zachytím bez problémov"), listOf(1, 3, 5)),
            Question(20, "agility", "Máš dobrý zmysel pre rytmus a rovnováhu?", listOf("Nie", "Trochu", "Áno"), listOf(1, 2, 5)),
            Question(21, "discipline", "Dodržiavaš denné rutiny?", listOf("Nie", "Občas", "Väčšinou áno"), listOf(1, 3, 5)),
            Question(22, "discipline", "Plníš si povinnosti načas?", listOf("Skoro nikdy", "Niekedy meškám", "Vždy načas"), listOf(1, 3, 5)),
            Question(23, "discipline", "Ako často si nastavuješ ciele a pracuješ na nich?", listOf("Nikdy", "Občas", "Pravidelne"), listOf(1, 3, 5)),
            Question(24, "discipline", "Ako často sa necháš rozptýliť počas úloh?", listOf("Často", "Niekedy", "Zriedkavo"), listOf(1, 2, 5)),
            Question(25, "discipline", "Vieš sa prinútiť k činnosti, aj keď sa ti nechce?", listOf("Nie", "Zriedkavo", "Áno"), listOf(1, 2, 5)),

            // pridaj viac podľa potreby...
        )
    }

    fun answerQuestion(id: Int, score: Int) {
        _answers[id] = score
    }

    fun calculateCategoryScores(): Map<String, Int> {
        val scoresByCategory = mutableMapOf<String, MutableList<Int>>()

        for (q in _questions.value) {
            val score = _answers[q.id] ?: 0
            scoresByCategory.getOrPut(q.category) { mutableListOf() }.add(score)
        }

        return scoresByCategory.mapValues { (_, scores) ->
            scores.average().roundToInt().coerceIn(1, 5)
        }
    }

    fun saveResultsToFirestore(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = Firebase.auth.currentUser
        val scores = calculateCategoryScores()

        if (currentUser != null) {
            val db = Firebase.firestore
            val userStatsRef = db.collection("users").document(currentUser.uid)

            val scores = calculateCategoryScores() // Map<String, Int>

            val structuredStats = scores.mapValues { (_, level) ->
                mapOf("level" to level, "xp" to 0)
            }

            val data = mapOf("stats" to structuredStats)

            userStatsRef.set(data)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e) }
        } else {
            onError(Exception("No user logged in"))
        }
    }
}
