import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AnnouncementFragment : Fragment() {

    private lateinit var announcementRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_announcement, container, false)
        announcementRecyclerView = view.findViewById(R.id.announcements)
        fetchAnnouncements()
        return view
    }

    private fun fetchAnnouncements() {
        db.collection("Announcement")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val announcementPairs = querySnapshot.documents.mapNotNull { document ->
                    val announcement = document.toObject(Announcement::class.java)
                    announcement?.let { Pair(document.id, it) }
                }
                setupRecyclerView(announcementPairs)
            }
            .addOnFailureListener { e ->
                Log.e("AnnouncementFragment", "Error fetching announcements", e)
            }
    }

    private fun setupRecyclerView(announcementPairs: List<Pair<String, Announcement>>) {
        val adapter = AnnouncementListAdapter(announcementPairs) { documentId ->
            val bundle = Bundle()
            bundle.putString("announcement", documentId)
            findNavController().navigate(R.id.action_announcementFragment_to_announcementNoticeFragment, bundle)
        }

        announcementRecyclerView.layoutManager = LinearLayoutManager(context)
        announcementRecyclerView.adapter = adapter
    }
}
