package com.example.semestralna_praca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralna_praca.model.LevelData
import com.example.semestralna_praca.model.QuestEntity
import com.example.semestralna_praca.model.QuestDisplay
import com.example.semestralna_praca.data.QuestData.QuestData
import com.example.semestralna_praca.utils.QuestUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class HomeViewModel : ViewModel() {

    private val _activeQuests = MutableStateFlow<List<QuestEntity>>(emptyList())
    val activeQuests: StateFlow<List<QuestEntity>> = _activeQuests

    private val _stats = MutableStateFlow<Map<String, LevelData>>(emptyMap())
    val stats: StateFlow<Map<String, LevelData>> = _stats

    private val _completedQuests = MutableStateFlow<List<QuestEntity>>(emptyList())
    val completedQuests: StateFlow<List<QuestEntity>> = _completedQuests

    init {
        loadUserStats()
        loadActiveQuests()
        loadCompletedQuests()
    }

    fun loadUserStats() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val docRef = Firebase.firestore.collection("users").document(uid)

        viewModelScope.launch {
            docRef.get().addOnSuccessListener { document ->
                val statsMap = document.get("stats") as? Map<String, Map<String, Any>> ?: return@addOnSuccessListener
                val parsedStats = statsMap.mapValues { (_, value) ->
                    val level = (value["level"] as? Long)?.toInt() ?: 1
                    val xp = (value["xp"] as? Long)?.toInt() ?: 0
                    LevelData(level, xp)
                }
                _stats.value = parsedStats
            }
        }
    }

    fun addQuest(category: String, frequency: String, onComplete: () -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore

        val scaledQuest = generateScaledQuest(category, frequency)
        if (scaledQuest == null) {
            onComplete()
            return
        }

        val newQuest = mapOf(
            "category" to scaledQuest.category,
            "title" to scaledQuest.description,
            "done" to false,
            "xpReward" to scaledQuest.xp,
            "frequency" to scaledQuest.frequency,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(uid)
            .collection("quests").add(newQuest)
            .addOnSuccessListener { onComplete() }
    }

    private fun generateScaledQuest(category: String, frequency: String): QuestDisplay? {
        val userLevel = _stats.value[category]?.level ?: 1
        val possibleQuests = QuestData.allQuests.filter { it.category == category }
        if (possibleQuests.isEmpty()) return null

        val selected = possibleQuests.random()
        return QuestUtils.getQuestForLevel(selected, userLevel, frequency)
    }

    fun loadActiveQuests() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("users").document(uid)
            .collection("quests")
            .whereEqualTo("done", false)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val quests = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        QuestEntity(
                            id = doc.id,
                            category = data["category"] as? String ?: "",
                            title = data["title"] as? String ?: "",
                            done = data["done"] as? Boolean ?: false,
                            xpReward = (data["xpReward"] as? Long ?: 10L).toInt(),
                            frequency = data["frequency"] as? String ?: "daily"
                        )
                    }
                    _activeQuests.value = quests
                }
            }
    }

    fun completeQuest(quest: QuestEntity, onComplete: () -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore
        val docRef = db.collection("users").document(uid)
            .collection("quests").document(quest.id)

        docRef.update("done", true).addOnSuccessListener {
            val userRef = db.collection("users").document(uid)

            userRef.get().addOnSuccessListener { doc ->
                val statsMap = doc.get("stats") as? Map<String, Map<String, Any>> ?: return@addOnSuccessListener
                val current = statsMap[quest.category]
                val currentLevel = (current?.get("level") as? Long)?.toInt() ?: 1
                val currentXp = (current?.get("xp") as? Long)?.toInt() ?: 0

                val newXp = currentXp + quest.xpReward
                val newLevel = currentLevel + (newXp / 100)
                val remainingXp = newXp % 100

                val updated = statsMap.toMutableMap()
                updated[quest.category] = mapOf(
                    "level" to newLevel,
                    "xp" to remainingXp
                )

                userRef.update("stats", updated).addOnSuccessListener {
                    loadUserStats()
                    onComplete()
                }
            }
        }
    }

    fun loadCompletedQuests() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("users").document(uid)
            .collection("quests")
            .whereEqualTo("done", true)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val quests = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        QuestEntity(
                            id = doc.id,
                            category = data["category"] as? String ?: "",
                            title = data["title"] as? String ?: "",
                            done = data["done"] as? Boolean ?: false,
                            xpReward = (data["xpReward"] as? Long ?: 10L).toInt(),
                            frequency = data["frequency"] as? String ?: "daily"
                        )
                    }
                    _completedQuests.value = quests
                }
            }
    }

    fun deleteQuest(quest: QuestEntity, onComplete: () -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore

        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val statsMap = doc.get("stats") as? Map<String, Map<String, Any>> ?: return@addOnSuccessListener
            val current = statsMap[quest.category]
            val currentLevel = (current?.get("level") as? Long)?.toInt() ?: 1
            val currentXp = (current?.get("xp") as? Long)?.toInt() ?: 0

            var newXp = currentXp - quest.xpReward
            var newLevel = currentLevel

            if (newXp < 0) {
                newLevel = (newLevel - 1).coerceAtLeast(1)
                newXp = 100 + newXp
            }

            val updated = statsMap.toMutableMap()
            updated[quest.category] = mapOf(
                "level" to newLevel,
                "xp" to newXp
            )

            userRef.update("stats", updated).addOnSuccessListener {
                db.collection("users").document(uid)
                    .collection("quests")
                    .document(quest.id)
                    .delete()
                    .addOnSuccessListener {
                        loadUserStats()
                        onComplete()
                    }
            }
        }
    }

    fun calculateOverallLevel(): Int {
        val currentStats = _stats.value
        if (currentStats.isEmpty()) return 1
        val average = currentStats.values.map { it.level }.average()
        return ceil(average).toInt()
    }

    val userLevel: Int
        get() = calculateOverallLevel()

    // 💣 Reset všetkého
    fun resetEverything(onComplete: () -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val db = Firebase.firestore
        val userRef = db.collection("users").document(uid)

        // 1. Reset štatistík
        val defaultStats = QuestData.allCategories.associateWith {
            mapOf("level" to 1, "xp" to 0)
        }

        userRef.update("stats", defaultStats).addOnSuccessListener {
            // 2. Vymazanie všetkých questov (aktívne aj dokončené)
            userRef.collection("quests").get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    // 3. Vyčistenie lokálnych stavov
                    _stats.value = emptyMap()
                    _activeQuests.value = emptyList()
                    _completedQuests.value = emptyList()

                    // 4. Znova načítame aktuálne údaje
                    loadUserStats()
                    loadActiveQuests()
                    loadCompletedQuests()

                    onComplete()
                }
            }
        }
    }
}
