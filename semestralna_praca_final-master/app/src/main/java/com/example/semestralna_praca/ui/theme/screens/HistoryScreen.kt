package com.example.semestralna_praca.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralna_praca.model.QuestEntity
import com.example.semestralna_praca.viewmodel.HomeViewModel

@Composable
fun HistoryScreen(viewModel: HomeViewModel) {
    val completedQuests by viewModel.completedQuests.collectAsState()
    var questToDelete by remember { mutableStateOf<QuestEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("🏆 História questov", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (completedQuests.isEmpty()) {
            Text("Zatiaľ si nesplnil žiadny quest.")
        } else {
            completedQuests.forEach { quest ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "✔ ${quest.title} [${quest.category}]",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { questToDelete = quest }) {
                        Icon(Icons.Default.Delete, contentDescription = "Zmazať quest")
                    }
                }
            }
        }
    }

    // Potvrdenie zmazania
    questToDelete?.let { quest ->
        AlertDialog(
            onDismissRequest = { questToDelete = null },
            title = { Text("Zmazať quest") },
            text = {
                Text("Naozaj chceš zmazať tento quest?\n\n${quest.title} [${quest.category}]\nStratíš ${quest.xpReward} XP.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteQuest(quest) {
                        questToDelete = null
                    }
                }) {
                    Text("Zmazať")
                }
            },
            dismissButton = {
                TextButton(onClick = { questToDelete = null }) {
                    Text("Zrušiť")
                }
            }
        )
    }
}
