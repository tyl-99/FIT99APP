import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fit99.R
import com.example.fit99.classes.Equipment
import com.example.fit99.model.MyModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.DetectionAlternateAdapter


class EquipmentDetectionFragment : Fragment() {
    private val REQUEST_CAMERA_PERMISSION = 3
    private lateinit var imageD: Bitmap
    private lateinit var view: View
    private lateinit var model : MyModel
    private lateinit var equipmentList : ArrayList<String>
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var navigator : NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_equipment_detection, container, false)
        checkCameraPermission()

        equipmentList = arrayListOf(
            "Treadmill",
            "Smith Machine",
            "Lat Pulldown-Seated Row Dual Machine",
            "Pectoral Fly Machine",
            "Chest Press Hammer",
            "Multipurpose Machine",
            "Ab Rotation Machine",
            "Leg Press Machine",
            "Elliptical",
            "Stationary Bike",
            "Abs-Back Extension Dual Machine",
            "Rowing Machine",
            "Dipping Station",
            "Leg-Hamstring Curl Machine",
            "Seated Calf Raise",
            "Leg Abduction Machine",
            "Preacher Curl",
            "Assisted Dips and Pull Ups Station",
            "Dumbbell",
            "Barbell",
            "Shoulder Press Machine",
            "Gym Bench",
            "Lateral Raise Machine",
            "Squat Rack",


        )
        navigator = findNavController()
        val gallery = view.findViewById<TextView>(R.id.gallery)
        val camera = view.findViewById<TextView>(R.id.camera)
        val detect = view.findViewById<Button>(R.id.detect)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollview)
        val loading = view.findViewById<ImageView>(R.id.loading)
        val result = view.findViewById<ConstraintLayout>(R.id.resullt)
        val status = view.findViewById<TextView>(R.id.status)
        val alternate= view.findViewById<ConstraintLayout>(R.id.alternate)
        val alternateEquipment = view.findViewById<RecyclerView>(R.id.alternateEquipment)
        alternateEquipment.layoutManager = LinearLayoutManager(activity);
        alternateEquipment.setHasFixedSize(true)
        Glide.with(this).load(R.drawable.loading).into(loading)

        gallery.setOnClickListener {
            openGallery()
        }

        camera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        detect.setOnClickListener {

            if(!::imageD.isInitialized){

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alert")
                    .setMessage("Please Upload An Image To Continue")
                    .setPositiveButton("OK") { dialog, id ->

                    }

               val dialog =  builder.create()
                dialog.show()
                return@setOnClickListener
            }else{
                var shouldDetect = false
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmation")
                    .setMessage("Are You Sure to Detect The Image?")
                    .setPositiveButton("Yes") { dialog, id ->

                        status.visibility = View.VISIBLE
                        status.text = "AI Detection In Progress.........."
                        result.visibility = View.GONE
                        alternate.visibility = View.GONE
                        scrollView.post {
                            scrollView.fullScroll(View.FOCUS_DOWN)
                        }
                        loading.visibility = View.VISIBLE

                        val inputData = model.preprocessImage(imageD)
                        val output = model.runInference(inputData)
                        val percentage = output.map{it*100}



                        var equipment = ""
                        if(percentage.max()>70){
                            equipment = equipmentList.get(percentage.indexOf(percentage.max()) )


                            Log.d("Jinitaimei",equipment)
                            loadData(equipment, object : LoadDataCallback {
                                override fun onEquipmentLoaded(equipment: Equipment) {
                                    Log.d("jinijini","bangkala")
                                    if (equipment != null) {
                                        var image = view.findViewById<ImageView>(R.id.resultimg)
                                        var name = view.findViewById<TextView>(R.id.eqName)
                                        var desc = view.findViewById<TextView>(R.id.resultDesc)

                                        var imageUrl = equipment.imageURL
                                        imageUrl += ".png?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
                                        Log.e(TAG,imageUrl)
                                        Picasso.get().load(imageUrl).into(image)

                                        name.text = equipment.name
                                        desc.text = equipment.description.take(95)+"..."

                                        result.setOnClickListener {
                                            val bundle = Bundle()
                                            bundle.putString("equipmentName", equipment.name)
                                            navigator.navigate(R.id.action_equipmentDetectionFragment_to_equipmentDetailsFragment, bundle)
                                        }


                                        GlobalScope.launch(Dispatchers.Main) {
                                            // Introduce a 3-second delay
                                            delay(3000)
                                            status.text = "Detection Complete"
                                            loading.visibility = View.INVISIBLE
                                            result.visibility = View.VISIBLE
                                        }


                                    } else {
                                        // Handle the case where no exercise was found
                                    }
                                }

                                override fun onError(exception: Exception) {
                                    // Handle the error, such as displaying an error message
                                }
                            })

                        }else{
                            val indexedPercentage = percentage.mapIndexed { index, value -> Pair(index, value) }
                            val highestThree = indexedPercentage.sortedByDescending { it.second }.take(3)
                            val alternateEquipments = ArrayList<String>()

                            for(three in highestThree){
                                alternateEquipments.add(equipmentList.get(three.first))
                            }


                            Log.d("jini",   alternateEquipments.toString())
                            val myEquipments = ArrayList<Equipment>()
                            for(equipment in alternateEquipments) {

                                loadData(equipment, object : LoadDataCallback {
                                    override fun onEquipmentLoaded(equipment: Equipment) {
                                        if (equipment != null) {
                                            myEquipments.add(equipment)
                                            Log.d("jini",   myEquipments.size.toString())

                                        }
                                        if(myEquipments.size==3){

                                            alternateEquipment.adapter = DetectionAlternateAdapter(myEquipments, navigator)
                                        }
                                    }

                                    override fun onError(exception: Exception) {
                                        // Handle the error, such as displaying an error message
                                    }
                                })
                            }
                            GlobalScope.launch(Dispatchers.Main) {
                                // Introduce a 3-second delay
                                delay(3000)
                                status.text = "No Equipment Detected"
                                loading.visibility = View.INVISIBLE
                                alternate.visibility = View.VISIBLE


                            }

                        }

                    }
                    .setNegativeButton("No") { dialog, id ->
                        shouldDetect = false
                        dialog.dismiss()

                    }
                val dialog =  builder.create()
                dialog.show()
            }



        }

        model = MyModel(requireContext(), "fit99v2.tflite")
        return view
    }

    private fun dispatchTakePictureIntent() {
        ImagePicker.with(this).cameraOnly().crop().start()
    }

    private fun openGallery() {
        ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*"))
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val image = view.findViewById<ImageView>(R.id.uploadImage)
            val uri: Uri = data?.data!!
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            imageD = bitmap


            image.setImageURI(uri)
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    fun loadData(name: String, callback: LoadDataCallback) {
        val query: Query = db.collection("Equipments").whereEqualTo("name", name)
        query.get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                // Loop through the result documents
                for (document in documents) {
                    // Handle the document data here
                    val equipment = document.toObject(Equipment::class.java)
                    callback.onEquipmentLoaded(equipment)
                    return@addOnSuccessListener
                }

            }
            .addOnFailureListener { exception ->
                // Handle errors here
                callback.onError(exception)
            }
    }


    interface LoadDataCallback {
        fun onEquipmentLoaded(equipment: Equipment)
        fun onError(exception: Exception)
    }

}