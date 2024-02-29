package com.example.fit99.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Experience

class ExperienceAdapter(private val experiences: MutableList<Experience>) :
    RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    class ExperienceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyEditText: TextView = view.findViewById(R.id.company)
        val positionEditText: TextView = view.findViewById(R.id.company2)
        val startYearEditText: TextView = view.findViewById(R.id.start)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.experience_form, parent, false)
        return ExperienceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = experiences[position]
        holder.companyEditText.setText(experience.company)
        holder.positionEditText.setText(experience.position)
        holder.startYearEditText.setText("${experience.startYear} - ${experience.endYear}")


    }

    fun getExperiences() : MutableList<Experience>{
        return experiences
    }

    override fun getItemCount(): Int = experiences.size
}
