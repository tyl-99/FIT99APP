import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

class EquipmentListFragment : Fragment() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var equipmentList : ArrayList<Equipment>
    private lateinit var navigator : NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_equipment_list, container, false)
        super.onViewCreated(view, savedInstanceState)
        navigator = findNavController()
        equipmentList =  ArrayList<Equipment>()
        newRecyclerView = view.findViewById(R.id.equipment_list)
        newRecyclerView.layoutManager = LinearLayoutManager(activity);
        newRecyclerView.setHasFixedSize(true)

        loadData(navigator, "")
        val searchEditText = view.findViewById<EditText>(R.id.searchbar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                loadData(navigator,s.toString())
                newRecyclerView.adapter = EquipmentAdapter(equipmentList,navigator)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }


    private fun loadData(navigator : NavController, searchQuery:String){
        db.collection("Equipments")
            .get()
            .addOnSuccessListener { documents ->
                equipmentList.clear()
                for (document in documents) {
                    val equipment = document.toObject(Equipment::class.java)
                    equipmentList.add(equipment)
                }
                if(searchQuery != ""){
                    equipmentList = filterEquipmentList(searchQuery)
                }
                newRecyclerView.adapter = EquipmentAdapter(equipmentList,navigator)
            }
            .addOnFailureListener { exception ->
                // Handle the error here
                Log.e("FirestoreError", "Error getting documents: ", exception)
            }
    }

    private fun filterEquipmentList(searchQuery: String): ArrayList<Equipment> {
        if (searchQuery.isNotBlank()) {
            return equipmentList.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true)
            } as ArrayList<Equipment>
        } else {
            return ArrayList() // Return an empty list when search query is blank
        }
    }






}

