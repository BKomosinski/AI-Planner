package com.example.aiplanner.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R
import com.example.aiplanner.database.DatabaseHelper
import com.example.aiplanner.MainActivity  // Importujemy MainActivity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import java.lang.Float


class LoggedInActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var hrvEditText: EditText
    private lateinit var restingHeartRateEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var bedtimeEditText: EditText
    private lateinit var wakeupTimeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var viewDataButton: Button
    private lateinit var logoutButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        auth = FirebaseAuth.getInstance()

        // Inicjalizacja widoków
        hrvEditText = findViewById(R.id.etHRV)
        restingHeartRateEditText = findViewById(R.id.etRestingHeartRate)
        weightEditText = findViewById(R.id.etWeight)
        bedtimeEditText = findViewById(R.id.etBedtime)
        wakeupTimeEditText = findViewById(R.id.etWakeupTime)

        // Inicjalizacja przycisków
        viewDataButton = findViewById(R.id.btnViewData)
        logoutButton = findViewById(R.id.btnLogout)
        saveButton = findViewById(R.id.btnSaveData)

        // Inicjalizacja bazy danych
        dbHelper = DatabaseHelper(this)

        // Obsługa kliknięcia przycisku zapisywania danych
        saveButton.setOnClickListener {
            val hrv = hrvEditText.text.toString()
            val restingHeartRate = restingHeartRateEditText.text.toString()
            val weight =  weightEditText.text.toString()
            val bedtime = bedtimeEditText.text.toString()
            val wakeupTime = wakeupTimeEditText.text.toString()

            val user = auth.currentUser
            val userName = user?.email ?: "Unknown User"


// Przekazywanie do bazy
            dbHelper.saveUserData(hrv, restingHeartRate, weight, bedtime, wakeupTime, userName)

            hrvEditText.setText("")
            restingHeartRateEditText.setText("")
            weightEditText.setText("")
            bedtimeEditText.setText("")
            wakeupTimeEditText.setText("")
        }

        // Obsługa przycisku "Pokaż dane"
        viewDataButton.setOnClickListener {
            // Logika do wyświetlania danych
            showData()
        }

        // Obsługa przycisku "Wyloguj"
        logoutButton.setOnClickListener {
            // Logika do wylogowywania
            logout()
        }
    }
    private fun showData() {
        // Załaduj dane z bazy danych
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        // Wylogowanie użytkownika z Firebase
        auth.signOut()

        // Przechodzimy do MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear previous activities from stack
        startActivity(intent)
        finish()  // Kończymy bieżącą aktywność, aby użytkownik nie mógł wrócić za pomocą "Back"
    }
}
