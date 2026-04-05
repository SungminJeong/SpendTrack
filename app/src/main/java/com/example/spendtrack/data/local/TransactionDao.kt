package com.example.spendtrack.data.local

import androidx.room.*
import com.example.spendtrack.data.model.MonthlySummary
import com.example.spendtrack.data.model.CategorySummary
import com.example.spendtrack.data.model.Transaction
import com.example.spendtrack.data.model.TypeSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM `Transaction` WHERE userId = :userId")
    fun getAll(userId: Int): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM `Transaction`
        WHERE userId = :userId
        AND category = :category
    """)
    fun filterCategory(userId: Int, category: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM `Transaction`
        WHERE userId = :userId
        AND date BETWEEN :start AND :end
    """)
    fun filterDate(userId: Int, start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getTransactionById(id: Int): Flow<Transaction?>

    @Query("""
    SELECT type, SUM(amount) as total
    FROM `Transaction`
    WHERE userId = :userId
    GROUP BY type
""")
    fun getTypeSummary(userId: Int): Flow<List<TypeSummary>>

    @Query("""
    SELECT category, SUM(amount) as total
    FROM `Transaction`
    WHERE userId = :userId
    AND type = 'expense'
    GROUP BY category
""")
    fun getCategorySummary(userId: Int): Flow<List<CategorySummary>>

    @Query("""
    SELECT strftime('%m', date/1000, 'unixepoch') as month,
           SUM(amount) as total
    FROM `Transaction`
    WHERE userId = :userId
    AND type = 'expense'
    GROUP BY month
""")
    fun getMonthlySummary(userId: Int): Flow<List<MonthlySummary>>

    @Query("""
    SELECT category, SUM(amount) as total
    FROM `Transaction`
    WHERE userId = :userId
    AND type = 'expense'
    AND date BETWEEN :start AND :end
    GROUP BY category
    """)
    fun getCategorySummaryByDate(
        userId: Int,
        start: Long,
        end: Long
    ): Flow<List<CategorySummary>>

    @Query("""
SELECT strftime('%m', date/1000, 'unixepoch') as month,
       SUM(amount) as total
FROM `Transaction`
WHERE userId = :userId
AND type = 'expense'
AND date BETWEEN :start AND :end
GROUP BY month
""")
    fun getMonthlySummaryByDate(
        userId: Int,
        start: Long,
        end: Long
    ): Flow<List<MonthlySummary>>
}