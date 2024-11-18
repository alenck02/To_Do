package com.example.todo.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.database.ToDoDatabase
import com.example.todo.databinding.ToDoLayoutBinding
import com.example.todo.model.ToDo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ToDoAdapter(
    private val onItemDelete: (ToDo) -> Unit,
    private val onItemEdit: (ToDo) -> Unit

) : RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ToDo>(){
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ToDoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(parent.context, binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class MyViewHolder(
        private val context: Context,
        private val binding : ToDoLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toDo: ToDo) = with(binding){
            val currentPosition = adapterPosition
            val currentToDo = differ.currentList[currentPosition]

            val dateFormat = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(currentToDo.date)

            dateTextView.text = dateFormat.format(date)

            checkList.text = currentToDo.toDoTitle
            checkBox.isChecked = currentToDo.isChecked

            if (checkBox.isChecked) {
                checkList.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                checkList.alpha = 0.7f
            } else {
                checkList.paintFlags = 0
                checkList.alpha = 1f
            }

            checkBox.setOnClickListener {
                currentToDo.isChecked = checkBox.isChecked

                if (checkBox.isChecked) {
                    checkList.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    checkList.alpha = 0.7f
                } else {
                    checkList.paintFlags = 0
                    checkList.alpha = 1f
                }

                val toDoDao = ToDoDatabase(context).getToDoDao()
                CoroutineScope(Dispatchers.IO).launch {
                    toDoDao.updateToDo(currentToDo)
                }
            }
        }
    }
}