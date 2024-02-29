package com.example.fit99
import AppPreferences
import WorkoutDetailMainAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.ExercisePreview
import com.example.fit99.classes.Workout
import com.example.fit99.classes.WorkoutExercise
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.atomic.AtomicInteger


class WorkoutDetailsFragment : Fragment(){
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: WorkoutDetailMainAdapter
    private lateinit var selectedExerciseView: RecyclerView
    private lateinit var selectedExercises : ArrayList<WorkoutExercise>
    private lateinit var editButton : Button
    //private lateinit var itemTouchHelper: ItemTouchHelper
    val workout = Workout()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        val view = inflater.inflate(com.example.fit99.R.layout.fragment_workout_details, container, false)
        super.onViewCreated(view, savedInstanceState)

        val appPreferences = AppPreferences(requireContext())
        val workout_name: String = requireArguments().getString("workout_name", "My Chest Workout")
        Log.d("jinitaimei",workout_name)
        editButton = view.findViewById<Button>(R.id.editBtn)


        val name = view.findViewById<TextView>(com.example.fit99.R.id.name)
        name.text = workout_name.toString()
        val button = view.findViewById<Button>(com.example.fit99.R.id.start)

        selectedExerciseView = view.findViewById(com.example.fit99.R.id.workoutExercises)
        selectedExerciseView.setHasFixedSize(true)

        lateinit var selectedExercise: ArrayList<String>

        if (arguments != null && requireArguments().containsKey("selectedExercise")) {
            selectedExercise = requireArguments().getStringArrayList("selectedExercise") as ArrayList<String>

            Log.d("Check Selected Exercise",selectedExercise.toString())
        }

        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("workout_name", workout_name)
            findNavController().navigate(R.id.action_workoutDetailsFragment_to_workoutSessionFragment, bundle)
        }

        loadData(
            workout_name,
            appPreferences.getUserEmail().toString(),
            { workout, documentId -> // Updated to receive the document ID
                Log.d("jinitaimei",workout.toString())
                if (workout != null) {


                }
            },
            { previews ->

                Log.d("Jini",previews.toString())
                adapter = WorkoutDetailMainAdapter(requireContext(), previews)

                selectedExerciseView.adapter = adapter
            }
        )

        return view
    }

    private fun loadData(
        workoutName: String,
        userId: String,
        workoutCallback: (Workout?, String?) -> Unit, // Callback includes Workout object and document ID
        previewsCallback: (List<ExercisePreview>) -> Unit
    ) {
        db.collection("Workouts")
            .whereEqualTo("name", workoutName)
            .whereEqualTo("email", userId)
            .get()
            .addOnSuccessListener { documents ->
                var workout: Workout? = null
                var documentId: String? = null // To store the document ID

                for (document in documents) {
                    workout = document.toObject(Workout::class.java)
                    documentId = document.id // Fetch the document ID

                    // Fetch exercise previews
                    val exercisePreviews = mutableListOf<ExercisePreview>()
                    val exercises = workout?.exercises

                    selectedExercises = workout.exercises

                    // Put the list of WorkoutExercise into a Bundle
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("selectedExercise", selectedExercises)

                    bundle.putString("workoutd", documentId) // Put the document ID into the bundle
                    editButton.setOnClickListener {
                        findNavController().navigate(R.id.action_workoutDetailsFragment_to_editWorkoutFragment, bundle)
                    }

                    val originalSequence = workout.exercises.map { it.exerciseName }
                    val detailsMap = mutableMapOf<String, ExercisePreview>()

                    if (exercises != null) {
                        val fetchCount = AtomicInteger(exercises.size) // To track the completion of fetch operations

                        for (exercise in exercises) {
                            val exerciseName = exercise.exerciseName

                            // Fetch visualURL from the Exercises collection by exerciseName
                            db.collection("Exercises")
                                .whereEqualTo("name", exerciseName)
                                .get()
                                .addOnSuccessListener { exerciseDocuments ->
                                    for (exerciseDocument in exerciseDocuments) {
                                        val visualURL = exerciseDocument.getString("visualURL")
                                        if (visualURL != null) {
                                            var jini = ""
                                            if(exercise.mode == "Reps"){
                                                jini = exercise.reps.toString()
                                            }
                                            else{
                                                jini = exercise.duration.toString()
                                            }
                                            val exercisePreview = ExercisePreview(
                                                name = exerciseName,
                                                imageResId = visualURL,
                                                mode = exercise.mode,
                                                reps = jini.toString()
                                            )
                                            detailsMap[exerciseName] = exercisePreview
                                        }
                                    }

                                    // Check if all fetch operations are completed
                                    if (fetchCount.decrementAndGet() == 0) {
                                        // Sort the previews based on the original sequence
                                        val sortedPreviews = originalSequence.mapNotNull { detailsMap[it] }
                                        previewsCallback(sortedPreviews)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("FirestoreError", "Error getting exercise documents: ", exception)
                                    workoutCallback(null, null)
                                }
                        }
                    } else {
                        // If no exercises are found
                        workoutCallback(workout, documentId)
                        previewsCallback(emptyList())
                    }
                    break // Assuming you are looking for a single document matching the criteria
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error here
                Log.e("FirestoreError", "Error getting documents: ", exception)
                workoutCallback(null, null) // Return null in case of an error
            }
    }












}


