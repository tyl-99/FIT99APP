package com.example.fit99.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.DoneWorkout

class MyDoneAdapter(
    private val doneWorkoutList: ArrayList<DoneWorkout>, // Update the data type here
    private val navigator: NavController,
    private val mode: String
) :
    RecyclerView.Adapter<MyDoneAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.done_row, parent, false) // Update layout resource
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return doneWorkoutList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = doneWorkoutList[position]
        holder.workoutNameTextView.text = currentItem.workout
        holder.dateCompletedTextView.text = currentItem.date

        holder.cont.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("workout_name", currentItem.workout)
            navigator.navigate(R.id.action_profileFragment2_to_workoutDetailsFragment,bundle)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cont: ConstraintLayout = itemView.findViewById(R.id.cont)
        val workoutNameTextView: TextView = itemView.findViewById(R.id.string)
        val dateCompletedTextView: TextView = itemView.findViewById(R.id.date)
        // Additional UI elements can be referenced here if needed
    }
}
