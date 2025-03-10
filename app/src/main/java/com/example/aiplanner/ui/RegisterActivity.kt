package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.aiplanner.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)  // Dopasuj layout, jeśli ma inną nazwę

        // Inicjalizacja FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Powiązanie widoków
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                // Sprawdzamy, czy hasła są takie same
                if (password == confirmPassword) {
                    // Rejestracja użytkownika
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Jeśli rejestracja zakończona sukcesem, przejdź do ekranu zalogowanego użytkownika
                                navigateToLoggedInActivity()
                            } else {
                                // Jeśli rejestracja się nie udała, wyświetl komunikat o błędzie
                                Toast.makeText(
                                    baseContext, "Rejestracja nie powiodła się. Spróbuj ponownie.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    // Jeśli hasła się nie zgadzają
                    Toast.makeText(baseContext, "Hasła muszą się zgadzać", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funkcja do przekierowania do LoggedInActivity
    private fun navigateToLoggedInActivity() {
        val intent = Intent(this, LoggedInActivity::class.java)
        startActivity(intent)
        finish()  // Kończymy obecną aktywność (RegisterActivity)
    }
}
