import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.squareup.picasso.Picasso

class AnnouncementListAdapter(
    private val announcementPairs: List<Pair<String, Announcement>>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<AnnouncementListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView6)
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.announcement_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (documentId, announcement) = announcementPairs[position]

        holder.titleTextView.text = announcement.title
        holder.dateTextView.text = announcement.getDateOnly()

        val imageUrl = "https://firebasestorage.googleapis.com/v0/b/fit99-9dacb.appspot.com/o/Announcement%2F${documentId}.jpg?alt=media"
        Picasso.get().load(imageUrl).into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClicked(documentId) // Trigger the click listener
        }
    }

    override fun getItemCount(): Int = announcementPairs.size
}
