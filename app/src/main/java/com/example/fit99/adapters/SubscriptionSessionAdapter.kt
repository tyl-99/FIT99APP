import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.SubscriptionSchedule

class SubscriptionSessionAdapter(
    private val schedules: List<SubscriptionSchedule>
) : RecyclerView.Adapter<SubscriptionSessionAdapter.SubscriptionSessionViewHolder>() {

    class SubscriptionSessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date)
        val endTimeTextView: TextView = view.findViewById(R.id.count)

        fun bind(schedule: SubscriptionSchedule) {
            dateTextView.text = schedule.date
            endTimeTextView.text = "${schedule.starttime} - ${schedule.endtime}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subscription_session_row, parent, false)
        return SubscriptionSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionSessionViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount(): Int = schedules.size
}
