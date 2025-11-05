package com.example.gitrepochecker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold { innerPadding ->
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = repoValue.url,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 60.dp),
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(20.dp))

                Text(
                    text = if (repoValue.isOutdated) "Устарел" else "Актуален",
                    color = if (repoValue.isOutdated) Color(0xFFe03131)
                            else Color(0xFF2f9e44),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Частота проверки",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        border = BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(120.dp).width(120.dp),
                        onClick = { frequency = "HOURLY" },
                        colors = if (frequency == "HOURLY")
                            ButtonDefaults.buttonColors(backgroundColor = Color(0xFF69db7c))
                        else ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCED4DA))
                    ) { Text(
                        "Каждый час",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) }

                    Spacer(Modifier.width(50.dp))

                    OutlinedButton(
                        border = BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(120.dp).width(120.dp),
                        onClick = { frequency = "DAILY" },
                        colors = if (frequency == "DAILY")
                            ButtonDefaults.buttonColors(backgroundColor = Color(0xFF69db7c))
                        else ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCED4DA))
                    ) { Text(
                        "Каждый день",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) }
                }

                Spacer(Modifier.height(50.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedButton(onClick = { viewModel.checkNow(repoValue) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFa5d8ff)),
                        border = BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(70.dp).width(300.dp)
                    )
                    {
                        Text("Проверить актуальность",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.height(70.dp))

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        border = BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(70.dp).width(300.dp)
                    ) {
                        Text("Удалить",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp)
                    }

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
