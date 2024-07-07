package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.R
import com.example.esemkagym.databinding.AdminAttendanceItemBinding
import com.example.esemkagym.model.MemberAttendance

class AdminAttendanceAdapter(private val attendances: List<MemberAttendance>): RecyclerView.Adapter<AdminAttendanceAdapter.AdminAttendanceHolder>() {
    class AdminAttendanceHolder(private val binding: AdminAttendanceItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(attendance: MemberAttendance) {
            binding.ivProfile.setImageResource(if (attendance.gender == "MALE") R.drawable.male else R.drawable.female)
            binding.tvMemberName.text = attendance.name
            binding.tvCheckIn.text = attendance.checkIn
            binding.tvCheckOut.text = attendance.checkOut
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAttendanceHolder {
        val binding = AdminAttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminAttendanceHolder(binding)
    }

    override fun getItemCount(): Int {
        return attendances.size
    }

    override fun onBindViewHolder(holder: AdminAttendanceHolder, position: Int) {
        holder.bind(attendances[position])
    }
}