import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fit99.R
import com.example.fit99.classes.Exercise
import com.example.fit99.classes.WorkoutExercise
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import java.util.Collections


class EditWorkoutExerciseDetailAdapter(private var mylist : ArrayList<WorkoutExercise>, var exerciseList : ArrayList<Exercise>, private val context : Context) :
    RecyclerView.Adapter<EditWorkoutExerciseDetailAdapter.MyViewHolder>(),
    EditItemTouchHelperAdapter {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.edit_workout_exercise_overview_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exerciseList[position]

        var imageUrl = currentItem.visualURL.replace(".mp4",".png")
        holder.name.text = currentItem.name

        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Log.e(TAG,imageUrl)

        Picasso.get().load(imageUrl).into(holder.thumbnail)

        var exerciseR = mylist.find { it.exerciseName == currentItem.name }

        var index = mylist.indexOfFirst { it.exerciseName == currentItem.name }

        if(exerciseR != null){
            if(exerciseR.mode == "Reps"){
                holder.summary.text = "${exerciseR.reps } Reps @ ${exerciseR.sets} Sets"
            }
            else{
                holder.summary.text = "${exerciseR.duration } S @ ${exerciseR.sets} Sets"
            }
        }

        holder.btn.setOnClickListener {
            if (exerciseR != null) {
                showDialog(exerciseR!!,currentItem, object: EditDialogCallback {
                    override fun onDialogResult(exercise: WorkoutExercise) {

                        var string = "Kan Kere"
                        Log.d("Jinitaimei",exercise.mode)
                        if(exercise.mode == "Reps"){
                            string = "${exercise.reps} Reps @ ${exercise.sets} Sets"
                        }
                        else if(exercise.mode == "Duration"){
                            string = "${exercise.duration} S @ ${exercise.sets} Sets"
                        }


                        holder.summary.text = string
                        holder.summary.visibility = View.VISIBLE
                        mylist[index] = exercise


                    }
                })
            }
        }
    }


    fun updateExerciseList(newExerciseList:ArrayList<Exercise>){
        exerciseList = newExerciseList
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cont : ConstraintLayout = itemView.findViewById(R.id.equipmentLayout)
        val name : TextView = itemView.findViewById(R.id.exerciseName)
        val thumbnail : ImageView = itemView.findViewById(R.id.thumbnail)
        val btn : TextView = itemView.findViewById(R.id.setBtn)
        val summary : TextView = itemView.findViewById(R.id.summary)

    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {

        // Update the dataset to reflect the item move
        val movedItem = exerciseList.removeAt(fromPosition)
        exerciseList.add(toPosition, movedItem)

        // Notify the adapter that an item has moved
        notifyItemMoved(fromPosition, toPosition)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(exerciseList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun showDialog(exercise:WorkoutExercise, myex: Exercise, callback: EditDialogCallback,){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.fit99.R.layout.exercise_dialog)
        val buttonCancel = dialog.findViewById<Button>(com.example.fit99.R.id.forgot)
        val buttonOk = dialog.findViewById<Button>(com.example.fit99.R.id.login)
        val reps = dialog.findViewById<TextView>(com.example.fit99.R.id.reps)
        val duration = dialog.findViewById<TextView>(com.example.fit99.R.id.duration)
        val reps_label = dialog.findViewById<TextView>(com.example.fit99.R.id.reps_label)
        val reps_data = dialog.findViewById<TextView>(com.example.fit99.R.id.reps_data)
        val sets_data = dialog.findViewById<TextView>(com.example.fit99.R.id.sets_data)
        val interval_data = dialog.findViewById<TextView>(com.example.fit99.R.id.interval_data)
        val recommended = dialog.findViewById<TextView>(com.example.fit99.R.id.recommended)
        val recommended_sets = dialog.findViewById<TextView>(com.example.fit99.R.id.recommended_sets)
        val recommended_interval = dialog.findViewById<TextView>(com.example.fit99.R.id.recommended_interval)

        recommended.text = ""
        recommended_sets.text = ""
        recommended_interval.text = ""

        if(exercise.mode == "Reps" ){
            reps_data.text = exercise.reps.toString()
        }
        else{
            reps_data.text = exercise.duration.toString()
        }

        sets_data.text = exercise.sets.toString()
        interval_data.text = exercise.interval.toString()

        val repsfaq = dialog.findViewById<ShapeableImageView>(com.example.fit99.R.id.repsfaq)
        val setsfaq = dialog.findViewById<ShapeableImageView>(com.example.fit99.R.id.setsfaq)
        val intervalfaq = dialog.findViewById<ShapeableImageView>(com.example.fit99.R.id.intervalfaq)

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        repsfaq.setOnClickListener {
            showInfoDialog("Reps/Duration", "Reps (Repetitions) refer to the number of times you perform a specific exercise. Duration refers to the amount of time you spend performing a specific exercise.")
        }
        setsfaq.setOnClickListener {
            showInfoDialog("Sets", "Sets refer to the number of cycles of reps that you complete.")
        }
        intervalfaq.setOnClickListener {
            showInfoDialog("Rest Interval", "Rest Interval is the amount of time you take a break between sets.")
        }
        if(exercise.mode == "" || exercise.mode == "Reps"){
            reps.setBackgroundResource(R.drawable.border_radius_exercise_mode_black)
            reps.setTextColor(ContextCompat.getColor(context, android.R.color.tab_indicator_text))
            exercise.mode = "Reps"
        }
        else{
            duration.setBackgroundResource(R.drawable.border_radius_exercise_mode_black)
            duration.setTextColor(ContextCompat.getColor(context, android.R.color.tab_indicator_text))
        }

        reps.setOnClickListener {
            it.setBackgroundResource(R.drawable.border_radius_exercise_mode_black)
            reps.setTextColor(ContextCompat.getColor(it.context, android.R.color.tab_indicator_text))
            exercise.mode = "Reps"
            reps_label.text = "Reps"
            duration.setBackgroundResource(R.drawable.border_radius_exercise_mode)
            duration.setTextColor(ContextCompat.getColor(it.context, android.R.color.black))

        }
        duration.setOnClickListener {
            it.setBackgroundResource(R.drawable.border_radius_exercise_mode_black)
            duration.setTextColor(ContextCompat.getColor(it.context, android.R.color.tab_indicator_text))
            exercise.mode = "Duration"
            reps_label.text = "Duration"
            reps.setBackgroundResource(R.drawable.border_radius_exercise_mode)
            reps.setTextColor(ContextCompat.getColor(it.context, android.R.color.black))

        }
        buttonOk.setOnClickListener {
            if(exercise.mode == "Reps"){
                exercise.reps = reps_data.text.toString().toIntOrNull()!!

            }else if(exercise.mode == "Duration"){
                exercise.duration = reps_data.text.toString().toDouble()
            }
            exercise.sets = sets_data.text.toString().toIntOrNull()!!
            exercise.interval = interval_data.text.toString().toIntOrNull()!!




            dialog.dismiss() // Close the dialog
            callback.onDialogResult(exercise)
        }
        dialog.show() // Show the dialog*/
    }

    fun getWorkoutExerciseList() :ArrayList<WorkoutExercise>{
        return mylist
    }

    private fun showInfoDialog(title: String, message: String) {
        val infoDialog = Dialog(context)
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        infoDialog.setContentView(R.layout.simple_info_dialog) // assuming you have a layout file 'simple_info_dialog.xml'

        val titleView = infoDialog.findViewById<TextView>(R.id.infoDialogTitle)
        val messageView = infoDialog.findViewById<TextView>(R.id.infoDialogMessage)
        val buttonOk = infoDialog.findViewById<Button>(R.id.infoDialogButtonOk)

        titleView.text = title
        messageView.text = message
        buttonOk.setOnClickListener { infoDialog.dismiss() }

        infoDialog.show()
    }


}

interface EditItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
}

interface EditDialogCallback {
    fun onDialogResult(exercise: WorkoutExercise)
}
