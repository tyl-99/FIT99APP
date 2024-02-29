import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.User

class SubscriptionAdapter(
    private val subscriptions: List<Subscription>,
    private val user: List<User>,
    private val Ids: List<String>,
    private val onItemClicked: (String) -> Unit // Lambda function passed to the adapter
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    class SubscriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.name)
        val textViewDetail: TextView = view.findViewById(R.id.textView26)
        val cont: ConstraintLayout = view.findViewById(R.id.cont)

        // Pass 'onItemClicked' as a parameter to the 'bind' method
        fun bind(subscription: Subscription, user: User, id: String, onItemClicked: (String) -> Unit) {
            textViewName.text = subscription.planName
            textViewDetail.text = user.name
            cont.setOnClickListener {
                onItemClicked(id) // Invoke the listener
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lesson_row, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        // Pass 'onItemClicked' to the 'bind' method
        holder.bind(subscriptions[position], user[position], Ids[position], onItemClicked)
    }

    override fun getItemCount(): Int = subscriptions.size
}
