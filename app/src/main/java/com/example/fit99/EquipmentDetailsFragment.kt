package com.example.fit99

import EquipmentStringAdapter
import ExerciseAdapter
import ExerciseStringAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fit99.classes.Equipment
import com.example.fit99.classes.Exercise
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class EquipmentDetailsFragment : Fragment() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var navigator : NavController
    private lateinit var exerciseR: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_equipment_details, container, false)
        val image : ImageView =  view.findViewById(R.id.eqDetailImage)
        val lly : ConstraintLayout =  view.findViewById(R.id.eqDet)


        lly.visibility = View.INVISIBLE
        exerciseR = view.findViewById(R.id.myexercises)
        exerciseR.layoutManager = LinearLayoutManager(activity);
        exerciseR.setHasFixedSize(true)
        val eqName : TextView = view.findViewById(R.id.eqDetailName)
        val desc : TextView = view.findViewById(R.id.eqDesc)
        //exerciseR =
        navigator = findNavController()
        val imageView: ImageView = view.findViewById(R.id.loading)
        Glide.with(this).load(R.drawable.loading).into(imageView)

        val equipmentName: String = requireArguments().getString("equipmentName", "Dumbbell")
        //val equipmentName: String = "Dumbbell"
        loadData(equipmentName, object : LoadDataCallback {
            override fun onExerciseLoaded(equipment : Equipment?, exerciseList:ArrayList<Exercise>) {

                if (equipment != null) {

                    eqName.text = equipment.name
                    desc.text = equipment.description
                    var imageUrl = equipment.imageURL.replace(".mp4","png")
                    imageUrl += ".png?alt=media&token=9b14d17b-77d7-40aa-8363-b80615d6ab83&_gl=1*1526093*_ga*NDQ0MjYyOTM3LjE2OTMzNTYwNDM.*_ga_CW55HF8NVT*MTY5Nzg3MTI1Ny40OS4xLjE2OTc4NzEyNzEuNDYuMC4w"

                    Picasso.get().load(imageUrl).into(image)
                    Log.e(TAG,exerciseList.size.toString())
                    exerciseR.adapter = EquipmentStringAdapter(exerciseList, navigator)
                    exerciseR.layoutParams.height = (120*2.73*exerciseList.size).toInt()



                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)

                        lly.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
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
        val query: Query = db.collection("Equipments").whereEqualTo("name", name)
        query.get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                val tasks = mutableListOf<Task<DocumentSnapshot>>()

                // Loop through the result documents
                for (document in documents) {
                    val equipment = document.toObject(Equipment::class.java)
                    val exerciseIds = equipment.exercises

                    for (exerciseId in exerciseIds) {
                        val task = db.collection("Exercises").document(exerciseId).get()
                        tasks.add(task)
                    }
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { exerciseSnapshots ->
                        val exerciseList = ArrayList<Exercise>()

                        for (exerciseSnapshot in exerciseSnapshots) {
                            val exercise = exerciseSnapshot.toObject(Exercise::class.java)
                            if (exercise != null) {
                                exerciseList.add(exercise)
                            }
                        }


                        // All exercises loaded, notify the callback with equipment and exercises
                        for (document in documents) {
                            val equipment = document.toObject(Equipment::class.java)

                            callback.onExerciseLoaded(equipment, exerciseList)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle errors here
                        callback.onError(exception)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                callback.onError(exception)
            }
    }




    interface LoadDataCallback {
        fun onExerciseLoaded(equipment: Equipment?, exerciseList: ArrayList<Exercise>)
        fun onError(exception: Exception)
    }


}
