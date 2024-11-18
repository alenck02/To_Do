package com.example.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todo.database.ToDoDatabase
import com.example.todo.repository.ToDoRepository
import com.example.todo.viewmodel.ToDoViewModel
import com.example.todo.viewmodel.ToDoViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var toDoViewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
    }

    private fun setupViewModel() {
        val toDoRepository = ToDoRepository(ToDoDatabase(this))
        val viewModelProFactory = ToDoViewModelFactory(application, toDoRepository)
        toDoViewModel = ViewModelProvider(this, viewModelProFactory)[ToDoViewModel::class.java]
    }
}