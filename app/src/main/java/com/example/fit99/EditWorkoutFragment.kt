package com.example.fit99
import AppPreferences
import EditWorkoutExerciseDetailAdapter
import WorkoutExerciseAdapter
import WorkoutExerciseDetailAdapter
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.Workout
import com.example.fit99.classes.WorkoutExercise
import com.example.fit99.model.EditItemMoveCallback
import com.google.firebase.firestore.FirebaseFirestore


class EditWorkoutFragment : Fragment(), ItemMoveCallbackListener{
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: EditWorkoutExerciseDetailAdapter
    private lateinit var selectedExerciseView: RecyclerView
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var name : TextView
    val workout = Workout()


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        val view = inflater.inflate(com.example.fit99.R.layout.fragment_edit_workout, container, false)
        super.onViewCreated(view, savedInstanceState)

        val pyramid = view.findViewById<TextView>(com.example.fit99.R.id.pyramid)
        val cycle = view.findViewById<TextView>(com.example.fit99.R.id.cycle)
        val next = view.findViewById<TextView>(com.example.fit99.R.id.next)
        name = view.findViewById<EditText>(com.example.fit99.R.id.name)
        val del = view.findViewById<Button>(com.example.fit99.R.id.delete)



        selectedExerciseView = view.findViewById(com.example.fit99.R.id.workoutExercises)
        selectedExerciseView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        selectedExerciseView.setHasFixedSize(true)

       var workoutd = requireArguments().getString("workoutd").toString()
        //var selected = requireArguments().getString("selectedExercise").toString()

