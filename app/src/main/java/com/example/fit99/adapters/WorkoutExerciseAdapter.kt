import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.text.TextUtils.replace
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
import com.squareup.picasso.Picasso
import java.util.Collections


class WorkoutExerciseAdapter(private var exerciseList : ArrayList<WorkoutExercise>,private var exercises : ArrayList<Exercise>, private val context : Context) :
    RecyclerView.Adapter<WorkoutExerciseAdapter.MyViewHolder>(),
    ItemTouchHelperAdapter {

    private var workoutExercisesList: ArrayList<WorkoutExercise> = ArrayList()




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_exercise_overview_row, parent, false)
        Log.d("kere",itemCount.toString())
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exerciseList[position]
        val currentExercise = exercises[position]
        holder.name.text = currentItem.exerciseName
        var imageUrl = currentExercise.visualURL.replace(".mp4",".png")

        imageUrl += "?alt=media&token=ea2f07c3-17ad-4d1a-907b-6a7c008608cc"
        Log.e(TAG,imageUrl)

        Picasso.get().load(imageUrl).into(holder.thumbnail)

        if(currentItem.mode !=null){
            var string = "Ji9ni"
            if(currentItem.mode == "Reps"){
                string = "${currentItem.reps} Reps @ ${currentItem.sets} Sets"
            }
            else if(currentItem.mode == "Duration"){
                string = "${currentItem.duration} S @ ${currentItem.sets} Sets"
            }
            holder.summary.text = string
            holder.summary.visibility = View.VISIBLE
        }

        var exerciseR = exerciseList.find { it.exerciseName == currentItem.exerciseName }

        var index = exerciseList.indexOfFirst { it.exerciseName == currentItem.exerciseName}


        holder.btn.setOnClickListener {
            Log.d("kere","jini")
            if (exerciseR != null) {
                showDialog(exerciseR!!, object: DialogCallback {
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
                        exerciseList[index] = exercise


                    }
                })
            }
        }
    }


    fun updateExerciseList(newExerciseList:ArrayList<WorkoutExercise>){
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
        Collections.swap(exerciseList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(exerciseList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun showDialog(exercise:WorkoutExercise, callback: DialogCallback){
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
        return exerciseList
    }
    interface ItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }


}

