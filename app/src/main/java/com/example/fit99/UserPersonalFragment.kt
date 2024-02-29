import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.fit99.Authentication
import com.example.fit99.R
import com.example.fit99.classes.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class UserPersonalFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var male: TextView
    private lateinit var password: TextView
    private lateinit var female: TextView
    private lateinit var name: TextView
    private lateinit var updateButton: Button
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private var selectedGender: String = "Male"
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var verificationCode = ""
    private var getpassword = ""

    private lateinit var uri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_personal, container, false)

        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        imageView = view.findViewById(R.id.uploadImageView)
        password = view.findViewById(R.id.password)
        male = view.findViewById(R.id.male)
        female = view.findViewById(R.id.female)
        name= view.findViewById(R.id.name)
        updateButton = view.findViewById(R.id.submitButton)
        heightEditText = view.findViewById(R.id.height)
        weightEditText = view.findViewById(R.id.weight)
        val appPreferences = AppPreferences(requireContext())
        val userEmail = appPreferences.getUserEmail().toString()
        loadUserData(userEmail)

        val logout = view.findViewById<Button>(R.id.logout)

        password.setOnClickListener {
            sendVerificationCode(userEmail)
        }

        logout.setOnClickListener {

            val appPreferences = AppPreferences(requireContext())
            val email = appPreferences.getUserEmail().toString()
            appPreferences.logout()

            // Update the FCM token to "none"
            updateFCMTokenToNone(email) { success ->
                if (success) {
                    // FCM token updated to "none" successfully
                    val intent = Intent(requireActivity(), Authentication::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // Handle the case where updating the token to "none" failed
                    Toast.makeText(requireContext(), "Failed to update FCM token.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireActivity(), Authentication::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        getUserPasswordFromFirestore(userEmail) { password ->
            getpassword = password.toString()
        }


        imageView.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .galleryMimeTypes(arrayOf("image/*"))
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

        updateButton.setOnClickListener {
            val height = heightEditText.text.toString().toDoubleOrNull()
            val weight = weightEditText.text.toString().toDoubleOrNull()

            if (validateInputs(selectedGender, height, weight)) {
                updateUserInFirestore(userEmail, height, weight)
            }
        }

        return view
    }

    private fun loadUserData(userEmail: String) {
        db.collection("Users").whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.first().toObject(User::class.java)
                    name.text = user.name.toString()
                    selectedGender = user.gender ?: "Male"
                    heightEditText.setText(user.height?.toString() ?: "")
                    weightEditText.setText(user.weight?.toString() ?: "")
                    user.imageUrl?.let { imageUrl -> Picasso.get().load(imageUrl).into(imageView) }
                    toggleGenderSelection()
                } else {
                    Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load user data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVerificationCode(email: String) {
        verificationCode = generateVerificationCode()
        CoroutineScope(Dispatchers.IO).launch {
            sendEmail(
                email,
                "Your Verification Code",
                "Your verification code is: $verificationCode"
            )
        }
        // Show dialog to enter the verification code
        showVerificationCodeDialog(email)
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }

    private fun sendEmail(receiver: String, subject: String, messageBody: String) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("j9films9@gmail.com", "iypv jnmj gdyh brye") // Use your email and password
            }
        })

        try {
            MimeMessage(session).apply {
                setFrom(InternetAddress("j9films9@gmail.com"))
                addRecipient(Message.RecipientType.TO, InternetAddress(receiver))
                this.subject = subject
                setText(messageBody)
            }.also { Transport.send(it) }
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun showVerificationCodeDialog(email: String) {
        val context = requireContext()
        val montserratMedium = ResourcesCompat.getFont(context, R.font.montserrat_medium)

        // Create an EditText for the verification code input
        val verificationCodeInput = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Verification Code"
            typeface = montserratMedium
        }


        // Create and show the AlertDialog
        AlertDialog.Builder(context).apply {
            setTitle("Enter Verification Code")
            setView(verificationCodeInput)
            setPositiveButton("Verify") { dialog, _ ->
                val inputCode = verificationCodeInput.text.toString()
                if (inputCode == verificationCode) {
                    dialog.dismiss()
                    showPasswordChangeDialog(email)
                } else {
                    Toast.makeText(context, "Incorrect code", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            create().show()
        }
    }


    private fun showPasswordChangeDialog(email: String) {
        val context = requireContext()
        val montserratMedium = ResourcesCompat.getFont(context, R.font.montserrat_medium)
        val eyeDrawable = ContextCompat.getDrawable(context, R.drawable.view_dark) // Replace with your drawable resource

        // Function to setup password toggle
        @SuppressLint("ClickableViewAccessibility")
        fun setupPasswordToggle(editText: EditText) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeDrawable, null)
            editText.setOnTouchListener { v, event ->
                val drawableEnd = 2
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

        val currentPasswordInput = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Current Password"
            typeface = montserratMedium
            setupPasswordToggle(this)
        }

        val newPasswordInput = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "New Password"
            typeface = montserratMedium
            setupPasswordToggle(this)
        }

        val confirmNewPasswordInput = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Confirm New Password"
            typeface = montserratMedium
            setupPasswordToggle(this)
        }


        // Create a LinearLayout and add the EditTexts
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                resources.getDimensionPixelSize(R.dimen.dialog_margin),
                resources.getDimensionPixelSize(R.dimen.dialog_margin),
                resources.getDimensionPixelSize(R.dimen.dialog_margin),
                resources.getDimensionPixelSize(R.dimen.dialog_margin)
            )
            addView(currentPasswordInput)
            addView(newPasswordInput)
            addView(confirmNewPasswordInput)
        }

        AlertDialog.Builder(context).apply {
            setTitle("Change Password")
            setView(layout)
            setPositiveButton("Update") { dialog, _ ->
                val currentPassword = currentPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()
                val confirmNewPassword = confirmNewPasswordInput.text.toString()

                // Regex for password validation
                val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$".toRegex()

                when {
                    currentPassword != getpassword -> {
                        Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmNewPassword -> {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    !newPassword.matches(passwordRegex) -> {
                        showErrorDialog("Password must contain at least one uppercase letter, one lowercase letter, one special character, and must be at least 8 characters long.")
                    }
                    else -> {
                        updatePasswordInFirestore(email, newPassword)
                        dialog.dismiss()
                    }
                }
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            create().show()
        }
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

    private fun updatePasswordInFirestore(email: String, newPassword: String) {
        db.collection("Users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents.first()
                    db.collection("Users").document(userDocument.id)
                        .update("password", newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Password updated successfully.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error finding user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }





    private fun toggleGenderSelection() {
        if (selectedGender == "Male") {
            selectedGender = "Female"
            male.setBackgroundResource(R.drawable.gender_selected)
            female.setBackgroundResource(R.drawable.gender_shape)
            male.setTextColor(resources.getColor(R.color.white))
            female.setTextColor(resources.getColor(R.color.black))

        } else {
            selectedGender = "Male"

            male.setBackgroundResource(R.drawable.gender_shape)
            female.setBackgroundResource(R.drawable.gender_selected)
            male.setTextColor(resources.getColor(R.color.black))
            female.setTextColor(resources.getColor(R.color.white))
        }
    }



    private fun validateInputs(gender: String, height: Double?, weight: Double?): Boolean {
        if (gender.isBlank()) {
            Toast.makeText(context, "Please select a gender.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (height == null || height <= 0) {
            Toast.makeText(context, "Please enter a valid height.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (weight == null || weight <= 0) {
            Toast.makeText(context, "Please enter a valid weight.", Toast.LENGTH_SHORT).show()
            return false
        }
        // Add more validation rules if needed
        return true
    }


    private fun updateUserInFirestore(userEmail: String, height: Double?, weight: Double?) {
        val userUpdates = hashMapOf<String, Any>(
            "gender" to selectedGender,
            "height" to (height ?: 0.0),
            "weight" to (weight ?: 0.0)
        )

        Log.d("myemail",userEmail)

        db.collection("Users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // There should be only one document matching the email,
                    // so we can directly access it with querySnapshot.documents[0]
                    val userDocument = querySnapshot.documents[0]

                    // Update the user's data
                    userDocument.reference
                        .update(userUpdates)
                        .addOnSuccessListener {
                            Log.d("myemail","sui")
                            uri?.let { uri ->
                                Log.d("myemail","url done")
                                if(::uri.isInitialized){
                                    uploadImageToStorage(userEmail, uri)
                                }

                                Toast.makeText(
                                    context,
                                    "Profile updated successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update profile.", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("myemail","Failed")
                        }
                }
            }
    }

    private fun uploadImageToStorage(userEmail: String, uri: Uri) {
        val storageRef = storageReference.child("User/$userEmail.jpg")
        Log.d("myemail",userEmail.toString())
        storageRef.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(context, "Image uploaded successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            uri = data?.data!!
            uri?.let {
                imageView.setImageURI(uri)
            }
        }
    }

    private fun updateFCMTokenToNone(userEmail: String, callback: (Boolean) -> Unit) {
        val usersRef = db.collection("Users")
        val query = usersRef.whereEqualTo("email", userEmail)

        query.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.first()
                    val userId = userDocument.id

                    // Update the "fcm" field for the user document with the matching email
                    usersRef.document(userId)
                        .update("fcm", "none")
                        .addOnSuccessListener {
                            // FCM token updated to "none" successfully
                            callback(true)
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors during the Firestore update
                            callback(false)
                        }
                } else {
                    // Handle the case where no user with the provided email is found
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors during the Firestore query
                callback(false)
            }
    }

    private fun getUserPasswordFromFirestore(email: String, callback: (String?) -> Unit) {
        db.collection("Users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.first().toObject(User::class.java)
                    val password = user.password
                    callback(password)
                } else {
                    // User not found
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to retrieve data
                callback(null)
            }
    }





}
