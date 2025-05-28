package com.example.aiplanner.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R
import android.app.TimePickerDialog
import java.util.Calendar
import com.example.aiplanner.database.DatabaseHelper
import android.widget.Button
import android.widget.Toast



data class ActivityType(
    val name: String,
    val color: Int
)
class AddingTaskActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var activityTypeSpinner: Spinner
    private lateinit var startTimeInput: EditText
    private lateinit var endTimeInput: EditText
    private lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_task)

        taskInput = findViewById(R.id.taskInput)
        activityTypeSpinner = findViewById(R.id.activityTypeSpinner)
        startTimeInput = findViewById(R.id.startTimeInput)
        endTimeInput = findViewById(R.id.endTimeInput)
        saveButton = findViewById(R.id.saveButton) // ← dodaj w XML

        val date = intent.getStringExtra("SELECTED_DATE") ?: return

        // spinner setup
        val activityTypes = listOf(
            ActivityType("Studia", getColor(R.color.colorStudy)),
            ActivityType("Hobby", getColor(R.color.colorHobby)),
            ActivityType("Trening", getColor(R.color.colorTraining))
        )
        val adapter = ActivityTypeAdapter(activityTypes)
        activityTypeSpinner.adapter = adapter

        // czas rozpoczęcia i zakończenia
        startTimeInput.setOnClickListener {
            showTimePicker { hour, minute ->
                startTimeInput.setText("%02d:%02d".format(hour, minute))
            }
        }
        endTimeInput.setOnClickListener {
            showTimePicker { hour, minute ->
                endTimeInput.setText("%02d:%02d".format(hour, minute))
            }
        }

        // obsługa zapisu zadania
        saveButton.setOnClickListener {
            val taskTitle = taskInput.text.toString()
            if (taskTitle.isNotBlank()) {
                val db = DatabaseHelper(this)
                db.addTask(taskTitle, date)
                finish()
            } else {
                Toast.makeText(this, "Wpisz nazwę zadania", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Funkcja otwierająca TimePickerDialog
    private fun showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true // true → 24h format
        )
        timePickerDialog.show()

    }



    inner class ActivityTypeAdapter(
        private val activityTypes: List<ActivityType>
    ) : BaseAdapter() {

        override fun getCount(): Int = activityTypes.size

        override fun getItem(position: Int): Any = activityTypes[position]

        override fun getItemId(position: Int): Long = position.toLong()

        private fun createColoredTextView(name: String, color: Int): TextView {
            val textView = TextView(this@AddingTaskActivity)
            textView.text = name
            textView.setPadding(32, 24, 32, 24)
            textView.setTextColor(0xFFFFFFFF.toInt())
            textView.textSize = 16f
            textView.background = getDrawable(R.drawable.spinner_background)
            textView.background.setTint(color)  // <- tu zmieniamy kolor dynamicznie!
            return textView
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val activityType = activityTypes[position]
            return createColoredTextView(activityType.name, activityType.color)
        }
//wyzej wybierana jest opcja, nizej rozwija sie lista
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val activityType = activityTypes[position]
            return createColoredTextView(activityType.name, activityType.color)
        }

    }
}
