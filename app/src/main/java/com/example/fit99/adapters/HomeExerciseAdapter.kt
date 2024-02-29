import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Preview
import com.squareup.picasso.Picasso

class HomeExerciseAdapter(private val context: Context, private val myList: List<Preview>,private val navigator :NavController) :
    RecyclerView.Adapter<HomeExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.content_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = myList[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cont : ConstraintLayout = itemView.findViewById(R.id.cont)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val textView: TextView = itemView.findViewById(R.id.name)

        fun bind(preview : Preview) {
            Picasso.get().load(preview.imageResId).into(imageView)
            textView.text = preview.name

            cont.setOnClickListener {
                val bundle  =Bundle()
                bundle.putString("exerciseName",preview.name)
                navigator.navigate(R.id.action_homeFragment_to_exerciseDetailsFragment,bundle)
            }




        }
    }
}
