import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R

class AnnouncementAdapter(private val onItemClicked: (String) -> Unit) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    private var announcements = listOf<Pair<String, Announcement>>()

    fun setAnnouncements(data: List<Pair<String, Announcement>>) {
        announcements = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.announcement_row, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcementPair = announcements[position]
        holder.bind(announcementPair.second)

        // Set an OnClickListener on the ConstraintLayout
        holder.cont.setOnClickListener {
            onItemClicked(announcementPair.first)

        }
    }

    override fun getItemCount() = announcements.size

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        val cont: ConstraintLayout = itemView.findViewById(R.id.cont)

        fun bind(announcement: Announcement) {
            titleTextView.text = announcement.title
            dateTextView.text = announcement.getDateOnly()
        }
    }
}

