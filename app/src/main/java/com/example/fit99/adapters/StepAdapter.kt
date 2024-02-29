import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R


class StepAdapter(private val stepList : ArrayList<String>) :
    RecyclerView.Adapter<StepAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.step_circle_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return stepList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = stepList[position]

        holder.num.text = (position+1).toString()
        holder.desc.text = currentItem
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val num : TextView = itemView.findViewById(R.id.step_circle)
        val desc : TextView = itemView.findViewById(R.id.muscle_name)
    }
}
