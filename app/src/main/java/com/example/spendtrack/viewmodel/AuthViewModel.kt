package com.example.spendtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendtrack.data.repository.UserRepository
import com.example.spendtrack.util.HashUtil
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var currentUserId: Int? = null

    fun register(name: String, password: String) {

        viewModelScope.launch {

            val hash = HashUtil.sha256(password)

            repository.register(name, hash)
        }
    }

    suspend fun login(name: String, password: String): Boolean {

        val user = repository.login(name) ?: return false

        val hash = HashUtil.sha256(password)

        if (user.passwordHash == hash) {
            currentUserId = user.id
            return true
        }

        return false
    }
}