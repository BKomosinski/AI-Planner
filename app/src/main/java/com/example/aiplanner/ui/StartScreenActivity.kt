package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen) // Używamy XML, który stworzyliśmy

        // Pobieramy przyciski z interfejsu
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        // Przycisk "Zaloguj się"
        loginButton.setOnClickListener {
            // Uruchamiamy ekran logowania
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Przycisk "Zarejestruj się"
        registerButton.setOnClickListener {
            // Uruchamiamy ekran rejestracji
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}
