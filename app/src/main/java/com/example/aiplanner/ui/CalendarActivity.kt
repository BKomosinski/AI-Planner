package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.aiplanner.R
import com.google.android.material.navigation.NavigationView

class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        //hamburger
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)

        //kalendarz
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        // 🔥 Obsługa kliknięcia na datę → przejście na stronę szczegółów
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Wybrano: $selectedDate", Toast.LENGTH_SHORT).show()

            // Przejście do DayPlanActivity z datą
            val intent = Intent(this, DayPlanActivity::class.java)
            intent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(intent)
        }

        // Włącz ikonę "Wstecz"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kalendarz"
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
