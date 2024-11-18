package com.example.todo.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todo")
@Parcelize
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val toDoTitle: String,
    val date: String,
    var isChecked: Boolean = false,
    var isPinned: Boolean = false
):Parcelable