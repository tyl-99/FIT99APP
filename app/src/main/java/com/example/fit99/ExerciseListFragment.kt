import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.adapters.ExerciseWithCountAdapter
import com.example.fit99.classes.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.A

class ExerciseListFragment : Fragment(), CategoryAdapter.CategoryClickListener {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var catRecyclerView: RecyclerView
    private lateinit var exerciseList: ArrayList<Exercise>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var navigator: NavController
    private lateinit var  exercisecount: List<ExerciseWithCount>
    private val exerciseCountMap = HashMap<String, Int>()
    private lateinit var spinner : Spinner


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_list, container, false)

        navigator = findNavController()
        exerciseList = ArrayList()
        newRecyclerView = view.findViewById(R.id.exercise_list)
        newRecyclerView.layoutManager = LinearLayoutManager(activity)
        newRecyclerView.setHasFixedSize(true)
        catRecyclerView = view.findViewById(R.id.filter_cat)
        catRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        catRecyclerView.setHasFixedSize(true)

        val catList = arrayListOf("Abs", "Shoulder", "Leg", "Chest", "Calves", "Cardio and Stretch", "Forearm", "Back", "Biceps")

        var selectedCategory = requireArguments().getString("selectedCategory", "")
        var selectedArr: ArrayList<String> = ArrayList()
        if (selectedCategory != "") {
            selectedArr.add(selectedCategory)

        }

        spinner = view.findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }




        categoryAdapter = CategoryAdapter(catList, selectedArr)
        categoryAdapter.setCategoryClickListener(this)
        catRecyclerView.adapter = categoryAdapter

        loadWorkoutsAndCountExercises()
        loadExerciseData()

        val searchEditText = view.findViewById<EditText>(R.id.searchbar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExerciseList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun sortByPopularity() {
        val sortedList = exercisecount.sortedWith(compareByDescending { it.count })
        updateRecyclerView(sortedList)
    }


    private fun sortAlphabetically(ascending: Boolean) {
        val sortedList = if (ascending) {
            exercisecount.sortedBy { it.exercise.name }
        } else {
            exercisecount.sortedByDescending { it.exercise.name }
        }
        updateRecyclerView(sortedList)
    }

    private fun updateRecyclerView(sortedList: List<ExerciseWithCount>) {
        newRecyclerView.adapter = ExerciseWithCountAdapter(
            sortedList,
            navigator
        )
    }


    private fun loadWorkoutsAndCountExercises() {
        db.collection("Workouts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val exercises = document.get("exercises") as? List<Map<String, Any>> ?: continue
                    for (exerciseMap in exercises) {
                        val exerciseName = exerciseMap["exerciseName"] as? String ?: continue
                        exerciseCountMap[exerciseName] = exerciseCountMap.getOrDefault(exerciseName, 0) + 1
                    }
                }
                Log.d("jini",exerciseCountMap.toString())
                updateAllExercisesCount()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }

    private fun updateAllExercisesCount() {
        db.collection("Exercises")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val exerciseName = document.getString("name") ?: continue
                    exerciseCountMap.putIfAbsent(exerciseName, 0)
                }
                updateAdapterWithData()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }

    fun loadExerciseData() {
        db.collection("Exercises")
            .get()
            .addOnSuccessListener { documents ->
                exerciseList.clear()
                for (document in documents) {
                    val exercise = document.toObject(Exercise::class.java)
                    exerciseList.add(exercise)
                }
                newRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }

    fun filterExerciseList(searchQuery: String) {
        val filteredList = if (searchQuery.isNotBlank()) {
            exerciseList.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            exerciseList
        }
        newRecyclerView.adapter = ExerciseWithCountAdapter(filteredList.map { exercise ->
            ExerciseWithCount(exercise, exerciseCountMap[exercise.name] ?: 0)
        }, navigator)
    }

    override fun onCategoryClicked(category: String) {
        val filteredList = if (category.isNotEmpty()) {
            exerciseList.filter { exercise ->
                exercise.category == category
            }
        } else {
            exerciseList
        }
        newRecyclerView.adapter = ExerciseWithCountAdapter(filteredList.map { exercise ->
            ExerciseWithCount(exercise, exerciseCountMap[exercise.name] ?: 0)
        }, navigator)
    }




    private fun updateAdapterWithData() {
        val exerciseListWithCount = exerciseList.map { exercise ->
            val count = exerciseCountMap[exercise.name] ?: 0
            ExerciseWithCount(exercise, count)
        }

        exercisecount = exerciseListWithCount

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "Popularity" -> sortByPopularity()
                    "A-Z Ascending" -> sortAlphabetically(ascending = true)
                    "A-Z Descending" -> sortAlphabetically(ascending = false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: handle the case where nothing is selected
            }
        }


        newRecyclerView.adapter = ExerciseWithCountAdapter(exerciseListWithCount, navigator)
    }


    data class ExerciseWithCount(val exercise: Exercise, val count: Int)
}


