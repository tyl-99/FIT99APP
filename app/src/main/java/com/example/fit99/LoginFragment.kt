import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fit99.Home
import com.example.fit99.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val sharedPreferencesHelper by lazy {
        SharedPreferencesHelper(requireContext())
    }

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgot: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login)
        forgot = view.findViewById(R.id.forgot)
        val image : ImageView = view.findViewById(R.id.imageView3)
        val coach : TextView = view.findViewById(R.id.logincoach)
        val registration : TextView = view.findViewById(R.id.signup)

        setupPasswordToggle(view.findViewById(R.id.password))


        registration.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registrationFragment)
        }


        forgot.setOnClickListener {
            showEmailDialog()
        }
        val db = FirebaseFirestore.getInstance()
        val appPreferences = AppPreferences(requireContext())

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().replace("\\s+".toRegex(), "")
            val password = passwordEditText.text.toString()
            Log.d("Jinitaimei",email)
            db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // User with this email exists in Firestore
                        val userDocument = documents.first()
                        val storedPassword = userDocument.getString("password")
                        val name = userDocument.getString("name")

                        if (storedPassword == password) {
                            appPreferences.saveLoggedInStatus(true)
                            if (name != null) {
                                appPreferences.saveUserCredentials(email, password, name)
                            }

                            getFCMToken { fcmToken ->
                                if (fcmToken != null) {
                                    val userRef = db.collection("Users").document(userDocument.id)
                                    userRef
                                        .update("fcm", fcmToken)
                                        .addOnSuccessListener {
                                            // FCM token updated successfully
                                            val intent = Intent(activity, Home::class.java)
                                            startActivity(intent)
                                            activity?.finish()
                                            Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            // Handle errors during the Firestore update
                                            Toast.makeText(context, "Error updating FCM token: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // FCM token is null, handle this case if necessary
                                    Toast.makeText(context, "FCM token is null", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            // Passwords don't match, handle the error
                            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // User with this email doesn't exist in Firestore
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during the query
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun getFCMToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener(OnSuccessListener { token ->

                callback(token)
            })
    }

    private fun showEmailDialog() {
        val emailInput = EditText(context)
        emailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailInput.hint = "Enter your email"

        AlertDialog.Builder(context)
            .setTitle("Reset Password")
            .setView(emailInput)
            .setPositiveButton("Next") { dialog, _ ->
                val email = emailInput.text.toString()
                sendVerificationCode(email)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
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


    private fun sendVerificationCode(email: String) {
        Log.d("Login","jini")
        val verificationCode = generateVerificationCode()
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
                    activity?.runOnUiThread {
                        // Show verification code dialog on successful email sending
                        showVerificationCodeDialog(email, verificationCode)
                        Log.d("EmailSend", "Verification email sent successfully.")
                    }
                } catch (e: MessagingException) {
                    e.printStackTrace()
                    activity?.runOnUiThread {
                        // Display or log detailed error message
                        Toast.makeText(context, "Email sending failed: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("EmailSendError", "MessagingException: ${e.message}", e)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread {
                        // Handle other exceptions
                        Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("EmailSendError", "Exception: ${e.message}", e)
                    }
                }
            }.start()


        } catch (e: MessagingException) {
            e.printStackTrace()

        }
    }


    private fun generateVerificationCode(): String {
        return List(6) { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
    }

    private fun showVerificationCodeDialog(email: String, verificationCode: String) {
        val codeInput = EditText(context)
        codeInput.inputType = InputType.TYPE_CLASS_TEXT
        codeInput.hint = "Enter verification code"

        AlertDialog.Builder(context)
            .setTitle("Enter Verification Code")
            .setView(codeInput)
            .setPositiveButton("Verify") { dialog, _ ->
                val inputCode = codeInput.text.toString()
                if (inputCode == verificationCode) {
                    showPasswordResetDialog(email)
                } else {
                    Toast.makeText(context, "Incorrect verification code", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showPasswordResetDialog(email: String) {
        val newPasswordInput = EditText(context)
        newPasswordInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordInput.hint = "New Password"

        val confirmNewPasswordInput = EditText(context)
        confirmNewPasswordInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmNewPasswordInput.hint = "Confirm New Password"

        AlertDialog.Builder(context)
            .setTitle("Set New Password")
            .setView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(newPasswordInput)
                addView(confirmNewPasswordInput)
            })
            .setPositiveButton("Update") { dialog, _ ->
                val newPassword = newPasswordInput.text.toString()
                val confirmNewPassword = confirmNewPasswordInput.text.toString()

                if (newPassword == confirmNewPassword) {
                    updatePasswordInFirestore(email, newPassword)
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun updatePasswordInFirestore(email: String, newPassword: String) {
        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents.first()
                    userDocument.reference
                        .update("password", newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // User not found
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to retrieve data
                Toast.makeText(context, "Error finding user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
