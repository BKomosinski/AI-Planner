package com.example.aiplanner.ui

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aiplanner.R
import com.example.aiplanner.database.DatabaseHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar


class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getTodayDate()

        auth = FirebaseAuth.getInstance()

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        val user = auth.currentUser
        val userName = user?.email ?: "Unknown User"

        // Pobierz dane z bazy
        val dataList = dbHelper.getDataForDate(userName, selectedDate)

        // Ustaw adapter RecyclerView
        adapter = HistoryAdapter(dataList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    private fun getTodayDate(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.DAY_OF_MONTH)}/" +
                "${cal.get(Calendar.MONTH)+1}/" +
                "${cal.get(Calendar.YEAR)}"
    }
}

