package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R

import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Utwórz odpowiedni układ dla ekranu logowania

        // Inicjalizacja Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicjalizacja widoków
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Ustawienie kliknięcia przycisku logowania
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        // Próba zalogowania użytkownika
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Jeśli logowanie się powiedzie, przenosimy do LoggedInActivity
                    navigateToLoggedInActivity()
                } else {
                    // Jeśli logowanie się nie powiedzie, wyświetlamy komunikat o błędzie
                    Toast.makeText(this, "Błąd logowania: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLoggedInActivity() {
        val intent = Intent(this, DayPlanActivity::class.java)
        startActivity(intent)
        finish()  // Kończymy bieżącą aktywność (ekran logowania)
    }
}
