import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Equipment
import com.example.fit99.classes.Exercise
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseMainFragment : Fragment() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_exercise_main, container, false)

        val navigator  = findNavController()
        var categories = listOf<ImageView>(view.findViewById(R.id.all),view.findViewById(R.id.leg),view.findViewById(R.id.cardio))

        for(category in categories){
            category.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("selectedCategory", it.tag.toString())
                if(bundle.getString("selectedCategory")!= null){
                    Log.d(ContentValues.TAG, bundle.getString("selectedCategory")!!)
                }

                navigator.navigate(R.id.action_exerciseMainFragment_to_exerciseListFragment, bundle)



            }
        }
        return view
    }








}

