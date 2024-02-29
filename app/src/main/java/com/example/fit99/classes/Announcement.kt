import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

data class Announcement(
    var title: String = "",
    var content: String = "",
    var date: String = ""
){
    fun getDateOnly(): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val parsedDate = parser.parse(date)
            formatter.format(parsedDate)
        } catch (e: Exception) {
            ""
        }
    }
}
