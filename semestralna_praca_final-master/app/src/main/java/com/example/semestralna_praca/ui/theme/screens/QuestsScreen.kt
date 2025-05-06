package com.example.semestralna_praca.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralna_praca.viewmodel.HomeViewModel
import com.example.semestralna_praca.model.QuestEntity

@Composable
fun QuestsScreen(viewModel: HomeViewModel = viewModel()) {
    val stats by viewModel.stats.collectAsState()
    val activeQuests by viewModel.activeQuests.collectAsState()

    var showDialogForCategory by remember { mutableStateOf<String?>(null) }
    var showQuestList by remember { mutableStateOf(false) }
    var selectedQuest by remember { mutableStateOf<QuestEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("🧾 Tvoje questy", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Kategórie:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        stats.forEach { (category, data) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$category: Level ${data.level} (${data.xp}/100 XP)",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDialogForCategory = category }) {
                        Icon(Icons.Default.Add, contentDescription = "Pridať quest")
                    }
                }

                LinearProgressIndicator(
                    progress = data.xp / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { showQuestList = true }) {
            Text("Zobraziť aktívne questy")
        }
    }

    // Dialog na pridanie questu
    showDialogForCategory?.let { category ->
        var selectedFrequency by remember { mutableStateOf("daily") }
        val options = listOf("daily", "weekly", "monthly")

        AlertDialog(
            onDismissRequest = { showDialogForCategory = null },
            title = { Text("Nový quest") },
            text = {
                Column {
                    Text("Kategória: $category")
                    Spacer(Modifier.height(8.dp))
                    Text("Typ questu:")
                    options.forEach { freq ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedFrequency == freq,
                                onClick = { selectedFrequency = freq }
                            )
                            Text(freq.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addQuest(category, selectedFrequency) {
                        showDialogForCategory = null
                    }
                }) {
                    Text("Pridať")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogForCategory = null }) {
                    Text("Zrušiť")
                }
            }
        )
    }

    // Dialog so zoznamom aktívnych questov
    if (showQuestList) {
        AlertDialog(
            onDismissRequest = { showQuestList = false },
            title = { Text("🎯 Aktívne questy") },
            text = {
                if (activeQuests.isEmpty()) {
                    Text("Nemáš žiadne aktívne questy.")
                } else {
                    LazyColumn(modifier = Modifier.height(250.dp)) {
                        items(activeQuests) { quest ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedQuest = quest }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (quest.frequency) {
                                    "daily" -> "🗓"
                                    "weekly" -> "📅"
                                    "monthly" -> "🗓️"
                                    else -> "❔"
                                }
                                Text("$icon ${quest.title}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuestList = false }) {
                    Text("Zavrieť")
                }
            }
        )
    }

    // Detail konkrétneho questu
    selectedQuest?.let { quest ->
        AlertDialog(
            onDismissRequest = { selectedQuest = null },
            title = { Text("Detail questu") },
            text = {
                Column {
                    Text("Názov: ${quest.title}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Typ: ${quest.frequency.uppercase()}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Odporúčanie:")
                    Text(getQuestTip(quest))
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedQuest = null }) {
                    Text("Späť")
                }
            }
        )
    }
}

// 🧠 Odporúčanie podľa typu
fun getQuestTip(quest: QuestEntity): String {
    return when (quest.frequency) {
        "daily" -> "Skús ho splniť ešte dnes – zaberie len chvíľku."
        "weekly" -> "Rozlož si ho do viacerých dní v týždni."
        "monthly" -> "Zvoľ si plán a sleduj svoj pokrok počas mesiaca."
        else -> "Dobrý tréning je vždy dobrý nápad!"
    }
}
