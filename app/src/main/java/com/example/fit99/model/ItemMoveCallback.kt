package com.example.fit99.model


import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.WorkoutConfirmationFragment

class ItemMoveCallback(private val listener: WorkoutConfirmationFragment) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN // Enable dragging up and down
        return makeMovementFlags(dragFlags, 0) // Disable other gestures
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // Notify the listener when an item is moved
        listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Not used in this case
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true // Enable long-press to start dragging
    }
}
