package com.example.spendtrack.data.repository

import com.example.spendtrack.data.local.UserDao
import com.example.spendtrack.data.model.User

class UserRepository(
    private val dao: UserDao
) {

    suspend fun register(name: String, passwordHash: String) {

        val user = User(
            name = name,
            passwordHash = passwordHash,
            createdAt = System.currentTimeMillis()
        )

        dao.insert(user)
    }

    suspend fun login(name: String): User? =
        dao.getUser(name)
}