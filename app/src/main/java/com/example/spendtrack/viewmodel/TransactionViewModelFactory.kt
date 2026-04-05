package com.example.spendtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spendtrack.data.repository.TransactionRepository

// [추가]
// TransactionViewModelFactory는 Repository를 ViewModel에 주입하기 위해 ViewModel을 생성하는 역할을 한다
class TransactionViewModelFactory(

    private val repository: TransactionRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {

            return TransactionViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}