package com.example.esemkagym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivityCheckInBinding
import java.net.HttpURLConnection
import java.net.URL

class CheckInActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityCheckInBinding
    private var isCheckedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")

        binding.tvSignOut.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvDate.text = sharedPref.getString("DATE", "")

        val code = binding.tbCode.text

        binding.btnCheckIn.setOnClickListener {
            if (!isCheckedIn) {
                checkIn(token!!, code.toString())
            } else {
                checkOut(token!!, code.toString())
            }
        }
    }

    private fun checkIn(token: String, code: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance/checkin/$code")
        val thread = Thread {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                setRequestProperty("Authorization", "Bearer $token")
                println("Response code: $responseCode")
                if (responseCode == 200) {
                    isCheckedIn = true
                    runOnUiThread {
                        binding.tbCode.isEnabled = false
                        binding.btnCheckIn.text = "CheckOut"
                    }
                }
            }
        }
        thread.start()
    }

    private fun checkOut(token: String, code: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance/checkout")
        val thread = Thread {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                setRequestProperty("Authorization", "Bearer $token")
                println("Response code: $responseCode")
                if (responseCode == 200) {
                    runOnUiThread {
                        binding.btnCheckIn.isEnabled = false
                        binding.btnCheckIn.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                    }
                }
            }
        }
        thread.start()
    }

}