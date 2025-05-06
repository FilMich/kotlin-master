package com.example.semestralna_praca.ui.theme.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.semestralna_praca.viewmodel.AssessmentViewModel


@Composable
fun AssessmentScreen(
    navController: NavHostController,
    viewModel: AssessmentViewModel = viewModel()
) {
    val questions by viewModel.questions.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val question = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Question ${currentIndex + 1}/${questions.size}", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = question.text, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(24.dp))

        question.options.forEachIndexed { i, option ->
            Button(
                onClick = {
                    val score = question.scores[i]
                    viewModel.answerQuestion(question.id, score)

                    if (currentIndex < questions.lastIndex) {
                        currentIndex++
                    } else {
                        val results = viewModel.calculateCategoryScores()
                        // TODO: Save to Firestore
                        println("Final category results: $results")
                        navController.navigate("main") {
                            //popUpTo(Screen.Assessment.route) { inclusive = true }
                            viewModel.saveResultsToFirestore(
                                onSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("assessment") { inclusive = true }
                                    }
                                },
                                onError = { e ->
                                    println("Firestore error: ${e.message}")
                                    // TODO: môžeš zobraziť Toast alebo Snackbar
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(option)
            }
        }
    }
}