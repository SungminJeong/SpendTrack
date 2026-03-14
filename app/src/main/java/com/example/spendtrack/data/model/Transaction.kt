package com.example.spendtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: String,

    val category: String,

    val amount: Double,

    val date: Long,

    val description: String?,

    val userId: Int
)