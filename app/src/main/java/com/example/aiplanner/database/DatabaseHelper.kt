package com.example.aiplanner.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.net.ConnectivityManager
import com.example.aiplanner.model.UserData
import java.text.SimpleDateFormat
import java.util.*
import java.lang.Float
import android.util.Log
import com.google.android.gms.common.util.CollectionUtils.mapOf
import java.sql.SQLException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_data.db"
        const val DATABASE_VERSION = 11
        const val TABLE_NAME = "user_data"
        const val COLUMN_ID = "id"
        const val COLUMN_HRV = "hrv"
        const val COLUMN_RESTING_HEART_RATE = "resting_heart_rate"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_BEDTIME = "bedtime"
        const val COLUMN_WAKEUP_TIME = "wakeup_time"
        const val COLUMN_DATE = "date"  // Dodajemy kolumnę dla daty
        const val COLUMN_USER_NAME = "user_name"

        const val TABLE_TASKS = "tasks"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_TITLE = "title"
        const val COLUMN_TASK_DATE = "date"

    }
    private val appContext: Context = context.applicationContext
    private val firestore = Firebase.firestore.apply {
        // tylko jeśli init nie był robiony wcześniej – bezpiecznie można wywołać wielokrotnie
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
    private val auth = Firebase.auth

    // shortcut do kolekcji subkolekcji user/{uid}/biometrics
    private fun biometricsCollection() =
        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("biometrics")

    // shortcut do kolekcji user/{uid}/tasks
    private fun tasksCollection() =
        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("tasks")

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
        val createTasksTable = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT,
                    date TEXT
                )
            """.trimIndent()

        db?.execSQL(createTasksTable)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            try {
                // Sprawdzamy, czy kolumna już istnieje
                val cursor = db.rawQuery("PRAGMA table_info($TABLE_NAME)", null)
                var columnExists = false
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val columnName = cursor.getString(cursor.getColumnIndex("name"))
                        if (columnName == COLUMN_USER_NAME) {
                            columnExists = true
                            break
                        }
                    }
                    cursor.close()
                }

                // Jeżeli kolumna 'user_name' nie istnieje, dodajemy ją
                if (!columnExists) {
                    db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_USER_NAME TEXT")
                }
            } catch (e: SQLException) {
                // Obsługuje przypadek, gdy kolumna już istnieje
                Log.e("Database", "Błąd przy aktualizacji bazy danych: ${e.message}")
            }
        }
    }


    fun saveUserData(hrv: String, restingHeartRate: String, weight: String, bedtime: String, wakeupTime: String, userName: String, date: String ) {
        val db = this.writableDatabase

        // Uzyskiwanie aktualnej daty

        val contentValues = ContentValues().apply {
            put(COLUMN_HRV, hrv)
            put(COLUMN_RESTING_HEART_RATE,restingHeartRate)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_BEDTIME, bedtime)
            put(COLUMN_WAKEUP_TIME, wakeupTime)
            put(COLUMN_DATE, date) // Dodanie daty
            put(COLUMN_USER_NAME, userName)
        }

        db.insert(TABLE_NAME, null, contentValues)
        db.close()

        val dataMap = mapOf(
            "hrv" to hrv,
            "resting_heart_rate" to restingHeartRate,
            "weight" to weight,
            "bedtime" to bedtime,
            "wakeup_time" to wakeupTime,
            "date" to date,
            "user_name" to userName
        )
        biometricsCollection()
            // używamy daty jako ID dokumentu – jeśli drugi wpis w ten sam dzień, nadpisze
            .document(date)
            .set(dataMap)
            .addOnSuccessListener { /* opcjonalnie Log.d(...) */ }
            .addOnFailureListener { e ->
                Log.e("DatabaseHelper", "Firestore saveUserData failed: ${e.message}")
            }
    }

    /**
     * Zwraca listę wpisów UserData dla danego userName i dokładnej daty.
     */
    fun getDataForDate(userName: String, date: String): List<UserData> {
        val result = mutableListOf<UserData>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER_NAME = ? AND $COLUMN_DATE = ?",
            arrayOf(userName, date)
        )
        if (cursor.moveToFirst()) {
            do {
                result += UserData(
                    hrv              = cursor.getString(cursor.getColumnIndex(COLUMN_HRV)),
                    restingHeartRate = cursor.getString(cursor.getColumnIndex(COLUMN_RESTING_HEART_RATE)),
                    weight           = cursor.getString(cursor.getColumnIndex(COLUMN_WEIGHT)),
                    bedtime          = cursor.getString(cursor.getColumnIndex(COLUMN_BEDTIME)),
                    wakeupTime       = cursor.getString(cursor.getColumnIndex(COLUMN_WAKEUP_TIME)),
                    date             = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return result
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
    fun addTask(title: String, date: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("date", date)
        }
        val rowId = db.insert(TABLE_TASKS, null, values)

        val taskId = rowId.toString() // lub sam generuj UUID jeśli nie chcesz zależeć od rowId
        val taskMap = mapOf(
            "id"    to taskId,
            "title" to title,
            "date"  to date
        )
        tasksCollection()
            .document(taskId)
            .set(taskMap)
            .addOnSuccessListener { /* OK */ }
            .addOnFailureListener { e ->
                Log.e("DatabaseHelper", "Firestore addTask failed: ${e.message}")
            }
    }
    private fun getTasksForDateLocal(date: String): List<String> {
        val list = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_TASK_TITLE FROM $TABLE_TASKS WHERE $COLUMN_TASK_DATE = ?",
            arrayOf(date)
        )
        while (cursor.moveToNext()) {
            list += cursor.getString(0)
        }
        cursor.close()
        db.close()
        return list
    }

    fun getTasksForDate(date: String, callback: (List<String>) -> Unit) {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        val isWifi = netInfo?.isConnected == true
                && netInfo.type == ConnectivityManager.TYPE_WIFI

        if (isWifi) {
            tasksCollection()
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { snap ->
                    val list = snap.documents.mapNotNull { it.getString("title") }
                    callback(list)
                }
                .addOnFailureListener {
                    callback(getTasksForDateLocal(date))
                }
        } else {
            callback(getTasksForDateLocal(date))
        }
    }

}