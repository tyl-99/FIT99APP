import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.WorkoutExercise
import com.squareup.picasso.Picasso
import org.checkerframework.checker.units.qual.s


class SelectExerciseAdapter(
    private var exerciseList: ArrayList<Exercise>,
    private val navigator: NavController,
    private val exerciseClickListener: ExerciseClickListener
) :
    RecyclerView.Adapter<SelectExerciseAdapter.MyViewHolder>() {

    private var selectedExercise = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercise_list_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exerciseList[position]
        holder.name.text = currentItem.name
        var imageUrl = currentItem.visualURL.replace(".mp4",".png")
        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Picasso.get().load(imageUrl).into(holder.thumbnail)

        // Set the initial state based on whether the item is selected
        if (selectedExercise.contains(currentItem.name)) {
            holder.cont.setBackgroundResource(R.drawable.row_selected)
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.itemView.tag = "1"
        } else {
            holder.cont.setBackgroundResource(R.drawable.row_notselected)
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.itemView.tag = "0"
        }

        holder.cont.setOnClickListener {
            // Toggle the state and update the selectedExercise list
            if (holder.itemView.tag == "1") {
                holder.cont.setBackgroundResource(R.drawable.row_notselected)
                holder.itemView.tag = "0"
                selectedExercise.remove(currentItem.name)
            } else {
                holder.cont.setBackgroundResource(R.drawable.row_selected)
                holder.itemView.tag = "1"
                selectedExercise.add(currentItem.name)
            }
            exerciseClickListener.onExerciseClicked(selectedExercise)
        }
    }

    fun getSelectedExercise() : ArrayList<String>{
        return selectedExercise
    }

    fun updateExerciseList(newExerciseList:ArrayList<Exercise>){
        exerciseList = newExerciseList
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cont : ConstraintLayout = itemView.findViewById(R.id.equipmentLayout)
        val name : TextView = itemView.findViewById(R.id.exerciseName)
        val thumbnail : ImageView = itemView.findViewById(R.id.thumbnail)

    }

    interface ExerciseClickListener {
        fun onExerciseClicked(selectedExercise: ArrayList<String>)
    }

}
