package com.example.todo.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.adapter.ToDoAdapter
import com.example.todo.databinding.FragmentHomeBinding
import com.example.todo.itemTouchHelper.SwipeController
import com.example.todo.model.ToDo
import com.example.todo.viewmodel.ToDoViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var todosViewModel : ToDoViewModel

    private lateinit var toDoAdapter: ToDoAdapter
    private lateinit var addToDoView: View

    private var currentStartOfWeek: LocalDate = LocalDate.now()
    var currentToDoId: Int? = null
    var isEditing = false

    private var selectedDateTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todosViewModel = (activity as MainActivity).toDoViewModel
        addToDoView = view
        setupHomeRecyclerView()

        updateDateText(currentStartOfWeek)

        // 저번주
        binding.preBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.yearMonth.text = "${yearMonth.year}년 ${yearMonth.month}월"
            updateDateText(currentStartOfWeek)
        }

        // 다음주
        binding.nextBtn.setOnClickListener {
            currentStartOfWeek = currentStartOfWeek.plusWeeks(1)
            val yearMonth = YearMonth.from(currentStartOfWeek)
            binding.yearMonth.text = "${yearMonth.year}년 ${yearMonth.month}월"
            updateDateText(currentStartOfWeek)
        }

        binding.addToDo.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.addToDo.windowToken, 0)

                val toDoTitle = binding.addToDo.text.toString().trim()
                val toDoDate = currentStartOfWeek.format(DateTimeFormatter.ofPattern("yy-MM-dd"))

                if (toDoTitle.isNotEmpty()) {
                    if (isEditing && currentToDoId != null) {
                        val toDo = ToDo(currentToDoId!!, toDoTitle, "$toDoDate",false)
                        todosViewModel.updateToDo(toDo)
                        isEditing = false
                    } else {
                        saveToDo(addToDoView)
                    }
                } else {
                    Toast.makeText(addToDoView.context, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                }

                val text = binding.addToDo.text
                text.clear()
                currentToDoId = null

                true
            } else {
                false
            }
        }
    }

    private fun updateDateText(startOfWeek: LocalDate) {
        val nearestSunday = startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val yearMonth = YearMonth.from(nearestSunday)
        binding.yearMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"

        for (i in 1..7) {
            val currentDateForDay = nearestSunday.plusDays(i.toLong() - 1)
            val dateTextView = when (i) {
                1 -> binding.date1
                2 -> binding.date2
                3 -> binding.date3
                4 -> binding.date4
                5 -> binding.date5
                6 -> binding.date6
                7 -> binding.date7
                else -> null
            }

            val dayTextView = when (i) {
                1 -> binding.day1
                2 -> binding.day2
                3 -> binding.day3
                4 -> binding.day4
                5 -> binding.day5
                6 -> binding.day6
                7 -> binding.day7
                else -> null
            }

            dateTextView?.text = formatDate(currentDateForDay)

            //오늘 날짜 표시
            val today = LocalDate.now()
            val isTodayInWeek = startOfWeek <= today && today <= startOfWeek.plusDays(6)

            if (isTodayInWeek) {
                if (today == currentDateForDay) {
                    binding.todayCircle.visibility = View.VISIBLE
                    dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener{
                        override fun onPreDraw() : Boolean {
                            dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                            val dateTextViewX = dateTextView.x
                            val dateTextViewWidth = dateTextView.width.toFloat()
                            val circleWidth = binding.todayCircle.width.toFloat()
                            binding.todayCircle.x =
                                dateTextViewX + (dateTextViewWidth - circleWidth) / 2
                            return true
                        }
                    })

                    dateTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    dayTextView?.setTextColor(Color.parseColor("#1D1D1D"))
                }
            } else {
                dateTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                dayTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                binding.todayCircle.visibility = View.GONE
            }

            // 주말일 경우 색상 다르게
            if (dateTextView?.id == binding.date6.id) {
                dayTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }

            if (dateTextView?.id == binding.date7.id) {
                dayTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }

            dateTextView?.setOnClickListener {

                selectedDateTextView?.let {
                    selectedDateTextView?.let { previousTextView ->
                        binding.clickDate.visibility = View.GONE
                    }
                }

                selectedDateTextView = dateTextView
                binding.clickDate.visibility = View.VISIBLE

                dateTextView?.viewTreeObserver?.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener{
                    override fun onPreDraw(): Boolean {
                        dateTextView.viewTreeObserver.removeOnPreDrawListener(this)
                        val dateTextViewX = dateTextView.x
                        val dateTextViewWidth = dateTextView.width.toFloat()
                        val clickDateWidth = binding.clickDate.width.toFloat()
                        binding.clickDate.x =
                            dateTextViewX + (dateTextViewWidth - clickDateWidth) / 2
                        return true
                    }
                })
            }
        }
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return date.format(formatter)
    }

    private fun setupHomeRecyclerView() {
        toDoAdapter = ToDoAdapter (
            onItemDelete = { toDo ->
                deleteToDo(toDo)
            },
            onItemEdit = { toDo ->
                editToDo(toDo)
            }
        )

        binding.homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = toDoAdapter
        }

        activity?.let {
            todosViewModel.getAllToDo().observe(viewLifecycleOwner) { todo ->
                toDoAdapter.differ.submitList(todo)
            }
        }

        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)!!
        val editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.edit)!!
        val deleteColor = Color.RED
        val editColor = Color.BLUE

        val swipeController = SwipeController(
            onItemDelete = { position ->
                val toDo = toDoAdapter.differ.currentList[position]
                deleteToDo(toDo)
            },
            onItemEdit = { position ->
                val toDo = toDoAdapter.differ.currentList[position]
                editToDo(toDo)
            }
        ).apply {
            setIconsAndColors(deleteIcon, editIcon, deleteColor, editColor)
        }

        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.homeRecyclerView)
    }

    private fun saveToDo(view: View) {
        val toDoTitle = binding.addToDo.text.toString().trim()
        val toDoDate = currentStartOfWeek.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
        val toDo = ToDo(0, toDoTitle, "$toDoDate", false)

        todosViewModel.addToDo(toDo)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.homeRecyclerView.scrollToPosition(0)
        }, 100)
    }

    private fun deleteToDo(toDo: ToDo) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("ToDo 삭제")
            setMessage("정말로 삭제 하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                todosViewModel.deleteToDo(toDo)
                toDoAdapter.notifyDataSetChanged()
            }
            setNegativeButton("취소") { _, _ ->
                toDoAdapter.notifyDataSetChanged()
            }
            setCancelable(false)
        }.create().show()
    }

    private fun editToDo(toDo: ToDo) {
        val position = toDoAdapter.differ.currentList.indexOf(toDo)
        if (position != -1) {
            val currentToDo = toDoAdapter.differ.currentList[position]
            currentToDoId = currentToDo.id
            binding.addToDo.setText(currentToDo.toDoTitle)
            isEditing = true
        }
        toDoAdapter.notifyDataSetChanged()
    }
}