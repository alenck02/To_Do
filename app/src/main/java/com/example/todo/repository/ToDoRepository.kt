package com.example.todo.repository

import com.example.todo.database.ToDoDatabase
import com.example.todo.model.ToDo

class ToDoRepository(private val db: ToDoDatabase) {
    suspend fun insertToDo(todo: ToDo) = db.getToDoDao().insertToDo(todo)
    suspend fun deleteToDo(todo: ToDo) = db.getToDoDao().deleteToDo(todo)
    suspend fun updateToDo(todo: ToDo) = db.getToDoDao().updateToDo(todo)

    fun getAllToDo() = db.getToDoDao().getAllToDo()
}