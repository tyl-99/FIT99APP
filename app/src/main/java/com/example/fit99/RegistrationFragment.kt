package com.example.fit99
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.service.autofill.UserData
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fit99.R
import com.example.fit99.classes.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Properties
import java.util.Random
import javax.mail.Authenticator
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.Message;


class RegistrationFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var male: TextView
    private lateinit var female: TextView
    private lateinit var button: Button
    private lateinit var myname: EditText
    private lateinit var myemail: EditText
    private lateinit var mypassword: EditText
    private lateinit var mypassword2: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private var selectedGender: String = "Male"
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var verificationCode: String = ""
    private var userData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        imageView = view.findViewById(R.id.imageView)
        male = view.findViewById(R.id.male)
        female = view.findViewById(R.id.female)
        button = view.findViewById(R.id.button)
        myname = view.findViewById(R.id.myname)
        myemail = view.findViewById(R.id.myemail)
        mypassword = view.findViewById(R.id.mypassword)
        mypassword2 = view.findViewById(R.id.mypassword2)
        heightEditText = view.findViewById(R.id.height)
        weightEditText = view.findViewById(R.id.weight)

        setupPasswordToggle(view.findViewById(R.id.mypassword))
        setupPasswordToggle(view.findViewById(R.id.mypassword2))

        imageView.setOnClickListener {
            ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*"))
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        male.setOnClickListener {
            selectedGender = "Male"
            toggleGenderSelection()
        }

        female.setOnClickListener {
            selectedGender = "Female"
            toggleGenderSelection()
        }

        button.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Register")
                .setMessage("Are you sure you want to Register")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    // Move the registration code here

                    val name = myname.text.toString()
                    val email = myemail.text.toString()
                    val password = mypassword.text.toString()
                    val confirmPassword = mypassword2.text.toString()



                    if (validateInputs(name, email, password, confirmPassword, selectedGender)) {

                        Log.d("userdata", "hahahai3")
                        // Initialize height and weight
                        val height: Double
                        val weight: Double

                        // Check if height and weight are not empty and are valid numbers
                        if (heightEditText.text.isNullOrEmpty() || weightEditText.text.isNullOrEmpty()) {
                            Toast.makeText(context, "Height and Weight cannot be empty", Toast.LENGTH_LONG).show()
                            return@setPositiveButton
                        } else {
                            try {
                                height = heightEditText.text.toString().toDouble()
                                weight = weightEditText.text.toString().toDouble()
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "Invalid number format for height or weight", Toast.LENGTH_LONG).show()
                                return@setPositiveButton
                            }
                        }

                        val uri = getImageUri()
                        if (uri == null) {
                            return@setPositiveButton
                        }
                        userData = User(name = name, email = email, password = password, gender = selectedGender, height = height.toDouble(), weight = weight)
                        Log.d("userdata", userData.toString())
                        generateAndSendVerificationCode(email)
                    }

                    Log.d("userdata", "hahahai4")
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }



        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupPasswordToggle(editText: EditText) {
        val drawableEnd = 2 // Index for drawableEnd
        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[drawableEnd].bounds.width())) {
                    // Toggle password visibility
                    editText.transformationMethod = if (editText.transformationMethod is PasswordTransformationMethod) {
                        HideReturnsTransformationMethod.getInstance()
                    } else {
                        PasswordTransformationMethod.getInstance()
                    }
                    // Move cursor to the end
                    editText.setSelection(editText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }



    private fun generateAndSendVerificationCode(email: String) {
        val random = Random()
        verificationCode = (100000 + random.nextInt(900000)).toString()

        val senderEmail = "j9films9@gmail.com" // Your email
        val password = "iypv jnmj gdyh brye" // Your email password

        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(email))
                subject = "Your Verification Code"
                setText("Your verification code is: ${verificationCode}")
            }

            Thread {
                try {
                    Transport.send(message)
                    activity?.runOnUiThread { showVerificationDialog() }
                } catch (e: MessagingException) {
                    e.printStackTrace()
                    // Handle failure in sending email
                    activity?.runOnUiThread {
                        showErrorDialog("Failed to send verification email. Please try again.")
                    }
                }
            }.start()
        } catch (e: MessagingException) {
            e.printStackTrace()
            // Handle preparation error
            activity?.runOnUiThread {
                showErrorDialog("Error preparing verification email. Please try again.")
            }
        }
    }

    private fun showVerificationDialog() {
        val codeInput = EditText(context)
        AlertDialog.Builder(requireContext())
            .setTitle("Verification")
            .setMessage("Enter the verification code sent to your email.")
            .setView(codeInput)
            .setPositiveButton("Verify") { _, _ ->
                if (codeInput.text.toString() == verificationCode) {
                    userData?.let { uploadImageToStorage(it) }
                } else {
                    showErrorDialog("Incorrect verification code.")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    @SuppressLint("ResourceAsColor")
    private fun toggleGenderSelection() {
        if (selectedGender == "Male") {
            male.setBackgroundResource(R.drawable.gender_selected)
            female.setBackgroundResource(R.drawable.gender_shape)
            male.setTextColor(R.color.white)
            female.setTextColor(R.color.black)

        } else {
            male.setBackgroundResource(R.drawable.gender_shape)
            female.setBackgroundResource(R.drawable.gender_selected)
            female.setTextColor(R.color.white)
            male.setTextColor(R.color.black)
        }
    }


    private fun uploadImageToStorage(user: User) {
        val uri = getImageUri() // Implement this function to get the image URI

        if (uri != null) {
            val storageRef = storageReference.child("User/${user.email}.jpg")
            val uploadTask = storageRef.putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    // Update the user's imageUrl with the download URL
                    user.imageUrl = downloadUri.toString()

                    // Save the updated user data to Firestore
                    saveUserDataToFirestore()
                } else {
                    // Handle the error
                }
            }
        }
    }


    private fun getImageUri(): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bytes = stream.toByteArray()

            // Create a temporary file to store the image
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val tempFile = File.createTempFile("temp_image", ".jpg", storageDir)

            // Write the bytes to the file
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(bytes)
            }

            // Get the file's URI
            return FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                tempFile
            )
        }

        return null
    }


    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        gender: String,
    ): Boolean {
        var success = false
        val maxHeight = 300.0
        val maxWeight = 300.0

        if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
        ) {
            showErrorDialog("Please fill in all the required fields.")
            return success
        }

        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$".toRegex()

        if (!password.matches(passwordRegex)) {
            showErrorDialog("Password must contain at least one uppercase letter, one lowercase letter, one special character, and must be at least 8 characters long.")
            return success
        }

        if (password != confirmPassword) {
            showErrorDialog("Passwords do not match.")
            return success
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorDialog("Please enter a valid email address.")
            return success
        }

        Log.d("userdata", "hahahai1")

        val query = db.collection("Users").get()
        val uniqueEmails = HashSet<String>()

        success = true
        query.addOnSuccessListener { documents ->
            for (document in documents) {
                val email = document.getString("email")
                if (email != null) {
                    if (uniqueEmails.contains(email)) {
                        success = false
                        showErrorDialog("Duplicate email found.")
                        return@addOnSuccessListener
                    } else {
                        uniqueEmails.add(email)
                        success = true
                    }
                }
            }

        }
        Log.d("userdata", "hahahai2")

        return success
    }


    private fun showErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Validation Error")
            .setMessage(errorMessage)
            .setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveUserDataToFirestore() {
        val user = userData
        Log.d("userdata",user.toString())
        val docRef = user?.name?.let { db.collection("Users").document(user.name.toString()) }

        if (docRef != null) {
            docRef.set(user)
                .addOnSuccessListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Success")
                        .setMessage("Account Created Successfully")
                        .setPositiveButton("OK") { dialog, id ->
                            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment2)
                        }

                    val dialog =  builder.create()
                    dialog.show()
                }
                .addOnFailureListener { e ->
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Error")
                        .setMessage("There was error during registration. Please try again!")
                        .setPositiveButton("OK") { dialog, id ->

                        }

                    val dialog =  builder.create()
                    dialog.show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)


            imageView.setImageURI(uri)
        }
    }
}
