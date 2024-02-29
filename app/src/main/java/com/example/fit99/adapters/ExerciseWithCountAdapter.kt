package com.example.fit99.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.squareup.picasso.Picasso

class ExerciseWithCountAdapter(
    private val exerciseListWithCount: List<ExerciseListFragment.ExerciseWithCount>,
    private val navigator: NavController
) : RecyclerView.Adapter<ExerciseWithCountAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercise_row, parent, false)
        return MyViewHolder(itemView)
    }

    fun getArr():List<ExerciseListFragment.ExerciseWithCount>{
        return exerciseListWithCount
    }
    override fun getItemCount(): Int = exerciseListWithCount.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exerciseListWithCount[position]
        holder.exerciseName.text = currentItem.exercise.name
        holder.exerciseCount.text = "${currentItem.count} in Workouts"

        var imageUrl = currentItem.exercise.visualURL.replace(".mp4", ".png")
        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Picasso.get().load(imageUrl).into(holder.thumbnail)

        holder.cont.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("exerciseName", currentItem.exercise.name)
            navigator.navigate(R.id.action_exerciseListFragment_to_exerciseDetailsFragment, bundle)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cont: ConstraintLayout = itemView.findViewById(R.id.equipmentLayout)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val exerciseCount: TextView = itemView.findViewById(R.id.count) // Assuming this TextView exists in your layout
    }
}
