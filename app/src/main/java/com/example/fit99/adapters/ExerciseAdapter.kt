import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso


class ExerciseAdapter(private val exerciseList : ArrayList<Exercise>, private val navigator : NavController) :
    RecyclerView.Adapter<ExerciseAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercise_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exerciseList[position]
        holder.exerciseName.text = currentItem.name
        var imageUrl = currentItem.visualURL.replace(".mp4",".png")
        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Log.e(TAG,"ji"+exerciseList.size)
        Picasso.get().load(imageUrl).into(holder.thumbnail)

        holder.cont.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("exerciseName", currentItem.name)
            navigator.navigate(R.id.action_exerciseListFragment_to_exerciseDetailsFragment, bundle)
        }
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cont : ConstraintLayout = itemView.findViewById(R.id.equipmentLayout)
        val thumbnail : ImageView = itemView.findViewById(R.id.thumbnail)
        val exerciseName : TextView = itemView.findViewById(R.id.exerciseName)
    }
}
