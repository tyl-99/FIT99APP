package com.example.fit99

import AppPreferences
import StringAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.adapters.MyDoneAdapter
import com.example.fit99.classes.DoneWorkout
import com.example.fit99.classes.ProfileWorkout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    private val REQUEST_SIGN_IN = 2

    private lateinit var stepsv :TextView
    private lateinit var distancev :TextView
    private lateinit var caloriesv :TextView
    private lateinit var bodyfatv :TextView

    // Configure the Google Fit API
    private val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        stepsv =view.findViewById(R.id.steps)
        distancev =view.findViewById(R.id.distance)
        caloriesv =view.findViewById(R.id.calories)
        bodyfatv =view.findViewById(R.id.fat)

        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (lastSignedInAccount == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/fitness.activity.read"))
                .requestScopes(Scope("https://www.googleapis.com/auth/fitness.location.read"))
                .requestEmail()
                .build()


            Log.d("profile jini",gso.toString())

            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_SIGN_IN)
        } else {
            // Check for permissions if already signed in
            Log.d("profile jini","Str8 Start")
            checkPermissionsAndFetchData(lastSignedInAccount)
        }

        setupUI(view)
        return view
    }

    private fun checkPermissionsAndFetchData(account: GoogleSignInAccount?) {
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            Log.d("profile jini","Ons")
            fetchDataFromGoogleFit()
        }
    }

    private fun fetchDataFromGoogleFit() {
        val account = GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_BODY_FAT_PERCENTAGE,DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(requireActivity(), account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    val stepDataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                    val distanceDataSet = bucket.getDataSet(DataType.AGGREGATE_DISTANCE_DELTA)
                    val caloriesDataSet = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED)
                    val fatDataSet = bucket.getDataSet(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)

                    var totalSteps = 0
                    var totalDistance = 0f // Distance in meters
                    var totalCalories = 0f

                    stepDataSet?.dataPoints?.forEach { dataPoint ->
                        totalSteps += dataPoint.getValue(Field.FIELD_STEPS).asInt()
                    }
                    stepsv.text = "$totalSteps Steps"


                    distanceDataSet?.dataPoints?.forEach { dataPoint ->

                        totalDistance += dataPoint.getValue(Field.FIELD_DISTANCE).asFloat()
                    }
                    val formattedDistance = String.format("%.2f", totalDistance / 1000)
                    distancev.text = "$formattedDistance km" // Convert meters to kilometers and format

                    caloriesDataSet?.dataPoints?.forEach { dataPoint ->
                        totalCalories += dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                    }
                    val formattedCalories = String.format("%.2f", totalCalories)
                    caloriesv.text = "$formattedCalories kcal"

                    var totalBodyFatPercentage = 0f
                    var dataPointCount = 0

                    fatDataSet?.dataPoints?.forEach { dataPoint ->
                        totalBodyFatPercentage += dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()
                        dataPointCount++
                    }

                    val averageBodyFatPercentage = if (dataPointCount > 0) {
                        totalBodyFatPercentage / dataPointCount
                    } else {
                        0f
                    }

                    bodyfatv.text = "Body Fat: ${String.format("%.0f", averageBodyFatPercentage)}%"

                    val fitnessData = hashMapOf(
                        "totalSteps" to totalSteps,
                        "averageDistanceKm" to String.format("%.2f", totalDistance / 1000), // converting to kilometers
                        "averageCalories" to String.format("%.2f", totalCalories),
                        "averageBodyFatPercentage" to if (dataPointCount > 0) String.format("%.2f", totalBodyFatPercentage / dataPointCount) else "0.00"
                    )


                    val appPreferences = AppPreferences(requireContext())
                    val userEmail = appPreferences.getUserEmail().toString()

                    val db = FirebaseFirestore.getInstance()

                    db.collection("Users")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                // Assuming only one document will match the email
                                val document = documents.documents.first()

                                // Get the "done" collection from the user's document
                                document.reference.collection("done")
                                    .orderBy("date", Query.Direction.DESCENDING) // Order by date in descending order
                                    .get()
                                    .addOnSuccessListener { doneDocuments ->
                                        val doneWorkoutList = ArrayList<DoneWorkout>()

                                        for (document in doneDocuments) {
                                            val date = document.getString("date") ?: ""
                                            val workoutName = document.getString("workout") ?: ""

                                            // Create a DoneWorkout object and add it to the list
                                            val doneWorkout = DoneWorkout(date, workoutName)
                                            doneWorkoutList.add(doneWorkout)
                                        }

                                        // Initialize and set up the RecyclerView with the doneWorkoutList
                                        val doneAdapter = MyDoneAdapter(doneWorkoutList, findNavController(), "mode")
                                        val recent = view?.findViewById<RecyclerView>(R.id.recentworkout)
                                        recent?.adapter = doneAdapter
                                    }



                                // Update the document with fitness data
                                document.reference.update(fitnessData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Document successfully updated with new fitness data")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error updating document", e)
                                    }
                            } else {
                                Log.d("Firestore", "No document found with the email $userEmail")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching document", e)
                        }





                }
            }
            .addOnFailureListener { e ->
                Log.e("profile jini", "There was a problem getting the fitness data.", e)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("profile jini", "requestCode: $requestCode, resultCode: ${resultCode==Activity.RESULT_OK}")
        if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                fetchDataFromGoogleFit()
            } else {
                Log.d("ProfileFragment", "Permission denied or request cancelled.")
            }
        } else if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } else {
                Log.d("ProfileFragment", "Sign-in failed.")
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            checkPermissionsAndFetchData(account)
        } catch (e: ApiException) {
            Log.w("ProfileFragment", "signInResult:failed code=" + e.statusCode)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI(view: View) {
        val setting: TextView = view.findViewById(R.id.settings)
        val propic = view.findViewById<ImageView>(R.id.propic)
        val name = view.findViewById<TextView>(R.id.name)
        val recent = view.findViewById<RecyclerView>(R.id.recentworkout)

        // Original UI setup code
        recent.layoutManager = LinearLayoutManager(activity)
        recent.setHasFixedSize(true)

        setting.setOnClickListener {
            //findNavController().navigate(R.id.action_profileFragment2_to_userPersonalFragment)
        }

        val appPreferences = AppPreferences(requireContext())
        val imageurl = "https://firebasestorage.googleapis.com/v0/b/fit99-9dacb.appspot.com/o/User%2F${appPreferences.getUserEmail()}.jpg?alt=media&token=0b498fee-36fd-4833-96bd-53ddb33ce78f"
        Picasso.get().load(imageurl).into(propic)

        Log.d("UserPic",imageurl)

        val currentDate = LocalDate.now()
        var lastsevenE = 0
        var lastsevenW = 0
        var lastthirtyE = 0
        var lastthirtyW = 0
        var workoutList = ArrayList<String>()
        var exerciseList = ArrayList<String>()

        val workoutArray: ArrayList<ProfileWorkout> = appPreferences.getArray("workoutExercise", ProfileWorkout::class.java) ?: arrayListOf()
        for(workout in workoutArray){
            workoutList.add(workout.name)
            if(workout.date.isBefore(currentDate.minusDays(7))){
                lastsevenW+=1
            }
            if(workout.date.isBefore(currentDate.minusDays(30))){
                lastthirtyW+= 1
            }
            for(exercise in workout.exercise){
                exerciseList.add(exercise.exerciseName)
                if(workout.date.isBefore(currentDate.minusDays(7))){
                    lastsevenE+=1
                }
                if(workout.date.isBefore(currentDate.minusDays(30))){
                    lastthirtyE+= 1
                }
            }
        }

        val frequencyMap = exerciseList.groupingBy { it }.eachCount()
        exerciseList = ArrayList(exerciseList.sortedWith(compareByDescending<String> { frequencyMap[it] }.thenBy { it }))

        name.text = appPreferences.getName().toString()

    }

}
