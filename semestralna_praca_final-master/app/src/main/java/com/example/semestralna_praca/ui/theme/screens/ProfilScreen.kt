package com.example.semestralna_praca.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.semestralna_praca.utils.AvatarHelper
import com.example.semestralna_praca.viewmodel.HomeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val overallLevel = viewModel.calculateOverallLevel()
    val avatar = AvatarHelper.getPlayerAvatar(overallLevel)

    var showSettings by remember { mutableStateOf(false) }
    var showConfirmReset by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (stats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = avatar, fontSize = 64.sp)
            Text("Level postavy: $overallLevel", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))
            stats.forEach { (category, data) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("$category: Level ${data.level} (${data.xp}/100 XP)")
                    LinearProgressIndicator(
                        progress = data.xp / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }
        }

        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Nastavenia")
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Nastavenia") },
                text = {
                    Column {
                        TextButton(onClick = {
                            showConfirmReset = true
                        }) {
                            Text("Resetovať štatistiky")
                        }
                        TextButton(onClick = {
                            showConfirmDelete = true
                        }) {
                            Text("Zmazať účet")
                        }
                        TextButton(onClick = {
                            Firebase.auth.signOut()
                            showSettings = false
                            navController.navigate("welcome") {
                                popUpTo("profile") { inclusive = true }
                            }
                        }) {
                            Text("Odhlásiť sa")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) {
                        Text("Zavrieť")
                    }
                }
            )
        }

        if (showConfirmReset) {
            AlertDialog(
                onDismissRequest = { showConfirmReset = false },
                title = { Text("Reset štatistík") },
                text = { Text("Naozaj chceš resetovať všetko – štatistiky, aktívne questy aj históriu?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetEverything {
                            showConfirmReset = false
                            showSettings = false
                        }
                    }) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmReset = false }) {
                        Text("Zrušiť")
                    }
                }
            )
        }

        if (showConfirmDelete) {
            AlertDialog(
                onDismissRequest = { showConfirmDelete = false },
                title = { Text("Zmazať účet") },
                text = { Text("Naozaj chceš zmazať svoj účet? Táto akcia je nevratná.") },
                confirmButton = {
                    TextButton(onClick = {
                        deleteAccount(
                            onSuccess = {
                                showConfirmDelete = false
                                showSettings = false
                                navController.navigate("welcome") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            },
                            onFailure = {
                                println("Zmazanie účtu zlyhalo: ${it.message}")
                            }
                        )
                    }) {
                        Text("Zmazať")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDelete = false }) {
                        Text("Zrušiť")
                    }
                }
            )
        }
    }
}

// ❌ Zmazanie účtu
fun deleteAccount(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val user = Firebase.auth.currentUser
    user?.delete()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onSuccess()
        } else {
            onFailure(task.exception ?: Exception("Neznáma chyba"))
        }
    }
}
