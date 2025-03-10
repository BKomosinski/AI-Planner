package com.example.aiplanner

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import com.example.aiplanner.ui.LoggedInActivity
import com.example.aiplanner.ui.LoginActivity
import com.example.aiplanner.ui.RegisterActivity


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If the user is logged in, proceed to LoggedInActivity (or main screen)
            navigateToLoggedInActivity()
        }

        // Initialize buttons for navigation to Login and Register activities
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Set onClickListeners for buttons
        loginButton.setOnClickListener {
            navigateToLoginActivity()
        }

        registerButton.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun navigateToLoggedInActivity() {
        val intent = Intent(this, LoggedInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}

