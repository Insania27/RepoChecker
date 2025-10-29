package com.example.gitrepochecker

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RepoListScreen(navController: NavController) {
    val viewModel: RepoViewModel = hiltViewModel()
    val repos = viewModel.repos.collectAsState(initial = emptyList()).value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") },
                contentColor = Color.White,
                backgroundColor = Color(0xFF69db7c)) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        if (repos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No repositories added")
            }
        } else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Отслеживание репозиториев",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 50.dp),
                    fontWeight = FontWeight.Bold
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().background(color = Color(0xFFf8f9fa))
                ) {
                    items(repos) { repo ->
                        Card(
                            modifier = Modifier
                                .width(350.dp)
                                .height(100.dp)
                                .clickable { navController.navigate("detail/${repo.id}") },
                            backgroundColor = Color(0xffa5d8ff),
                            elevation = 1.dp,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                color = Color.Black,
                                width = 2.dp
                            )
                        ) {
                            Text(
                                text = "Репозиторий ${repo.id}",
                                modifier = Modifier
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}