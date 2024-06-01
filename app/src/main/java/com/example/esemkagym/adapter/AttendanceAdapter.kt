package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.databinding.ItemAttedanceBinding
import com.example.esemkagym.model.Attendance

class AttendanceAdapter(private var attendances : List<Attendance>): RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder>() {
    class AttendanceHolder(private val binding: ItemAttedanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            binding.tvCheckInDate.text = attendance.checkIn
            binding.tvCheckOutDate.text = attendance.checkOut
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHolder {
        val binding = ItemAttedanceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceHolder, position: Int) {
        holder.bind(attendances[position])

        // onClick Scheme
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onItemClick(position, attendances[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return attendances.size
    }

    // OnClick Scheme
   private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onItemClick(position: Int, item: Attendance)
    }
}