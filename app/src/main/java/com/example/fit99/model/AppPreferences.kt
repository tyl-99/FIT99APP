import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun saveLoggedInStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun saveUserCredentials(email: String, password: String, name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userEmail", email)
        editor.putString("userPassword", password)
        editor.putString("name", name)
        editor.apply()
    }

    fun saveObject(key: String, array: List<Any>) {
        val gson = Gson()
        val json = gson.toJson(array)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = sharedPreferences.getString(key, null) ?: return null
        val gson = Gson()
        return gson.fromJson(json, clazz)
    }

    fun <T> getArray(key: String, clazz: Class<T>): ArrayList<T>? where T : Any {
        val json = sharedPreferences.getString(key, null) ?: return null
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, clazz).type
        return gson.fromJson(json, type)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("userEmail", null)
    }

    fun getUserPassword(): String? {
        return sharedPreferences.getString("userPassword", null)
    }

    fun getName(): String? {
        return sharedPreferences.getString("name", null)
    }

    fun <T> getValue(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("This type is not supported")
        }
    }

    // Clear all data stored in AppPreferences
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
