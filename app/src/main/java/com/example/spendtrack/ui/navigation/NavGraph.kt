package com.example.spendtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*

import com.example.spendtrack.ui.screen.*
import com.example.spendtrack.viewmodel.AuthViewModel
import com.example.spendtrack.viewmodel.TransactionViewModel

@Composable
fun NavGraph(viewModel: TransactionViewModel, authViewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("register") {
            RegisterScreen(navController, authViewModel)
        }

        composable("list/{userId}") { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toInt() ?: 0

            TransactionListScreen(
                navController,
                viewModel,
                userId
            )
        }

        composable("add/{userId}") { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toInt() ?: 0

            AddEditTransactionScreen(
                navController,
                viewModel,
                null,
                userId
            )
        }

        composable("edit/{id}/{userId}") { backStackEntry ->

            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toInt() ?: 0

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toInt() ?: 0

            AddEditTransactionScreen(
                navController,
                viewModel,
                id,
                userId
            )
        }

        composable("stats/{userId}") { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toInt() ?: 0

            StatsScreen(
                userId = userId,
                viewModel = viewModel
            )
        }
    }
}

