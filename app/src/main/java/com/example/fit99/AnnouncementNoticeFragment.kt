package com.example.fit99

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class AnnouncementNoticeFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var imageView: ImageView
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_announcement_notice, container, false)
        initializeViews(view)
        loadAnnouncement()
        return view
    }

    private fun initializeViews(view: View) {
        titleTextView = view.findViewById(R.id.title)
        dateTextView = view.findViewById(R.id.date)
        contentTextView = view.findViewById(R.id.content)
        imageView = view.findViewById(R.id.imageView7)
    }

    private fun loadAnnouncement() {
        val announcementId = requireArguments().getString("announcement", "Jini")
        announcementId?.let {
            db.collection("Announcement").document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val title = document.getString("title")
                        val date = document.getString("date")
                        val content = document.getString("content")
                        val imageUrl = "https://firebasestorage.googleapis.com/v0/b/fit99-9dacb.appspot.com/o/Announcement%2F${it}.jpg?alt=media"
                        Picasso.get().load(imageUrl).into(imageView)
                        Log.d("AnnouncementNoticeFragment", content.toString())
                        titleTextView.text = title
                        dateTextView.text = date
                        contentTextView.text = content
                        // Load the image into imageView
                    } else {
                        Log.d("AnnouncementNoticeFragment", "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("AnnouncementNoticeFragment", "Error fetching document", e)
                }
        }
    }
}
