import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Workout


class WorkoutAdapter(private val workoutList : ArrayList<Workout>, private val navigator: NavController) :
    RecyclerView.Adapter<WorkoutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = workoutList[position]

        holder.name.text = currentItem.name
        holder.count.text = "${currentItem.exercises.size.toString()} Exercises"

        holder.cont.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("workout_name", currentItem.name)
            navigator.navigate(R.id.action_workoutFragment2_to_workoutDetailsFragment,bundle)
        }
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cont : ConstraintLayout = itemView.findViewById(R.id.cont)
        val name : TextView = itemView.findViewById(R.id.name)
        val count : TextView = itemView.findViewById(R.id.count)
    }
}
