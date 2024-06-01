package com.example.esemkagym

import android.content.Intent
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

class DailyCheckinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyCheckinBinding
    private lateinit var adapter: AttendanceAdapter
    private var attendances = listOf(
        Attendance("2023-10-08 12:00", "2023-10-08 12:00"),
        Attendance("2024-10-08 13:00", "2023-10-08 12:00"),
        Attendance("2025-10-08 14:00", "2023-10-08 12:00"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyCheckinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        adapter = AttendanceAdapter(attendances)
        binding.rvAttendance.adapter = adapter

        adapter.setOnClickListener(object : AttendanceAdapter.OnClickListener {
            override fun onItemClick(position: Int, item: Attendance) {
                Toast.makeText(this@DailyCheckinActivity, item.checkIn, Toast.LENGTH_LONG).show()
            }
        })
    }

}