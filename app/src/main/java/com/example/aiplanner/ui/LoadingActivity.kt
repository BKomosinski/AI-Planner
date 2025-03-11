package com.example.aiplanner.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.aiplanner.R

import com.example.aiplanner.MainActivity


class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // Załaduj GIF przy użyciu Glide
        val loadingGif = findViewById<ImageView>(R.id.loadingGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.loading) // nazwa pliku w res/drawable -> loading.gif
            .into(loadingGif)

        // Ustaw opóźnienie i przejście do MainActivity po zakończeniu ładowania
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3000 ms = 3 sekundy (możesz dostosować)
    }
}
