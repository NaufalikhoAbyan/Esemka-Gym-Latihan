package com.example.esemkagym

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivitySignUpBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpdata: JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var email = binding.tbEmail.text
        var password = binding.tbPassword.text
        var radioMale = binding.rbMale
        var radioFemale = binding.rbFemale
        var gender = ""
        var name = ""

        binding.btnSignUp.setOnClickListener{
            if(email!!.isEmpty() || password!!.isEmpty()){
                Toast.makeText(this, "Mohon isi semua kolom!", Toast.LENGTH_SHORT).show()
            } else if(!radioMale.isChecked && !radioFemale.isChecked){
                Toast.makeText(this, "Mohon isi gender!", Toast.LENGTH_SHORT).show()
            } else {
                var success = false
                gender = if(radioMale.isChecked) "MALE" else "FEMALE"
                try {
                    name = email.subSequence(0, email.indexOf('@')).toString()
                    success = true
                } catch (e: Exception) {
                    Toast.makeText(this, "Masukan email yang valid!", Toast.LENGTH_SHORT).show()
                }
                if (success) {
                    sendRegisterData(email.toString(), gender, name, password.toString())
                }
            }
        }
    }

    private fun sendRegisterData(email: String, gender: String, name: String, password: String) {
        signUpdata = JSONObject()
        signUpdata.put("email", email)
        signUpdata.put("gender", gender)
        signUpdata.put("name", name)
        signUpdata.put("password", password)
        println(signUpdata)

        val url = URL("http://10.0.2.2:8081/api/signup")
        val thread = Thread {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                setRequestProperty("Content-Type", "Application/json")
                setRequestProperty("Accept", "Application/json")

                val sw = OutputStreamWriter(outputStream)
                sw.write(signUpdata.toString())
                sw.flush()

                println("Response code: $responseCode")

                if(responseCode == 200) {
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()
                        response.append(it.readLine())
                        if (it.readLine() != null) {
                            response.append(it.readLine())
                        }
                        it.close()
                        val jsonResponse = JSONObject(response.toString())
                        println("Response: $jsonResponse")

                        val intent = Intent(this@SignUpActivity, RegisteredActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        thread.start()
    }
}
