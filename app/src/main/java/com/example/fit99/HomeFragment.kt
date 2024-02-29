import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.Equipment
import com.example.fit99.classes.Preview
import com.example.fit99.classes.WorkoutExercise
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var exercisesAdapter: HomeExerciseAdapter
    private lateinit var equipmentAdapter: PreviewAdapter
    private var exercisesList = mutableListOf<Preview>()
    private val equipmentList = mutableListOf<Preview>()
    private val exerciseFrequencyMap = mutableMapOf<String, Int>()
    private lateinit var exercisesRecyclerView : RecyclerView


    override fun onResume() {
        super.onResume()
        exercisesList.clear()
        equipmentList.clear()
        loadWorkoutsAndCountExercises()
        loadEquipmentDataFromFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerViews
        exercisesRecyclerView = view.findViewById<RecyclerView>(R.id.exercises)
        val equipmentRecyclerView = view.findViewById<RecyclerView>(R.id.equipments)

        val moreExercisesTextView = view.findViewById<TextView>(R.id.moreExercises)
        val moreEquipmentsTextView = view.findViewById<TextView>(R.id.moreEquipments)
        val detectButton = view.findViewById<Button>(R.id.detectBtn)
        val findButton = view.findViewById<Button>(R.id.findBtn)

        // Set onClickListeners
        moreExercisesTextView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_exerciseListFragment)
        }

        moreEquipmentsTextView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_equipmentListFragment)
        }

        detectButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_equipmentDetectionFragment)
        }

        findButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_exerciseMainFragment)
        }

        exercisesRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        equipmentRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        exercisesAdapter = HomeExerciseAdapter(requireContext(), exercisesList,findNavController())
        equipmentAdapter = PreviewAdapter(requireContext(), equipmentList,findNavController())

        exercisesRecyclerView.adapter = exercisesAdapter
        equipmentRecyclerView.adapter = equipmentAdapter


        return view
    }



    private fun loadExerciseDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val exercisesRef = db.collection("Exercises").limit(5)

        exercisesRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val visualURL = document.getString("visualURL") ?: ""
                    var imageUrl = visualURL.replace(".mp4",".png")
                    imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"

                    val exercise = Preview(name, imageUrl)
                    exercisesList.add(exercise)
                }

                exercisesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }

    private fun loadWorkoutsAndCountExercises() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Workouts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val exercises = document.get("exercises") as? List<Map<String, Any>> ?: continue
                    for (exerciseMap in exercises) {
                        val exerciseName = exerciseMap["exerciseName"] as? String ?: continue
                        exerciseFrequencyMap[exerciseName] = exerciseFrequencyMap.getOrDefault(exerciseName, 0) + 1
                    }
                }
                displayTopExercises()
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun displayTopExercises() {
        val topExerciseEntries = exerciseFrequencyMap.entries.sortedByDescending { it.value }.take(5)
        Log.d("home", "Top exercise entries: $topExerciseEntries")

        val topExercises = ArrayList<Exercise>()
        val exercisesCollection = FirebaseFirestore.getInstance().collection("Exercises")

        // Track the number of completed Firestore queries
        var completedQueries = 0

        for (entry in topExerciseEntries) {
            val exerciseName = entry.key

            // Fetch exercise details from Firestore
            exercisesCollection.document(exerciseName).get().addOnSuccessListener { document ->
                if (document != null) {
                    val exercise = document.toObject(Exercise::class.java)
                    exercise?.let { topExercises.add(it) }
                } else {
                    Log.d("home", "No exercise found for: $exerciseName")
                }

                completedQueries++
                if (completedQueries == topExerciseEntries.size) {
                    // All Firestore queries are complete, now update the RecyclerView
                    updateRecyclerView(topExercises)
                }
            }.addOnFailureListener { e ->
                Log.w("home", "Error fetching exercise details", e)
                completedQueries++
                if (completedQueries == topExerciseEntries.size) {
                    updateRecyclerView(topExercises)
                }
            }
        }
    }

    private fun updateRecyclerView(exercises: ArrayList<Exercise>) {
        val previews = exercises.map { exercise ->
            val imageUrl = constructImageUrlForExercise(exercise.visualURL)
            Preview(exercise.name, imageUrl)
        }.toMutableList()

        Log.d("home", "Exercise previews: $previews")

        exercisesList = previews
        exercisesRecyclerView.adapter = HomeExerciseAdapter(requireContext(), previews,findNavController())
    }


    // Function to construct the image URL for a given exercise name
    private fun constructImageUrlForExercise(exerciseName: String): String {
        var imageUrl = "$exerciseName".replace(".mp4", ".png")
        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Log.d("jini",imageUrl)
        return imageUrl
    }


    private fun loadEquipmentDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val equipmentRef = db.collection("Equipments").limit(5)

        equipmentRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    var imageURL = document.getString("imageURL") ?: ""
                    imageURL += ".png?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"

                    val equipment = Preview(name, imageURL)
                    equipmentList.add(equipment)
                }

                equipmentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }
}
