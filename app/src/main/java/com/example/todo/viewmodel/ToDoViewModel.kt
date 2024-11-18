package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.ToDo
import com.example.todo.repository.ToDoRepository
import kotlinx.coroutines.launch

class ToDoViewModel(app: Application, private val toDoRepository: ToDoRepository): AndroidViewModel(app) {

    fun addToDo(todo: ToDo) =
        viewModelScope.launch {
            toDoRepository.insertToDo(todo)
        }

    fun deleteToDo(todo: ToDo) =
        viewModelScope.launch {
            toDoRepository.deleteToDo(todo)
        }

    fun updateToDo(todo: ToDo) =
        viewModelScope.launch {
            toDoRepository.updateToDo(todo)
        }

    fun getAllToDo() = toDoRepository.getAllToDo()
}