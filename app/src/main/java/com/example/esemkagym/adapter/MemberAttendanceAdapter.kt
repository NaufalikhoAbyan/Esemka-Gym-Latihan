package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.databinding.MemberAttendanceItemBinding
import com.example.esemkagym.model.MemberAttendance

class MemberAttendanceAdapter(private val attendances: List<MemberAttendance>): RecyclerView.Adapter<MemberAttendanceAdapter.MemberAttendanceHolder>() {
    class MemberAttendanceHolder(private val binding: MemberAttendanceItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: MemberAttendance) {
            binding.checkInDate.text = attendance.checkIn
            binding.checkOutDate.text = attendance.checkOut
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAttendanceHolder {
        val binding = MemberAttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberAttendanceHolder(binding)
    }

    override fun getItemCount(): Int {
        return attendances.size
    }

    override fun onBindViewHolder(holder: MemberAttendanceHolder, position: Int) {
        holder.bind(attendances[position])
    }
}