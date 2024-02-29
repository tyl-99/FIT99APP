import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdminPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AdminPreferences", Context.MODE_PRIVATE)

    // Save the admin logged-in status
    fun saveAdminLoggedInStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdminLoggedIn", isLoggedIn)
        editor.apply()
    }

    // Check if an admin is logged in
    fun isAdminLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isAdminLoggedIn", false)
    }

    // Save admin credentials
    fun saveAdminCredentials(id: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("adminId", id)
        editor.putString("adminPassword", password)
        editor.apply()
    }

    // Retrieve admin ID
    fun getAdminId(): String? {
        return sharedPreferences.getString("adminId", null)
    }

    // Retrieve admin password
    fun getAdminPassword(): String? {
        return sharedPreferences.getString("adminPassword", null)
    }

    // Clear admin-related data
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove("isAdminLoggedIn")
        editor.remove("adminId")
        editor.remove("adminPassword")
        editor.apply()
    }


}
