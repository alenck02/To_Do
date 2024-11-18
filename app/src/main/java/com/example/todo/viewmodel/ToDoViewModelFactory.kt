package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.todo.repository.ToDoRepository

class ToDoViewModelFactory(val app: Application, private val toDoRepository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return ToDoViewModel(app, toDoRepository) as T
    }
}