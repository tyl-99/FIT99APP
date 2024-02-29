package com.example.fit99

import AppPreferences
import WorkoutAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.classes.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Query
import org.w3c.dom.Text


class workoutFragment : Fragment() {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var newRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        val add = view.findViewById<TextView>(R.id.add)
        val appPreferences = AppPreferences(requireContext())
        val email = appPreferences.getUserEmail()
        newRecyclerView = view.findViewById(R.id.workouts)
        newRecyclerView.layoutManager = LinearLayoutManager(activity);
        newRecyclerView.setHasFixedSize(true)

        add.setOnClickListener {
            findNavController().navigate(R.id.action_workoutFragment2_to_selectExerciseFragment)
        }

        loadWorkoutsByEmail(email.toString())
        return  view
    }



    private fun loadWorkoutsByEmail(email: String) {
        db.collection("Workouts")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                val workoutList = ArrayList<Workout>()

                for (document in documents) {
                    val workout = document.toObject(Workout::class.java)
                    workoutList.add(workout)
                }

                // Set the retrieved workout list as the data source for your RecyclerView
                newRecyclerView.adapter = WorkoutAdapter(workoutList, findNavController())
            }
            .addOnFailureListener { exception ->
                // Handle the error here
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }
}