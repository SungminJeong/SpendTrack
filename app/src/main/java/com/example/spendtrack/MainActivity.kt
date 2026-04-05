package com.example.spendtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.spendtrack.ui.theme.SpendtrackTheme

import com.example.spendtrack.ui.navigation.NavGraph

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendtrack.data.local.AppDatabase
import com.example.spendtrack.data.repository.TransactionRepository
import com.example.spendtrack.data.repository.UserRepository
import com.example.spendtrack.viewmodel.AuthViewModel
import com.example.spendtrack.viewmodel.AuthViewModelFactory
import com.example.spendtrack.viewmodel.TransactionViewModel
import com.example.spendtrack.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // [추가] Database 생성
            val database = AppDatabase.getDatabase(this)

            // [추가] Repository 생성
            val repository = TransactionRepository(
                database.transactionDao()
            )

            // [추가] ViewModel 생성
            val viewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(repository)
            )

            val userRepository =
                UserRepository(database.userDao())

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(userRepository)
            )

            // [수정] NavGraph에 ViewModel 전달
            NavGraph(viewModel, authViewModel)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendtrackTheme {
        Greeting("Android")
    }
}