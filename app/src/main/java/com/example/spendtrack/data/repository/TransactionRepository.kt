package com.example.spendtrack.data.repository

import com.example.spendtrack.data.local.TransactionDao
import com.example.spendtrack.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val dao: TransactionDao
) {

    fun getTransactions(userId: Int): Flow<List<Transaction>> =
        dao.getAll(userId)

    suspend fun insert(transaction: Transaction) =
        dao.insert(transaction)

    suspend fun update(transaction: Transaction) =
        dao.update(transaction)

    suspend fun delete(transaction: Transaction) =
        dao.delete(transaction)

    fun getTransactionById(id: Int) =
        dao.getTransactionById(id)

    fun getTypeSummary(userId: Int) =
        dao.getTypeSummary(userId)

    fun getCategorySummary(userId: Int) =
        dao.getCategorySummary(userId)

    fun getMonthlySummary(userId: Int) =
        dao.getMonthlySummary(userId)

    fun filterByCategory(userId: Int, category: String) =
        dao.filterCategory(userId, category)

    fun filterByDate(userId: Int, start: Long, end: Long) =
        dao.filterDate(userId, start, end)

    fun getCategorySummaryByDate(userId: Int, start: Long, end: Long) =
        dao.getCategorySummaryByDate(userId, start, end)

    fun getMonthlySummaryByDate(userId: Int, start: Long, end: Long) =
        dao.getMonthlySummaryByDate(userId, start, end)
}