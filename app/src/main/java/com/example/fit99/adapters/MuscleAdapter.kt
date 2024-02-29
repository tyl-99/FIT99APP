import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R


class MuscleAdapter(private val muscleList : ArrayList<String>) :
    RecyclerView.Adapter<MuscleAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.muscle_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return muscleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = muscleList[position]
        Log.d(ContentValues.TAG,muscleList.toString())
        holder.name.text = currentItem
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.muscle_name)
    }
}
