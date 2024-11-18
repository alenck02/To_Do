package com.example.todo.itemTouchHelper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R

class SwipeController(
    private val onItemDelete: (Int) -> Unit,
    private val onItemEdit: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    private var previousPosition: Int? = null
    private var currentDx = 0f

    private lateinit var deleteIcon: Drawable
    private lateinit var editIcon: Drawable
    private var deleteColor: Int = 0
    private var editColor: Int = 0

    fun setIconsAndColors(deleteIcon: Drawable, editIcon: Drawable, deleteColor: Int, editColor: Int) {
        this.deleteIcon = deleteIcon
        this.editIcon = editIcon
        this.deleteColor = deleteColor
        this.editColor = editColor
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when(direction) {
            LEFT -> onItemDelete(position)
            RIGHT -> onItemEdit(position)
        }
    }

    // select recyclerview when you drag and drop
//    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        viewHolder?.let {
//            currentPosition = viewHolder.adapterPosition
//            getDefaultUIUtil().onSelected(getView(it))
//        }
//
//        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//            viewHolder?.itemView?.alpha = 0.7f
//        }
//    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
        viewHolder.itemView.alpha = 1.0f
        viewHolder.itemView.translationX = 0f
        viewHolder.itemView.translationY = 0f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val background: ColorDrawable
        val icon: Drawable
        val text: String
        val paint = Paint()

        if (dX > 0) {
            icon = editIcon
            background = ColorDrawable(editColor)
            text = "Update"
            paint.color = Color.WHITE
        } else {
            icon = deleteIcon
            background = ColorDrawable(deleteColor)
            text = "Delete"
            paint.color = Color.WHITE
        }

        background.setBounds(
            if (dX > 0) itemView.left else itemView.right + dX.toInt(),
            itemView.top,
            if (dX > 0) itemView.left + dX.toInt() else itemView.right,
            itemView.bottom
        )
        background.draw(c)

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX > 0) {
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        } else {
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        }

        icon.draw(c)

        paint.textSize = 40f
        paint.textAlign = Paint.Align.LEFT
        val textX: Float
        val textY = itemView.top + (itemView.height / 2) + (paint.textSize / 2)

        if (dX > 0) {
            textX = (itemView.left + icon.intrinsicWidth + iconMargin * 2).toFloat()
        } else {
            textX = itemView.right - icon.intrinsicWidth - iconMargin * 2 - paint.measureText(text)
        }

        c.drawText(text, textX, textY, paint)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder) : View = viewHolder.itemView.findViewById(R.id.swipe_view)
}