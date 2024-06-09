package com.example.esemkagym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPref: SharedPreferences

    private val currentDate = LocalDate.now()
    private var displayDate = "";

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val indonesiaLocale = Locale("id", "ID")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", indonesiaLocale)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", indonesiaLocale)

        val date = inputFormat.parse(currentDate.toString())
        displayDate = outputFormat.format(date)

        binding.btnSignIn.setOnClickListener {
            email = binding.tbEmail.text.toString()
            password = binding.tbPassword.text.toString()

            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua kolom!", Toast.LENGTH_SHORT).show()
            } else {
                sendLoginData(email, password)
            }
        }
    }

    private fun sendLoginData (email: String, password: String) {
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        println(jsonObject.toString())

        val url = URL("http://10.0.2.2:8081/api/login")

        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"

                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    doInput = true
                    doOutput = true

                    val wr = OutputStreamWriter(outputStream)
                    wr.write(jsonObject.toString())
                    wr.flush()

                    println("URL : $url")
                    println("Response Code : $responseCode")

                    if(responseCode == 200){
                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()

                            val jsonResponse = JSONObject(response.toString())
                            val jsonUserData = jsonResponse.getJSONObject("user")

                            println("Response : $jsonResponse")

                            with(sharedPref.edit()){
                                putString("NAME", jsonUserData["name"].toString())
                                putString("EMAIL", jsonUserData["email"].toString())
                                putString("TOKEN", jsonResponse["token"].toString())
                                putString("DATE", displayDate)
                                apply()
                            }

                            val intent = Intent(this@MainActivity, AdminCodeActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
    }
}