package com.example.spendtrack.data.local

import androidx.room.*
import com.example.spendtrack.data.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM User WHERE name = :name")
    suspend fun getUser(name: String): User?
}