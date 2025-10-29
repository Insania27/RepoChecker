package com.example.gitrepochecker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AddRepoScreen(navController: NavController) {
    val viewModel: RepoViewModel = hiltViewModel()
    var url by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("HOURLY") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TopAppBar(title = { Text("Добавление репозитория") })
        TextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL репозитория") },
            modifier = Modifier.fillMaxWidth()
        )
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Частота проверки")
        Button(onClick = { frequency = "HOURLY" },
            colors = if (frequency == "HOURLY") ButtonDefaults.buttonColors(Color.Green)
            else ButtonDefaults.buttonColors(Color.Gray)) {
            Text("Каждый час")
        }
        Button(onClick = { frequency = "DAILY" }, colors =
            if (frequency == "DAILY") ButtonDefaults.buttonColors(Color.Green)
            else ButtonDefaults.buttonColors(Color.Gray)) {
            Text("Каждый день")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
                viewModel.addRepo(url, frequency) { success, msg ->
                    if (success) {
                        navController.popBackStack()
                    } else {
                        error = msg
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Color.Blue)) {
            Text("Добавить")
        }
    }
}