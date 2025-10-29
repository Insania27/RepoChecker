package com.example.gitrepochecker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RepoDetailScreen(navController: NavController, repoId: Long) {
    val viewModel: RepoViewModel = hiltViewModel()


    LaunchedEffect(repoId) {
        viewModel.loadRepo(repoId)
    }

    val repo by remember { derivedStateOf { viewModel.currentRepo.value } }
    var showDeleteDialog by remember { mutableStateOf(false) }


    var frequency by remember(repo) { mutableStateOf(repo?.frequency ?: "HOURLY") }


    if (repo == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val repoValue = repo!!

    LaunchedEffect(repoValue.frequency) {
        frequency = repoValue.frequency
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Детали репозитория") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "URL репозитория:")
            Text(text = repoValue.url)

            Text(
                text = if (repoValue.isOutdated) "Статус: Устарел" else "Статус: Актуален"
            )

            Text(text = "Частота проверки")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { frequency = "HOURLY" },
                    colors = if (frequency == "HOURLY")
                        ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                    else ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) { Text("Каждый час") }

                Button(
                    onClick = { frequency = "DAILY" },
                    colors = if (frequency == "DAILY")
                        ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                    else ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) { Text("Каждый день") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.checkNow(repoValue) }) {
                    Text("Проверить актуальность")
                }
                Button(
                    onClick = {
                        if (frequency != repoValue.frequency) {
                            viewModel.updateRepoFrequency(repoValue, frequency)
                        }
                    }
                ) {
                    Text("Сохранить")
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Удалить")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Вы уверены, что хотите удалить репозиторий?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteRepo(repoValue)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}
