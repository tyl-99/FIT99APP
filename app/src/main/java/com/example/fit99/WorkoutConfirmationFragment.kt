package com.example.fit99
import AppPreferences
import WorkoutExerciseDetailAdapter
import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.Workout
import com.example.fit99.classes.WorkoutExercise
import com.example.fit99.model.ItemMoveCallback
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore


class WorkoutConfirmationFragment : Fragment(), ItemMoveCallbackListener{
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: WorkoutExerciseDetailAdapter
    private lateinit var selectedExerciseView: RecyclerView
    private lateinit var itemTouchHelper: ItemTouchHelper
    val workout = Workout()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        val view = inflater.inflate(com.example.fit99.R.layout.fragment_workout_confirmation, container, false)
        super.onViewCreated(view, savedInstanceState)

        val pyramid = view.findViewById<TextView>(com.example.fit99.R.id.pyramid)
        val cycle = view.findViewById<TextView>(com.example.fit99.R.id.cycle)
        val next = view.findViewById<TextView>(com.example.fit99.R.id.next)
        val name = view.findViewById<EditText>(com.example.fit99.R.id.name)
        val modefaq = view.findViewById<ShapeableImageView>(com.example.fit99.R.id.mode_faq)



        selectedExerciseView = view.findViewById(com.example.fit99.R.id.workoutExercises)
        selectedExerciseView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        selectedExerciseView.setHasFixedSize(true)

        lateinit var selectedExercise: ArrayList<String>

        if (arguments != null && requireArguments().containsKey("selectedExercise")) {
            selectedExercise = requireArguments().getStringArrayList("selectedExercise") as ArrayList<String>

            Log.d("Check Selected Exercise",selectedExercise.toString())
        }

        modefaq.setOnClickListener{
            showInfoDialog("Pyramid/Cycle", "A Pyramid Workout involves varying your reps and weights in an ascending and then descending sequence for each exercise, combining both strength and endurance training.\n\nA Cycle Workout focuses on completing all sets of one exercise with consistent weight and repetitions before moving to the next exercise, allowing for concentrated training on specific muscle groups.")
        }

        loadData(selectedExercise) { exercises ->
            val exerciseArrayList = ArrayList(exercises)
            adapter = WorkoutExerciseDetailAdapter(exerciseArrayList, requireContext())
            selectedExerciseView.adapter = adapter

        }

        pyramid.setOnClickListener {
            pyramid.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_selected)
            pyramid.setTextColor(ContextCompat.getColor(it.context, android.R.color.white))
            cycle.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_shape)
            cycle.setTextColor(ContextCompat.getColor(it.context, R.color.white))
            workout.mode = "Pyramid"
        }

        cycle.setOnClickListener {
            cycle.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_selected)
            cycle.setTextColor(ContextCompat.getColor(it.context, android.R.color.white))
            pyramid.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_shape)
            pyramid.setTextColor(ContextCompat.getColor(it.context, android.R.color.white))
            workout.mode = "Pyramid"
        }

        itemTouchHelper = ItemTouchHelper(ItemMoveCallback(this))
        itemTouchHelper.attachToRecyclerView(selectedExerciseView)

        next.setOnClickListener {
            if(workout.mode == ""){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alert")
                    .setMessage("Please Select Workout Mode")
                    .setPositiveButton("OK") { dialog, id ->

                    }

                val dialog =  builder.create()
                dialog.show()
                return@setOnClickListener
            }
            if(!isValidName(name.text.toString())){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alert")
                    .setMessage("Please Enter Valid Workout Name")
                    .setPositiveButton("OK") { dialog, id ->

                    }

                val dialog =  builder.create()
                dialog.show()
                return@setOnClickListener
            }

            workout.name = name.text.toString()

            val exerciseList = adapter.getWorkoutExerciseList()



            if(!validateWorkoutExercises(exerciseList)){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alert")
                    .setMessage("Please Ensure All The Exercise Details Are Complete!")
                    .setPositiveButton("OK") { dialog, id ->

                    }

                val dialog =  builder.create()
                dialog.show()
                return@setOnClickListener
            }
            val appPreferences = AppPreferences(requireContext())
            workout.email = appPreferences.getUserEmail().toString()

            workout.exercises = exerciseList
            val docRef = db.collection("Workouts").document()

            docRef.set(workout)
                .addOnSuccessListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Alert")
                        .setMessage("Workout Successfully Created!")
                        .setPositiveButton("OK") { dialog, id ->
                            findNavController().navigate(com.example.fit99.R.id.action_workoutConfirmationFragment_to_workoutFragment2)
                        }

                    val dialog =  builder.create()
                    dialog.show()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error writing document", e)
                }


        }



        return view
    }


    private fun showInfoDialog(title: String, message: String) {
        val infoDialog = Dialog(requireContext())
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        infoDialog.setContentView(com.example.fit99.R.layout.simple_info_dialog) // assuming you have a layout file 'simple_info_dialog.xml'

        val titleView = infoDialog.findViewById<TextView>(com.example.fit99.R.id.infoDialogTitle)
        val messageView = infoDialog.findViewById<TextView>(com.example.fit99.R.id.infoDialogMessage)
        val buttonOk = infoDialog.findViewById<Button>(com.example.fit99.R.id.infoDialogButtonOk)

        titleView.text = title
        messageView.text = message
        buttonOk.setOnClickListener { infoDialog.dismiss() }

        infoDialog.show()
    }

    private fun loadData(selectedExercise: ArrayList<String>, callback: (List<Exercise>) -> Unit) {
        db.collection("Exercises")
            .whereIn("name", selectedExercise) // Filter by selected exercise names
            .get()
            .addOnSuccessListener { documents ->
                val exerciseList = mutableListOf<Exercise>()
                for (document in documents) {
                    val exercise = document.toObject(Exercise::class.java)
                    exerciseList.add(exercise)
                }

                callback(exerciseList)
            }
            .addOnFailureListener { exception ->
                // Handle the error here
                Log.e("FirestoreError", "Error getting documents: ", exception)
                callback(emptyList()) // Return an empty list in case of an error
            }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val adapter = selectedExerciseView.adapter as? WorkoutExerciseDetailAdapter
        adapter?.moveItem(fromPosition, toPosition)
    }

    fun isValidName(name: String): Boolean {
        if (name.isEmpty()) {
            return false
        }

        if (name.length < 3) {
            return false
        }


        return true
    }

    fun validateWorkoutExercises(workoutExercises: List<WorkoutExercise>): Boolean {
        for (exercise in workoutExercises) {
            // Check for empty exerciseName, sets, or interval
            if (exercise.exerciseName.isEmpty() || exercise.sets <= 0 || exercise.interval <= 0) {
                println("Error: ExerciseName, sets, or interval is invalid for exercise ${exercise.exerciseName}.")
                return false
            }

            // Check mode "Reps" and validate reps
            if (exercise.mode == "Reps" && exercise.reps <= 0) {
                println("Error: Reps cannot be zero or negative for exercise ${exercise.exerciseName} with mode Reps.")
                return false
            }

            // Check mode "Duration" and validate duration
            if (exercise.mode == "Duration" && exercise.duration <= 0) {
                println("Error: Duration cannot be zero or negative for exercise ${exercise.exerciseName} with mode Duration.")
                return false
            }
        }

        // All exercises are valid
        return true
    }




}

interface ItemMoveCallbackListener {
    fun onItemMove(fromPosition: Int, toPosition: Int)
}

