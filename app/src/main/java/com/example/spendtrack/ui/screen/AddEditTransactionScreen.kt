package com.example.spendtrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendtrack.data.model.Transaction
import com.example.spendtrack.viewmodel.TransactionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.spendtrack.data.model.CategoryType

import com.example.spendtrack.ui.components.CategoryDropdownInput

@Composable
fun AddEditTransactionScreen(navController: NavController,
                             viewModel: TransactionViewModel,
    // 수정 시 전달
                             transactionId: Int? = null,
                             userId: Int
) {
    val transaction by viewModel
        .getTransactionById(transactionId ?: 0)
        .collectAsState(initial = null)

    // 기존 Transaction이 있으면 카테고리 초기화, 없으면 ETC
    val initialCategory: CategoryType = transaction?.category?.let {
        runCatching { CategoryType.valueOf(it) }.getOrNull()
    } ?: CategoryType.ETC

    var selectedCategory by remember { mutableStateOf(CategoryType.ETC) }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Add Transaction", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        CategoryDropdownInput(
            selected = selectedCategory,
            onSelected = {
                selectedCategory = it
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") }
        )

        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(Modifier.height(24.dp))

        Button(

            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                if (transaction == null){
                    val newTransaction = Transaction(
                        type = "expense",
                        category = selectedCategory.name,
                        amount = parsedAmount,
                        date = System.currentTimeMillis(),
                        description = description,
                        userId = userId
                    )

                    viewModel.add(newTransaction)
                    //navController.popBackStack("list/$userId", false)

                }else {

                    val updatedTransaction = transaction!!.copy(
                        category = selectedCategory.name,
                        amount = amount.toDouble(),
                        description = description
                    )

                    viewModel.update(updatedTransaction)
                }
                navController.popBackStack("list/$userId", false)

            },
            modifier = Modifier.fillMaxWidth()

        ) {

            Text("Save")

        }
    }
}