        del.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteWorkout(workoutd)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }



        val workoutBundle: ArrayList<WorkoutExercise> = arguments?.getParcelableArrayList("selectedExercise") ?: arrayListOf()
        val bundle = Bundle()
        Log.d("kere",workoutBundle.toString())
        loadData(workoutd,workoutBundle) { workout, workoutExercises, exercises ->
            Log.d("kere","ji${workoutExercises.size}ni${exercises.size}")
            bundle.putParcelableArrayList("workoutExercisesKey", ArrayList(workoutExercises))
            bundle.putString("workoutd",workoutd)


            if(workoutBundle.isEmpty()){

                adapter = EditWorkoutExerciseDetailAdapter(workoutBundle,ArrayList(exercises), requireActivity())
                selectedExerciseView.adapter = adapter
            }
            else{
                adapter = EditWorkoutExerciseDetailAdapter(workoutBundle,ArrayList(exercises), requireActivity())
                selectedExerciseView.adapter = adapter
            }


            if(workout.mode == "Pyramid"){
                pyramid.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_selected)
                pyramid.setTextColor(ContextCompat.getColor(pyramid.context, android.R.color.white))
                cycle.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_shape)
                cycle.setTextColor(ContextCompat.getColor(cycle.context, R.color.white))
                workout.mode = "Pyramid"
            }
            else{
                cycle.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_selected)
                cycle.setTextColor(ContextCompat.getColor(cycle.context, android.R.color.white))
                pyramid.setBackgroundResource(com.example.fit99.R.drawable.cat_filter_shape)
                pyramid.setTextColor(ContextCompat.getColor(pyramid.context, android.R.color.white))
                workout.mode = "Cycle"
            }

        }



        val addTextView = view.findViewById<TextView>(com.example.fit99.R.id.add)
        addTextView.setOnClickListener {
            findNavController().navigate(com.example.fit99.R.id.action_editWorkoutFragment_to_editSelectExerciseFragment, bundle)
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
            workout.mode = "Cycle"
        }

        itemTouchHelper = ItemTouchHelper(EditItemMoveCallback(this))
        itemTouchHelper.attachToRecyclerView(selectedExerciseView)


        next.setOnClickListener {
            var confirm  = true
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Edit")
                .setMessage("Are you sure you want to edit this workout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    if(!isValidName(name.text.toString())){
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Alert")
                            .setMessage("Please Enter Valid Workout Name")
                            .setPositiveButton("OK") { dialog, id ->

                            }

                        val dialog =  builder.create()
                        dialog.show()
                        return@setPositiveButton
                    }

                    Log.d("EditWorkout",workout.mode.toString())
                    workout.name = name.text.toString()
                    val myadapter : EditWorkoutExerciseDetailAdapter = selectedExerciseView.adapter as EditWorkoutExerciseDetailAdapter
                    val exerciseList = myadapter.getWorkoutExerciseList()



                    if(!validateWorkoutExercises(exerciseList)){
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Alert")
                            .setMessage("Please Ensure All The Exercise Details Are Complete!")
                            .setPositiveButton("OK") { dialog, id ->

                            }

                        val dialog =  builder.create()
                        dialog.show()
                        return@setPositiveButton
                    }
                    val appPreferences = AppPreferences(requireContext())
                    workout.email = appPreferences.getUserEmail().toString()

                    workout.exercises = exerciseList
                    val docRef = db.collection("Workouts").document(workoutd)

                    docRef.set(workout)
                        .addOnSuccessListener {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Alert")
                                .setMessage("Workout Successfully Edited!")
                                .setPositiveButton("OK") { dialog, id ->
                                    findNavController().navigate(com.example.fit99.R.id.action_editWorkoutFragment_to_workoutFragment2)
                                }

                            val dialog =  builder.create()
                            dialog.show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error writing document", e)
                        }
                }
                .setNegativeButton("No") { dialog, _ ->
                    confirm = false
                    dialog.dismiss()
                }
                .show()




        }



        return view
    }

    private fun deleteWorkout(workoutId: String) {
        db.collection("Workouts").document(workoutId)
            .delete()
            .addOnSuccessListener {
                Log.d("Delete", "Document successfully deleted!")
                Toast.makeText(context, "Workout successfully deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigate(com.example.fit99.R.id.action_editWorkoutFragment_to_workoutFragment2) // Replace with your navigation destination
            }
            .addOnFailureListener { e ->
                Log.w("Delete", "Error deleting document", e)
                Toast.makeText(context, "Error deleting workout", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData(workoutId: String,exercises :ArrayList<WorkoutExercise>, callback: (Workout, List<WorkoutExercise>, List<Exercise>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val appPreferences = AppPreferences(requireContext())
        val userEmail = appPreferences.getUserEmail()
        var exerciseNames : ArrayList<String> = ArrayList<String>()

        // Fetch the workout document using the workoutId and userEmail
        db.collection("Workouts")
            .document(workoutId)
            .get()
            .addOnSuccessListener { workoutDocument ->
                if (workoutDocument.exists() && workoutDocument.getString("email") == userEmail) {
                    name.setText(workoutDocument.getString("name").toString())
                    val exercisesRawData = workoutDocument.get("exercises") as? List<*>
                    val workoutExercise = mutableListOf<WorkoutExercise>()
                    exercisesRawData?.forEach { item ->
                        val workoutEx = convertMapToWorkoutExercise(item as? Map<String, Any>)
                        workoutEx?.let { workoutExercise.add(it) }
                    }
                    if(exercises.isEmpty()){
                        exerciseNames = ArrayList(workoutExercise.map { it.exerciseName })
                    }
                    else{
                        exerciseNames = ArrayList(exercises.map { it.exerciseName })

                    }


                    // Get the mode from the document
                    val mode = workoutDocument.getString("mode") ?: "DefaultMode" // Replace "DefaultMode" with your default

                    // Create a Workout object
                    val workout = userEmail?.let {
                        Workout(
                            name = workoutId,
                            email = it,
                            mode = mode,
                            exercises = ArrayList(workoutExercise)
                            // Populate other fields as necessary
                        )
                    } ?: Workout()

                    // Fetch the corresponding Exercise objects
                    db.collection("Exercises")
                        .whereIn("name", exerciseNames )
                        .get()
                        .addOnSuccessListener { documents ->
                            val exercises = documents.mapNotNull { it.toObject(Exercise::class.java) }

                            // Return the Workout object, workout exercises list, and exercises list via the callback
                            callback(workout, workoutExercise, exercises)
                        }
                        .addOnFailureListener {
                            callback(workout, emptyList(), emptyList())
                        }
                } else {
                    callback(Workout(), emptyList(), emptyList())
                }
            }
            .addOnFailureListener {
                callback(Workout(), emptyList(), emptyList())
            }
    }


    private fun convertMapToWorkoutExercise(map: Map<String, Any>?): WorkoutExercise? {
        if (map == null) return null
        return WorkoutExercise(
            exerciseName = map["exerciseName"] as? String ?: "",
            mode = map["mode"] as? String ?: "",
            reps = (map["reps"] as? Number)?.toInt() ?: 0,
            duration = (map["duration"] as? Number)?.toDouble() ?: 0.00,
            sets = (map["sets"] as? Number)?.toInt() ?: 0,
            interval = (map["interval"] as? Number)?.toInt() ?: 0
        )
    }




    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        (selectedExerciseView.adapter as? EditWorkoutExerciseDetailAdapter)?.onItemMove(fromPosition, toPosition)
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




