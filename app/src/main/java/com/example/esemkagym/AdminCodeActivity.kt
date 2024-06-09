package com.example.esemkagym

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivityAdminCodeBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdminCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminCodeBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val token = sharedPref.getString("TOKEN", "")

        getCheckInCode(token!!)

        val date = sharedPref.getString("DATE", "")

        binding.tvDate.text = date
    }

    private fun getCheckInCode (bearerToken: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance/checkin/code")
        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"

                    setRequestProperty("Authorization", "Bearer $bearerToken")

                    println("Code: $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use{
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while(inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }

                        it.close()

                        val jsonResponse = JSONObject(response.toString())
                        println("Response: $jsonResponse")

                        binding.tvCode.text = jsonResponse["code"].toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}