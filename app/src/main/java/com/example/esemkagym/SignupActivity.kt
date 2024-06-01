package com.example.esemkagym

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivitySignupBinding
import com.example.esemkagym.model.LoginData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var loginData = intent.getSerializableExtra("loginData") as LoginData

        binding.textView.text = loginData.email

        sendLoginData(loginData.email, loginData.password)
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