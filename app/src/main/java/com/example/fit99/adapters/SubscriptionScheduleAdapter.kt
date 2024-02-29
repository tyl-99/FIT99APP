import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Schedule

class SubscriptionScheduleAdapter(private val stringList: ArrayList<Schedule>, private val navigator: NavController,) :
    RecyclerView.Adapter<SubscriptionScheduleAdapter.MyViewHolder>() {


    private val clickedSchedules = HashSet<Schedule>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_schedule_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    fun getAllSchedules(): ArrayList<Schedule> {
        return ArrayList(stringList) // Return a copy of the schedule list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = stringList[position]
        holder.time.text = "${currentItem.starttime} - ${currentItem.endtime}"
        holder.date.text = currentItem.date.toString()

        updateViewAppearance(holder, currentItem)

        holder.cont.setOnClickListener {
            if (clickedSchedules.contains(currentItem)) {
                clickedSchedules.remove(currentItem)
            } else {
                clickedSchedules.add(currentItem)
            }
            updateViewAppearance(holder, currentItem)
        }
    }

    private fun updateViewAppearance(holder: MyViewHolder, schedule: Schedule) {
        if (clickedSchedules.contains(schedule)) {
            holder.cont.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorBlue3))
            holder.time.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorWhite))
            holder.date.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorWhite))
        } else {
            holder.cont.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorBlack))
            holder.time.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorBlue3))
            holder.date.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorBlue3))
        }
    }

    fun getClickedSchedules(): HashSet<Schedule> {
        return clickedSchedules
    }

    fun addSchedule(schedule: Schedule) {
        stringList.add(schedule)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cont: ConstraintLayout = itemView.findViewById(R.id.cont)
        val time: TextView = itemView.findViewById(R.id.count)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    fun updateData(newSchedules: ArrayList<Schedule>) {
        stringList.clear()
        stringList.addAll(newSchedules)
        notifyDataSetChanged()

    }
}
