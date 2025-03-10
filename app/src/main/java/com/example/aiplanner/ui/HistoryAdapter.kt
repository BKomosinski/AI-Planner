package com.example.aiplanner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aiplanner.R
import com.example.aiplanner.model.UserData

class HistoryAdapter(private val dataList: List<UserData>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = dataList[position]
        holder.hrvTextView.text = "HRV: ${userData.hrv}"
        holder.restingHeartRateTextView.text = "Resting Heart Rate: ${userData.restingHeartRate}"
        holder.weightTextView.text = "Weight: ${userData.weight}"
        holder.bedtimeTextView.text = "Bedtime: ${userData.bedtime}"
        holder.wakeupTimeTextView.text = "Wakeup Time: ${userData.wakeupTime}"
        holder.dateTextView.text = "Date: ${userData.date}"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hrvTextView: TextView = itemView.findViewById(R.id.hrvTextView)
        val restingHeartRateTextView: TextView = itemView.findViewById(R.id.restingHeartRateTextView)
        val weightTextView: TextView = itemView.findViewById(R.id.weightTextView)
        val bedtimeTextView: TextView = itemView.findViewById(R.id.bedtimeTextView)
        val wakeupTimeTextView: TextView = itemView.findViewById(R.id.wakeupTimeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }
}
