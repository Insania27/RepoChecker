package com.example.gitrepochecker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
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
fun AddRepoScreen(navController: NavController) {
    val viewModel: RepoViewModel = hiltViewModel()
    var url by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("HOURLY") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top =50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL репозитория", color = Color(0xFF194370)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFF194370),
                focusedBorderColor = Color(0xFF194370)
            ),

        )
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Частота проверки",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { frequency = "HOURLY" },
//            colors = if (frequency == "HOURLY") ButtonDefaults.buttonColors(Color.Green)
//            else ButtonDefaults.buttonColors(Color.Gray)) {
//            Text("Каждый час")
//        }
//        Button(onClick = { frequency = "DAILY" }, colors =
//            if (frequency == "DAILY") ButtonDefaults.buttonColors(Color.Green)
//            else ButtonDefaults.buttonColors(Color.Gray)) {
//            Text("Каждый день")
//        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                border = BorderStroke(width = 2.dp, color = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp),
                onClick = { frequency = "HOURLY" },
                colors = if (frequency == "HOURLY")
                    androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            0xFF69db7c
                        )
                    )
                else androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0xFFCED4DA
                    )
                )
            ) {
                Text(
                    "Каждый час",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(50.dp))

            OutlinedButton(
                border = BorderStroke(width = 2.dp, color = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp),
                onClick = { frequency = "DAILY" },
                colors = if (frequency == "DAILY")
                    androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            0xFF69db7c
                        )
                    )
                else androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0xFFCED4DA
                    )
                )
            ) {
                Text(
                    "Каждый день",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
        Spacer(Modifier.height(50.dp))
//        Button(onClick = {
//                viewModel.addRepo(url, frequency) { success, msg ->
//                    if (success) {
//                        navController.popBackStack()
//                    } else {
//                        error = msg
//                    }
//                }
//            },
//            colors = ButtonDefaults.buttonColors(Color.Blue)) {
//            Text("Добавить")
//        }
        OutlinedButton(
            onClick = {
                viewModel.addRepo(url, frequency) { success, msg ->
                    if (success) {
                        navController.popBackStack()
                    } else {
                        error = msg
                    }

                }
            },
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = Color(
                    0xFFa5d8ff
                )
            ),
            border = BorderStroke(width = 2.dp, color = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(70.dp)
                .width(300.dp)
        )
        {
            Text(
                "Добавить",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}