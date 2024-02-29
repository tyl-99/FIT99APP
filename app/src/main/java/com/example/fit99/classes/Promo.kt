import java.util.*
import kotlin.collections.ArrayList

data class Promo(
    val promoCode: String = "",
    val dueDate: String = " ",
    val details: ArrayList<String> = arrayListOf(),
    val percentage:Int = 0
)
