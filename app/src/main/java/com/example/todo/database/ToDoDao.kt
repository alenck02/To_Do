package com.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.model.ToDo

@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(todo: ToDo)

    @Update
    suspend fun updateToDo(todo: ToDo)

    @Update
    suspend fun updateToDoList(todos: List<ToDo>)

    @Delete
    suspend fun deleteToDo(todo: ToDo)

    @Query("SELECT * FROM TODO ORDER BY id DESC")
    fun getAllToDo(): LiveData<List<ToDo>>
}