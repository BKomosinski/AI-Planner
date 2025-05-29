package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Calendar
import com.example.aiplanner.database.DatabaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class DayPlanActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonplus: Button
    private lateinit var selectedDate: String
    private lateinit var buttonAddBio: Button
    private lateinit var biometricContainer: LinearLayout
    private lateinit var hrvTextView: TextView
    private lateinit var restingHrTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var bedtimeTextView: TextView
    private lateinit var wakeupTextView: TextView


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
        biometricContainer = findViewById(R.id.biometricContainer)
        hrvTextView        = findViewById(R.id.hrvTextView)
        restingHrTextView  = findViewById(R.id.restingHrTextView)
        weightTextView     = findViewById(R.id.weightTextView)
        bedtimeTextView    = findViewById(R.id.bedtimeTextView)
        wakeupTextView     = findViewById(R.id.wakeupTextView)
        buttonAddBio       = findViewById(R.id.btnAddBiometric)

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
        buttonAddBio = findViewById(R.id.btnAddBiometric)
        buttonAddBio.setOnClickListener {
            Intent(this, LoggedInActivity::class.java).also { intent ->
                intent.putExtra("SELECTED_DATE", selectedDate)
                startActivity(intent)
            }
        }

    }

    // Funkcja do ładowania zadań
    private fun loadTasks() {
        val db = DatabaseHelper(this)
        // 1) ładujemy zadania
        db.getTasksForDate(selectedDate) { tasks ->
            runOnUiThread {
                updateUI(tasks)
                loadBiometricData()
            }
        }
    }

    private fun loadBiometricData() {
        val db = DatabaseHelper(this)
        var auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val userName = user?.email ?: "Unknown User"

        // potrzebujemy metodę getDataForDate(userName, date)
        val data = db.getDataForDate(userName, selectedDate)
        if (data.isNotEmpty()) {
            val bio = data.first()
            biometricContainer.visibility = View.VISIBLE
            buttonAddBio.visibility = View.GONE

            hrvTextView.text       = "HRV: ${bio.hrv}"
            restingHrTextView.text = "Tętno spocz.: ${bio.restingHeartRate}"
            weightTextView.text    = "Waga: ${bio.weight}"
            bedtimeTextView.text   = "Pora snu: ${bio.bedtime}"
            wakeupTextView.text    = "Pobudka: ${bio.wakeupTime}"
        } else {
            biometricContainer.visibility = View.GONE
            buttonAddBio.visibility = View.VISIBLE
        }
    }

    private fun updateUI(tasks: List<String>) {
        val taskTextView = findViewById<TextView>(R.id.planDetailsTextView)
        taskTextView.text = if (tasks.isEmpty()) {
            "Brak planu na ten dzień"
        } else {
            // wypisz każdy element w nowej linii z kropką
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