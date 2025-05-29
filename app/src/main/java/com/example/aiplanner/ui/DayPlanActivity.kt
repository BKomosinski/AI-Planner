package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Calendar
import com.example.aiplanner.database.DatabaseHelper

class DayPlanActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonplus: Button
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dayplan)

        //hamburger
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)

        // przypisanie daty
        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getTodayDate()

        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        dateTextView.text = "Plan na $selectedDate"

        val holidayName = intent.getStringExtra("HOLIDAY_NAME")
        val holidayTextView = findViewById<TextView>(R.id.holidayTextView)
        if (!holidayName.isNullOrEmpty()) {
            holidayTextView.text = "Święto: $holidayName"
            holidayTextView.visibility = View.VISIBLE
        } else {
            holidayTextView.visibility = View.GONE
        }

        loadTasks()

        //guzik - POPRAWKA: przekazuj selectedDate
        buttonplus = findViewById(R.id.btnplus)
        buttonplus.setOnClickListener{
            val intent = Intent(this, AddingTaskActivity::class.java)
            intent.putExtra("SELECTED_DATE", selectedDate) // <- WAŻNE: przekaż datę!
            startActivity(intent)
        }
    }

    // Funkcja do ładowania zadań
    private fun loadTasks() {
        val db = DatabaseHelper(this)
        val tasks = db.getTasksForDate(selectedDate)

        val taskTextView = findViewById<TextView>(R.id.planDetailsTextView)
        taskTextView.text = if (tasks.isEmpty()) {
            "Brak planu na ten dzień"
        } else {
            "Zadania na $selectedDate:\n" + tasks.joinToString("\n") { "• $it" }
        }
    }

    // Odśwież zadania po powrocie z AddingTaskActivity
    override fun onResume() {
        super.onResume()
        loadTasks() // Odśwież listę zadań
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Miesiące zaczynają się od 0
        val year = calendar.get(Calendar.YEAR)
        return "$day/$month/$year"
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Przykład – otwórz stronę domową
                startActivity(Intent(this, DayPlanActivity::class.java))
                finish()
            }
            R.id.nav_calendar -> {
                // Otwórz stronę z kalendarzem
                startActivity(Intent(this, CalendarActivity::class.java))
            }
            R.id.nav_data -> {
                // Otwórz stronę z kalendarzem
                startActivity(Intent(this, LoggedInActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Zamknij menu po kliknięciu
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}