import android.content.Context
import android.content.SharedPreferences

class CoachPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CoachPreferences", Context.MODE_PRIVATE)

    // Save the coach logged-in status
    fun saveCoachLoggedInStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isCoachLoggedIn", isLoggedIn)
        editor.apply()
    }

    // Check if a coach is logged in
    fun isCoachLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isCoachLoggedIn", false)
    }

    // Save coach credentials
    fun saveCoachCredentials(id: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("coachId", id)
        editor.putString("coachPassword", password)
        editor.apply()
    }

    // Retrieve coach ID
    fun getCoachId(): String? {
        return sharedPreferences.getString("coachId", null)
    }

    // Retrieve coach password
    fun getCoachPassword(): String? {
        return sharedPreferences.getString("coachPassword", null)
    }

    // Clear all data stored in CoachPreferences
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}

