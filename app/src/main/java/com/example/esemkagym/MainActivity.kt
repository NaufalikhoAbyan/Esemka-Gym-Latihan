package com.example.esemkagym

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var email: String = ""
    private var password: String = ""
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        binding.progressBar.visibility = View.GONE
        binding.btnSignIn.setOnClickListener {
            email = binding.tbEmail.text.toString()
            password = binding.tbPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                sendLoginData(email, password)
            } else {
                Toast.makeText(this@MainActivity, "Email dan Password should not empty!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendLoginData(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
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

                    if (responseCode == 200){
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                        }

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

                            with(sharedPref.edit()){
                                putString("TOKEN", jsonResponse["token"].toString())
                                apply()
                            }

                            startActivity(Intent(this@MainActivity, DailyCheckinActivity::class.java))
                        }
                    } else {
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Login Failed!", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        thread.start()
    }
}