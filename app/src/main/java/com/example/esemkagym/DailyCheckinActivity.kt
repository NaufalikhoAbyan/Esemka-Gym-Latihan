package com.example.esemkagym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkagym.adapter.AttendanceAdapter
import com.example.esemkagym.databinding.ActivityDailyCheckinBinding
import com.example.esemkagym.model.Attendance
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DailyCheckinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyCheckinBinding
    private lateinit var adapter: AttendanceAdapter
    private var attendances = listOf(
        Attendance("2023-10-08 12:00", "2023-10-08 12:00"),
        Attendance("2024-10-08 13:00", "2023-10-08 12:00"),
        Attendance("2025-10-08 14:00", "2023-10-08 12:00"),
    )

    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyCheckinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")

        getAttendanceData(token!!)

        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        adapter = AttendanceAdapter(attendances)
        binding.rvAttendance.adapter = adapter

        adapter.setOnClickListener(object : AttendanceAdapter.OnClickListener {
            override fun onItemClick(position: Int, item: Attendance) {
                Toast.makeText(this@DailyCheckinActivity, item.checkIn, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getAttendanceData(token: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance")

        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Authorization", "Bearer $token")

                    println("URL : $url")
                    println("Response Code : $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()

                        val jsonResponse = JSONObject(response.toString())

                        println("Response : $jsonResponse")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

}