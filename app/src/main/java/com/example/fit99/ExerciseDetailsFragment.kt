import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class ExerciseDetailsFragment : Fragment() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var stepRView: RecyclerView
    private lateinit var muscleRView: RecyclerView
    private lateinit var exercise : Exercise
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_details, container, false)
        val video = view.findViewById<VideoView>(R.id.video)
        val name = view.findViewById<TextView>(R.id.exercisedetailname)
        val contentLayout = view.findViewById<ConstraintLayout>(R.id.ex_detail)
        val weight : TextView =  view.findViewById(R.id.weight)
        stepRView = view.findViewById(R.id.steps_list)
        stepRView.layoutManager = LinearLayoutManager(activity)
        stepRView.setHasFixedSize(true)
        muscleRView = view.findViewById(R.id.muscle_list)
        muscleRView.layoutManager = LinearLayoutManager(activity)
        muscleRView.setHasFixedSize(true)
        contentLayout.visibility = View.INVISIBLE

        val exerciseName: String = requireArguments().getString("exerciseName", "Barbell bench Press")
        loadData(exerciseName, object : LoadDataCallback {
            override fun onExerciseLoaded(exercise: Exercise?) {
                if (exercise != null) {
                    name.text = exercise.name
                    weight.text = "Recommended Weight For Beginners : \n${exercise.recommended_weight}"
                    stepRView.adapter = StepAdapter(exercise.steps)
                    muscleRView.adapter = MuscleAdapter(exercise.muscles)

                    val videoUri = Uri.parse(exercise.visualURL + "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc")
                    video.setVideoURI(videoUri)
                    video.setOnPreparedListener { mediaPlayer ->


                        mediaPlayer.isLooping = true
                        mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)

                        video.start()
                        contentLayout.visibility = View.VISIBLE
                    }

                } else {
                    // Handle the case where no exercise was found
                }
            }

            override fun onError(exception: Exception) {
                // Handle the error, such as displaying an error message
            }
        })

        return view
    }


    fun loadData(name: String, callback: LoadDataCallback) {
        val query: Query = db.collection("Exercises").whereEqualTo("name", name)
        query.get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                // Loop through the result documents
                for (document in documents) {
                    // Handle the document data here
                    val exercise = document.toObject(Exercise::class.java)
                    callback.onExerciseLoaded(exercise)
                    return@addOnSuccessListener
                }
                // No matching document found
                callback.onExerciseLoaded(null)
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                callback.onError(exception)
            }
    }


    interface LoadDataCallback {
        fun onExerciseLoaded(exercise: Exercise?)
        fun onError(exception: Exception)
    }
}
