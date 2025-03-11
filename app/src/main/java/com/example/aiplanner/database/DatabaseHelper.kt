package com.example.aiplanner.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.aiplanner.model.UserData
import java.text.SimpleDateFormat
import java.util.*
import java.lang.Float
import android.util.Log
import java.sql.SQLException


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_data.db"
        const val DATABASE_VERSION = 8
        const val TABLE_NAME = "user_data"
        const val COLUMN_ID = "id"
        const val COLUMN_HRV = "hrv"
        const val COLUMN_RESTING_HEART_RATE = "resting_heart_rate"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_BEDTIME = "bedtime"
        const val COLUMN_WAKEUP_TIME = "wakeup_time"
        const val COLUMN_DATE = "date"  // Dodajemy kolumnę dla daty
        const val COLUMN_USER_NAME = "user_name"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_HRV TEXT, " +
                "$COLUMN_RESTING_HEART_RATE TEXT, " +
                "$COLUMN_WEIGHT TEXT, " +
                "$COLUMN_BEDTIME TEXT, " +
                "$COLUMN_WAKEUP_TIME TEXT, " +
                "$COLUMN_DATE TEXT,"+  // Dodajemy kolumnę dla daty
                "$COLUMN_USER_NAME TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) { // Zmien wersje, by dodac kolumnę tylko raz
            try {
                db.execSQL("ALTER TABLE user_data ADD COLUMN user_name TEXT")
            } catch (e: SQLException) {
                // Obsługuje przypadek, gdy kolumna już istnieje
                Log.e("Database", "Kolumna 'user_name' już istnieje")
            }
        }
    }

    // Metoda do pobierania ostatnich danych
//    fun getLastData(): UserData {
//        val db = readableDatabase
//        val cursor = db.query(
//            TABLE_NAME, null, null, null, null, null,
//            "$COLUMN_ID DESC", "1"
//        )
//
//        cursor?.moveToFirst()
//        val hrv = cursor?.getString(cursor.getColumnIndex(COLUMN_HRV)) ?: ""
//        val restingHeartRate = cursor?.getString(cursor.getColumnIndex(COLUMN_RESTING_HEART_RATE)) ?: ""
//        val weight = cursor?.getString(cursor.getColumnIndex(COLUMN_WEIGHT)) ?: ""
//        val bedtime = cursor?.getString(cursor.getColumnIndex(COLUMN_BEDTIME)) ?: ""
//        val wakeupTime = cursor?.getString(cursor.getColumnIndex(COLUMN_WAKEUP_TIME)) ?: ""
//        val date = cursor?.getString(cursor.getColumnIndex(COLUMN_DATE)) ?: ""
//
//        cursor?.close()
//
//        return UserData(hrv, restingHeartRate, weight, bedtime, wakeupTime, date)
//    }

    // Metoda do zapisywania danych użytkownika
    fun saveUserData(hrv: String, restingHeartRate: String, weight: String, bedtime: String, wakeupTime: String, userName: String) {
        val db = this.writableDatabase

        // Uzyskiwanie aktualnej daty
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val contentValues = ContentValues().apply {
            put(COLUMN_HRV, hrv)
            put(COLUMN_RESTING_HEART_RATE,restingHeartRate)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_BEDTIME, bedtime)
            put(COLUMN_WAKEUP_TIME, wakeupTime)
            put(COLUMN_DATE, currentDate) // Dodanie daty
            put(COLUMN_USER_NAME, userName)
        }

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    // Metoda do pobierania wszystkich danych
    fun getAllData(userName: String): List<UserData> {
        val dataList = mutableListOf<UserData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER_NAME = ? ORDER BY $COLUMN_DATE DESC", arrayOf(userName))

        if (cursor.moveToFirst()) {
            do {
                val hrv = cursor.getString(cursor.getColumnIndex(COLUMN_HRV)) ?: ""
                val restingHeartRate = cursor.getString(cursor.getColumnIndex(COLUMN_RESTING_HEART_RATE)) ?: ""
                val weight = cursor.getString(cursor.getColumnIndex(COLUMN_WEIGHT)) ?: ""
                val bedtime = cursor.getString(cursor.getColumnIndex(COLUMN_BEDTIME)) ?: ""
                val wakeupTime = cursor.getString(cursor.getColumnIndex(COLUMN_WAKEUP_TIME)) ?: ""
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)) ?: ""

                dataList.add(UserData(hrv, restingHeartRate, weight, bedtime, wakeupTime, date))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return dataList
    }

    // Metoda do wstawiania danych
    fun insertData(hrv: String, restingHeartRate: String, weight: String, bedtime: String, wakeupTime: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HRV, hrv)
            put(COLUMN_RESTING_HEART_RATE, restingHeartRate)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_BEDTIME, bedtime)
            put(COLUMN_WAKEUP_TIME, wakeupTime)
        }
        db.insert(TABLE_NAME, null, values)
    }
}
