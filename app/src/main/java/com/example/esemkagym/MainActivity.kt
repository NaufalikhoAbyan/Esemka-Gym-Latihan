package com.example.esemkagym

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkagym.databinding.ActivityMainBinding
import com.example.esemkagym.model.LoginData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener {
            email = binding.tbEmail.text.toString()
            password = binding.tbPassword.text.toString()
//            Log.d("Email", email)
//            Log.d("Password", password)

//            startActivity(Intent(this, SignupActivity::class.java))
            val intent = Intent(this, SignupActivity::class.java)
            val loginData = LoginData(email, password)
            intent.putExtra("loginData", loginData)
            startActivity(intent)
        }
    }
}