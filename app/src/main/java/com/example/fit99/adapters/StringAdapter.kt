import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R

class StringAdapter(private val stringList: ArrayList<String>, private val navigator: NavController, private val mode: String) :
    RecyclerView.Adapter<StringAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.string_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = stringList[position]
        holder.stringTextView.text = currentItem

        /*holder.cont.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(mode, currentItem)
            navigator.navigate(R.id.action_destination1_to_destination2, bundle) // Update navigation IDs accordingly
        }*/
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cont: ConstraintLayout = itemView.findViewById(R.id.cont)
        val stringTextView: TextView = itemView.findViewById(R.id.string)
        // Additional UI elements can be referenced here if needed
    }
}
