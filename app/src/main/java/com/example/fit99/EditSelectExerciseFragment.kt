import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.WorkoutExercise
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.selects.selectUnbiased

class EditSelectExerciseFragment : Fragment(), CategoryAdapter.CategoryClickListener,SelectExerciseAdapter.ExerciseClickListener {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var catRecyclerView: RecyclerView
    private lateinit var exerciseList : ArrayList<Exercise>
    private lateinit var categoryAdapter : CategoryAdapter
    private lateinit var navigator : NavController
    private lateinit var selectAdapter :EditSelectExerciseAdapter
    private lateinit var noSelected: TextView
    private lateinit var next : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_select_exercise, container, false)
        super.onViewCreated(view, savedInstanceState)

        val workoutExercises: ArrayList<WorkoutExercise> = arguments?.getParcelableArrayList("workoutExercisesKey") ?: arrayListOf()
        noSelected = view.findViewById<TextView>(R.id.noSelected)
        next=view.findViewById(R.id.next)
        navigator = findNavController()
        exerciseList =  ArrayList<Exercise>()
        newRecyclerView = view.findViewById(R.id.exercise_list)
        newRecyclerView.layoutManager = LinearLayoutManager(activity);
        newRecyclerView.setHasFixedSize(true)
        catRecyclerView = view.findViewById(R.id.filter_cat)
        catRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        catRecyclerView.setHasFixedSize(true)
        selectAdapter = EditSelectExerciseAdapter(exerciseList,navigator,this,workoutExercises)
        newRecyclerView.adapter = selectAdapter



        val catList = arrayListOf<String>(
            "Abs",
            "Shoulder",
            "Leg",
            "Chest",
            "Calves",
            "Cardio and Stretch",
            "Forearm",
            "Back",
            "Biceps"
        )
        next.setOnClickListener {
            val selectedExercise = selectAdapter.getSelectedExercise()

            // Iterate through selected exercises and check if they exist in workoutExercises
            for (exerciseName in selectedExercise) {
                val exists = workoutExercises.any { it.exerciseName == exerciseName }
                if (!exists) {
                    // If the exercise name is not in workoutExercises, create a new WorkoutExercise
                    val newWorkoutExercise = WorkoutExercise(exerciseName = exerciseName)
                    workoutExercises.add(newWorkoutExercise)
                }
            }

            // Proceed with your existing logic
            val bundle = Bundle()
            bundle.putParcelableArrayList("selectedExercise",workoutExercises)
            bundle.putString("workoutd",requireArguments().getString("workoutd").toString())

            navigator.navigate(R.id.action_editSelectExerciseFragment_to_editWorkoutFragment, bundle)
        }


        var selectedArr : ArrayList<String> = ArrayList<String>()


        categoryAdapter = CategoryAdapter(catList,selectedArr)
        categoryAdapter.setCategoryClickListener(this)
        catRecyclerView.adapter = categoryAdapter

        loadData(navigator, "",true)
        val searchEditText = view.findViewById<EditText>(R.id.searchbar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                loadData(navigator,s.toString(),false)
                selectAdapter.updateExerciseList(exerciseList)
                selectAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        return view
    }

    private fun loadData(navigator : NavController, searchQuery:String, redirect:Boolean){
        db.collection("Exercises")
            .get()
            .addOnSuccessListener { documents ->
                exerciseList.clear()
                for (document in documents) {
                    val exercise = document.toObject(Exercise::class.java)
                    exerciseList.add(exercise)
                }
                if(searchQuery != ""){
                    exerciseList = filterExerciseList(searchQuery)
                }
                selectAdapter.updateExerciseList(exerciseList)
                selectAdapter.notifyDataSetChanged()


            }
            .addOnFailureListener { exception ->
                // Handle the error here
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }


    override fun onCategoryClicked(category: String) {

        val selectedCategories = categoryAdapter.getSelectedCat()

        if(selectedCategories.size==0){
            selectAdapter.updateExerciseList(exerciseList)
            selectAdapter.notifyDataSetChanged()
        }
        else{
            Log.d(TAG,exerciseList.size.toString())
            val filteredExerciseList = exerciseList.filter { exercise ->
                exercise.category in selectedCategories
            }
            val filteredExerciseArrayList = ArrayList(filteredExerciseList)
            Log.d(TAG,filteredExerciseArrayList.size.toString())
            selectAdapter.updateExerciseList(filteredExerciseArrayList)
            selectAdapter.notifyDataSetChanged()
        }

    }

    private fun filterExerciseList(searchQuery: String): ArrayList<Exercise> {
        if (searchQuery.isNotBlank()) {
            return exerciseList.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true)
            } as ArrayList<Exercise>
        } else {
            return ArrayList() // Return an empty list when search query is blank
        }
    }

    override fun onExerciseClicked(selectedExercise: ArrayList<String>) {
        if(selectedExercise.size >0){
            noSelected.visibility=View.VISIBLE
            next.visibility =View.VISIBLE
            noSelected.text = selectedExercise.size.toString()+ " Exercises Selected"
        }
        else{
            noSelected.visibility=View.INVISIBLE
            next.visibility =View.GONE
        }
    }
}

