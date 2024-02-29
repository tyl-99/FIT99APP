import android.content.Context
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.ExercisePreview
import com.example.fit99.classes.WorkoutExercise
import com.squareup.picasso.Picasso

class WorkoutDetailMainAdapter(private val context: Context, private val myList: List<ExercisePreview>) :
    RecyclerView.Adapter<WorkoutDetailMainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.workout_details, parent, false)
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
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val textView: TextView = itemView.findViewById(R.id.name)
        private val reps: TextView = itemView.findViewById(R.id.reps)

        fun bind(preview : ExercisePreview) {
            var imageUrl = preview.imageResId.replace(".mp4",".png")
            imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
            Picasso.get().load(imageUrl).into(imageView)
            textView.text = preview.name
            if(preview.mode == "Reps"){
                reps.text = "${preview.reps} Reps"
            }
            else{
                reps.text = "${preview.reps} Seconds"
            }
        }
    }
}
