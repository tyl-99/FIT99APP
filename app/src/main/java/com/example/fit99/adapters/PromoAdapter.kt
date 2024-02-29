import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R

class PromoAdapter(private val promoList: List<Promo>, private val onPromoClick: (String) -> Unit) : RecyclerView.Adapter<PromoAdapter.PromoViewHolder>() {

    class PromoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPromo: TextView = view.findViewById(R.id.name)
        val cont: ConstraintLayout = view.findViewById(R.id.cont)
        // Other views can be added here if your promo item has more data fields
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_promo, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val promo = promoList[position]
        holder.textViewPromo.text = promo.promoCode
        holder.cont.setOnClickListener {
            onPromoClick(promo.promoCode)
        }
    }

    override fun getItemCount() = promoList.size
}
