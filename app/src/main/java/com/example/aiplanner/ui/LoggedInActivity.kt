package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.aiplanner.R
import com.example.aiplanner.MainActivity  // Importujemy MainActivity

class LoggedInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)  // Dopasuj layout, jeśli ma inną nazwę

        // Inicjalizacja FirebaseAuth
        auth = FirebaseAuth.getInstance()

        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            // Wylogowanie
            auth.signOut()

            // Po wylogowaniu przenosimy do MainActivity
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        // Zamiast logowania przenosimy użytkownika do MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Kończymy bieżącą aktywność
    }
}
