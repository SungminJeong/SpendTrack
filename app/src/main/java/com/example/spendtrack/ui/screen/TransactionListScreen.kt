package com.example.spendtrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendtrack.data.model.Transaction
import com.example.spendtrack.ui.components.TransactionItem
import com.example.spendtrack.viewmodel.TransactionViewModel
import com.example.spendtrack.data.model.CategoryType
import com.example.spendtrack.ui.components.CategoryDropdown

@Composable
fun TransactionListScreen(navController: NavController, viewModel: TransactionViewModel, userId: Int) {


    // [추가] Flow → Compose 상태 변환
    val transactions by viewModel
        .transactions(userId)
        .collectAsState(initial = emptyList())

    val totalExpense = transactions
        .filter { it.type == "expense" }
        .sumOf { it.amount }


    val currentMonthStart = remember {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.timeInMillis
    }

    var selectedMonth by remember { mutableStateOf(currentMonthStart) }

    LaunchedEffect(Unit) {
        val calendar = java.util.Calendar.getInstance()

        calendar.timeInMillis = currentMonthStart
        val start = calendar.timeInMillis

        calendar.add(java.util.Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        viewModel.setDateFilter(start, end)
    }

    var expanded by remember { mutableStateOf(false) }

    fun formatMonth(time: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy MMMM", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(time))
    }

    fun getRecentMonths(): List<Long> {
        val list = mutableListOf<Long>()
        val calendar = java.util.Calendar.getInstance()

        repeat(12) {
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
            list.add(calendar.timeInMillis)
            calendar.add(java.util.Calendar.MONTH, -1)
        }

        return list
    }

    Scaffold(

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    navController.navigate("add/$userId")
                }
            ) {

                Text("+")
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier.padding(padding)
        ) {

            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    navController.navigate("stats/$userId")
                }
            ) {

                Text("View Statistics")

            }

            val months = remember { getRecentMonths() }

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(
                        selectedMonth?.let { formatMonth(it) } ?: "Select Month"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    months.forEach { month ->

                        DropdownMenuItem(
                            text = { Text(formatMonth(month)) },
                            onClick = {

                                selectedMonth = month
                                expanded = false

                                // 🔥 핵심 필터 적용
                                val calendar = java.util.Calendar.getInstance()
                                calendar.timeInMillis = month

                                val start = calendar.timeInMillis

                                calendar.add(java.util.Calendar.MONTH, 1)
                                val end = calendar.timeInMillis

                                viewModel.setDateFilter(start, end)
                            }
                        )
                    }
                }
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    val monthText = formatMonth(selectedMonth)

                    Text(
                        text = "Total Expense ($monthText)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val formattedTotal = String.format("%.2f", totalExpense)

                    Text(
                        text = "-$formattedTotal €",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            var selectedCategory by remember { mutableStateOf<CategoryType?>(null) }

// 카테고리 필터
            CategoryDropdown(
                selected = selectedCategory,
                onSelected = {
                    selectedCategory = it
                    viewModel.setCategoryFilter(it?.name)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = {
                        val calendar = java.util.Calendar.getInstance()

                        // 이번 달 시작
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        calendar.set(java.util.Calendar.MINUTE, 0)
                        calendar.set(java.util.Calendar.SECOND, 0)
                        val start = calendar.timeInMillis

                        selectedMonth = start

                        // 이번 달 끝
                        calendar.add(java.util.Calendar.MONTH, 1)
                        val end = calendar.timeInMillis

                        viewModel.setDateFilter(start, end)
                    }
                ) {
                    Text("This Month")
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
            /////////////////////////////////////////////////////////////////////////////////////////

// 필터 초기화
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    selectedCategory = null
                    viewModel.clearFilter()
                }
            ) {
                Text("Clear Filter")
            }

            LazyColumn {

                items(transactions) {transaction ->

                    TransactionItem(
                        transaction = transaction,
                        onDelete = {

                            viewModel.delete(transaction)

                        },
                        onEdit = {

                            navController.navigate("edit/${transaction.id}/$userId")

                        }
                    )
                }
            }
        }

    }
}