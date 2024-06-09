package com.example.esemkagym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.esemkagym.databinding.ActivityAdminDashbardBinding
import com.google.android.material.navigation.NavigationView

class AdminDashbardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashbardBinding
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashbardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_admin_dashbard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        navView.getHeaderView(0).findViewById<ImageView>(R.id.ivClose).setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START, true)
        }

        sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val username = sharedPref.getString("NAME", "")
        val email = sharedPref.getString("EMAIL", "")

        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvUsername).text = username
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvEmail).text = email

        binding.logout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}