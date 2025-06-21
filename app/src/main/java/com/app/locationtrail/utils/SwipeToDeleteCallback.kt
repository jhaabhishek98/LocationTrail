package com.app.locationtrail.utils

import android.app.AlertDialog
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.locationtrail.R
import com.app.locationtrail.data.local.LocationEntity
import androidx.core.graphics.drawable.toDrawable
import com.app.locationtrail.ui.list.LocationListAdapter


class SwipeToDeleteCallback(
    private val onDeleteConfirmed: (LocationEntity) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val background = Color.RED.toDrawable()
    private val deleteIcon = R.drawable.ic_delete // your delete icon
    private val paint = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = 0.5f // 50%

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = (viewHolder as LocationListAdapter.LocationViewHolder).getItem()
        viewHolder.itemView.post {
            AlertDialog.Builder(viewHolder.itemView.context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete \"${item.name}\"?")
                .setPositiveButton("Delete") { _, _ -> onDeleteConfirmed(item) }
                .setNegativeButton("Cancel") { _, _ ->
                    (viewHolder.itemView.parent as RecyclerView).adapter?.notifyItemChanged(viewHolder.adapterPosition)
                }
                .show()
        }
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        // Background
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        // Icon and text
        val icon = ContextCompat.getDrawable(itemView.context, deleteIcon)!!
        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + icon.intrinsicHeight
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)

        // Text
        paint.color = Color.WHITE
        paint.textSize = 40f
        paint.isAntiAlias = true
        val text = "Delete"
        val textWidth = paint.measureText(text)
        c.drawText(text, iconLeft - textWidth - 20, itemView.top + itemView.height / 2f + 15, paint)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}