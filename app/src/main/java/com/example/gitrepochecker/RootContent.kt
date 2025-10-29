package com.example.gitrepochecker

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RootContent(navController: NavHostController) {

    val viewModel: RepoViewModel = hiltViewModel()


    LaunchedEffect(Unit) {
         viewModel.runImmediateChecksInApp()
    }


    Scaffold(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") { RepoListScreen(navController) }
            composable("add") { AddRepoScreen(navController) }
            composable("detail/{repoId}") { backStackEntry ->
                val repoId = backStackEntry.arguments?.getString("repoId")?.toLong() ?: 0L
                RepoDetailScreen(navController, repoId)
            }
        }
    }
}
