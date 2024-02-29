import com.example.fit99.classes.SubscriptionSchedule

data class Subscription(
    val userEmail: String = "",
    val coachId: String = "",
    val payment: Double = 0.0,
    val planName: String = "",
    val schedules: ArrayList<SubscriptionSchedule> = ArrayList<SubscriptionSchedule>(),
    val status: String = "",
    val dateSubscribed: String = ""
)
