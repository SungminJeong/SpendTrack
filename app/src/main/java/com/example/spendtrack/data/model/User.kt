package com.example.spendtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val passwordHash: String,

    val createdAt: Long
)