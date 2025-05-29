package com.example.aiplanner.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.aiplanner.R

import com.example.aiplanner.MainActivity


class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // Załaduj GIF przy użyciu Glide
        val loadingGif = findViewById<ImageView>(R.id.loadingGif)
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()

        if (batteryPct > 20) {
            Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(loadingGif)
        } else {
            loadingGif.setImageResource(R.drawable.icon_background) // obrazek statyczny zamiast animacji
            Toast.makeText(this, "Tryb oszczędzania energii: animacja pominięta", Toast.LENGTH_SHORT).show()
        }


        // Ustaw opóźnienie i przejście do MainActivity po zakończeniu ładowania
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }, 3000) // 3000 ms = 3 sekundy (możesz dostosować)
    }
}
