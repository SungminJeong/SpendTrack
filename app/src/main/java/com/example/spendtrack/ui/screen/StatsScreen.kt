package com.example.spendtrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.example.spendtrack.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    userId: Int,
    viewModel: TransactionViewModel
) {

    // ================= 상태 =================
    var expanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    fun getMonthStart(time: Long): Long {
        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getMonthEnd(time: Long): Long {
        calendar.timeInMillis = time
        calendar.add(Calendar.MONTH, 1)
        return calendar.timeInMillis
    }

    fun formatMonth(time: Long): String {
        val sdf = SimpleDateFormat("yyyy MMM", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun getRecentMonths(): List<Long> {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()

        repeat(12) {
            cal.set(Calendar.DAY_OF_MONTH, 1)
            list.add(cal.timeInMillis)
            cal.add(Calendar.MONTH, -1)
        }
        return list
    }

    val currentMonthStart = getMonthStart(System.currentTimeMillis())
    var selectedMonth by remember { mutableStateOf(currentMonthStart) }

    // 최초 1회 필터 적용
    LaunchedEffect(Unit) {
        viewModel.setMonthFilter(selectedMonth)
    }

    // ================= 데이터 =================
    val monthlyFlow = remember {
        viewModel.monthlySummaryFiltered(userId)
    }

    val categoryFlow = remember {
        viewModel.categorySummaryFiltered(userId)
    }

    val monthlySummary by monthlyFlow.collectAsState()
    val categorySummary by categoryFlow.collectAsState()

    // ================= UI =================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🔥 월 선택 UI
        val months = remember { getRecentMonths() }

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(formatMonth(selectedMonth))
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

                            viewModel.setMonthFilter(month)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ================= PieChart =================
        Text("Expense by Category", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (categorySummary.isEmpty()) {
            Text("No data available")
        } else {

            val entries = categorySummary.map {
                PieEntry(it.total.toFloat(), it.category)
            }

            val dataSet = PieDataSet(entries, "Category").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 14f
                valueTextColor = android.graphics.Color.WHITE
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter())
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { PieChart(it) },
                update = { chart ->
                    chart.data = data
                    chart.setUsePercentValues(true)
                    chart.description.isEnabled = false
                    chart.animateY(1000)
                    chart.invalidate()
                }
            )
        }

        ////////////////////////////////////////////////////////////////////
        val monthlyData by viewModel
            .simpleMonthlyComparison(userId)
            .collectAsState(initial = emptyList())



        val sorted = monthlyData.sortedBy { it.month }


        val current = sorted.lastOrNull()?.total ?: 0.0
        val previous = sorted.dropLast(1).lastOrNull()?.total ?: 0.0

        val changeRate =
            if (previous == 0.0) 0.0
            else ((current - previous) / previous) * 100

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📊 Monthly Comparison",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("This month: ${"%.2f".format(current)}")
        Text("Last month: ${"%.2f".format(previous)}")

        Text(
            text = "Change: ${"%.1f".format(changeRate)}%",
            color = if (changeRate >= 0)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        )
        ///////////////////////////////////////////////////////////

        Spacer(modifier = Modifier.height(24.dp))

        // ================= BarChart =================
        Text("Monthly Expense", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (monthlySummary.isEmpty()) {
            Text("No data available")
        } else {

            val entries = sorted.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.total.toFloat())
            }



            val dataSet = BarDataSet(entries, "Monthly Expense").apply {
                colors = listOf(
                    androidx.compose.ui.graphics.Color(0xFF9E9E9E).toArgb(),
                    androidx.compose.ui.graphics.Color(0xFF2196F3).toArgb()
                )
            }



            val data = BarData(dataSet)

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { BarChart(it) },
                update = { chart ->
                    chart.data = data
                    chart.description.isEnabled = false

                    chart.xAxis.valueFormatter =
                        IndexAxisValueFormatter(sorted.map { it.month })
                    /*
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(
                        monthlySummary.map { it.month }
                    )
                     */


                    chart.animateY(1000)
                    chart.invalidate()
                }
            )
        }
    }
}






