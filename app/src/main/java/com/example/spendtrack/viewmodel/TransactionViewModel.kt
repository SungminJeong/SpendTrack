package com.example.spendtrack.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendtrack.data.model.CategorySummary
import com.example.spendtrack.data.model.Transaction
import com.example.spendtrack.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.*
import java.util.Calendar
import com.example.spendtrack.data.model.MonthlySummary

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    /////////////////////////////////////////////////////////////////
    private val _selectedMonth = MutableStateFlow<Long?>(null)
    val selectedMonth: StateFlow<Long?> = _selectedMonth
    /////////////////////////////////////////////////////////////////

    // [기존]
    fun add(transaction: Transaction) {

        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    // [추가] UPDATE 기능
    fun update(transaction: Transaction) {

        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun getTransactionById(id: Int) =
        repository.getTransactionById(id)

    // [기존]
    fun delete(transaction: Transaction) {

        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun typeSummary(userId: Int) =
        repository.getTypeSummary(userId)

    fun categorySummary(userId: Int) =
        repository.getCategorySummary(userId)

    fun monthlySummary(userId: Int) =
        repository.getMonthlySummary(userId)

    // ✅ StateFlow 필터 상태
    private val selectedCategoryFlow = MutableStateFlow<String?>(null)
    private val startDateFlow = MutableStateFlow<Long?>(null)
    private val endDateFlow = MutableStateFlow<Long?>(null)


    // ✅ 필터 설정
    fun setCategoryFilter(category: String?) {
        selectedCategoryFlow.value = category
        startDateFlow.value = null
        endDateFlow.value = null
    }

    fun setDateFilter(start: Long, end: Long) {
        startDateFlow.value = start
        endDateFlow.value = end
        selectedCategoryFlow.value = null
    }

    fun clearFilter() {
        selectedCategoryFlow.value = null
        startDateFlow.value = null
        endDateFlow.value = null
    }

    // ✅ 핵심: combine으로 자동 필터링
    fun transactions(userId: Int): Flow<List<Transaction>> {

        val baseFlow = repository.getTransactions(userId)

        return combine(
            baseFlow,
            selectedCategoryFlow,
            startDateFlow,
            endDateFlow
        ) { list, category, start, end ->

            list.filter {

                val matchCategory =
                    category == null || it.category == category

                val matchDate =
                    (start == null || end == null) ||
                            (it.date in start..end)

                matchCategory && matchDate
            }
        }
    }
    ///////////////////////////////////////////////////
    fun setMonthFilter(month: Long?) {
        _selectedMonth.value = month
    }
    private fun getMonthRange(monthStart: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = monthStart

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        return start to end
    }
    fun monthlySummaryFiltered(userId: Int): StateFlow<List<MonthlySummary>> {
        return _selectedMonth.flatMapLatest { month ->

            if (month == null) {
                repository.getMonthlySummary(userId)
            } else {
                val (start, end) = getMonthRange(month)
                repository.getMonthlySummaryByDate(userId, start, end)
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }
    fun categorySummaryFiltered(userId: Int): StateFlow<List<CategorySummary>> {
        return _selectedMonth.flatMapLatest { month ->

            if (month == null) {
                repository.getCategorySummary(userId)
            } else {
                val (start, end) = getMonthRange(month)
                repository.getCategorySummaryByDate(userId, start, end)
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

}
