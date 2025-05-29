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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback

import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.util.Calendar
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.DecelerateInterpolator


class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        //hamburger
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)

        //kalendarz
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

// Początkowy stan: poniżej ekranu i przezroczysty
        calendarView.translationY = 150f
        calendarView.alpha = 0f

// Animacja przesunięcia w górę
        val slideUp = ObjectAnimator.ofFloat(calendarView, "translationY", 500f, 0f)
        slideUp.duration = 2000
        slideUp.interpolator = DecelerateInterpolator()

// fade in
        val fadeIn = ObjectAnimator.ofFloat(calendarView, "alpha", 0f, 1f)
        fadeIn.duration = 2000
        fadeIn.interpolator = DecelerateInterpolator()

// animacja
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(slideUp, fadeIn)
        animatorSet.startDelay = 600
        animatorSet.start()

        fetchPolishHolidays(2025) { holidayMap ->

            calendarView.setOnDateChangeListener { _, year, month, day ->
                val formattedDate = "$year-${"%02d".format(month + 1)}-${"%02d".format(day)}"
                val displayDate = "$day/${month + 1}/$year"
                val holidayName = holidayMap[formattedDate]

                if (holidayName != null) {
                    Toast.makeText(this, "To święto: $holidayName", Toast.LENGTH_LONG).show()
                }

                val intent = Intent(this, DayPlanActivity::class.java)
                intent.putExtra("SELECTED_DATE", displayDate)
                intent.putExtra("HOLIDAY_NAME", holidayName)
                startActivity(intent)
            }
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

                startActivity(Intent(this, DayPlanActivity::class.java))
                finish()
            }
            R.id.nav_calendar -> {

                startActivity(Intent(this, CalendarActivity::class.java))
            }
            R.id.nav_data -> {

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
    fun fetchPolishHolidays(year: Int, onResult: (Map<String, String>) -> Unit) {
        val url = "https://date.nager.at/api/v3/PublicHolidays/$year/PL"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    val holidays = mutableMapOf<String, String>()
                    val jsonArray = JSONArray(body)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val date = obj.getString("date") // yyyy-MM-dd
                        val name = obj.getString("localName")
                        holidays[date] = name
                    }
                    runOnUiThread {
                        onResult(holidays)
                    }
                }
            }
        })
    }


}
