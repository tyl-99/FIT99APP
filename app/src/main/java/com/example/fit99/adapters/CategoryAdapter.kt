import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.currentComposer
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R


class CategoryAdapter(private val catList : ArrayList<String>,private val setList : ArrayList<String>) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    private var selectedCat = setList

    private var categoryClickListener: CategoryClickListener? = null

    fun setCategoryClickListener(listener: CategoryClickListener) {
        categoryClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filter_category_list, parent, false)
        return MyViewHolder(itemView)
    }

    fun setSelected(catList : ArrayList<String>){
        this.selectedCat = catList

    }

    override fun getItemCount(): Int {
        return catList.size
    }
    fun getSelectedCat():ArrayList<String> {
        return selectedCat
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = catList[position]


        holder.cat.text = currentItem

        if(currentItem in setList){
            holder.cat.setBackgroundResource(R.drawable.cat_filter_selected)
            holder.cat.setTextColor(ContextCompat.getColor(holder.cat.context, R.color.white))
            holder.cat.tag = "1"
        }

        holder.cat.setOnClickListener {
            if (it.tag == "1") {
                it.setBackgroundResource(R.drawable.cat_filter_shape)
                holder.cat.setTextColor(ContextCompat.getColor(it.context, android.R.color.white))
                it.tag = "0"
            }
            else if(it.tag == "0"){

                it.setBackgroundResource(R.drawable.cat_filter_selected)
                holder.cat.setTextColor(ContextCompat.getColor(it.context, R.color.white))
                it.tag = "1"
            }

            if (selectedCat.contains(currentItem)) {
                selectedCat.remove(currentItem)
                // Reset the view state here (background and text color)
            } else {
                selectedCat.add(currentItem)
                // Set the view state for selected state here
            }
            categoryClickListener?.onCategoryClicked(currentItem)
        }





    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cat : TextView = itemView.findViewById(R.id.cat)

    }

    interface CategoryClickListener {
        fun onCategoryClicked(category: String)
    }
}
