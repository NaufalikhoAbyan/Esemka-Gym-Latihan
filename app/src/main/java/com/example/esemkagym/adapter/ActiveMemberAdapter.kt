package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.R
import com.example.esemkagym.databinding.MemberItemBinding
import com.example.esemkagym.model.Member
import java.time.LocalDate

class ActiveMemberAdapter(private val activeMember: List<Member>): RecyclerView.Adapter<ActiveMemberAdapter.ActiveMemberHolder>() {
    class ActiveMemberHolder(private val binding: MemberItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(activeMember: Member) {
            binding.tvMemberName.text = activeMember.name
            binding.tvDate.text = activeMember.date
            val today = LocalDate.now()
            val threshold = LocalDate.parse(activeMember.date).minusDays(7)
            binding.tvResume.isVisible = today >= threshold
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveMemberHolder {
        val binding = MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiveMemberHolder(binding)
    }

    override fun getItemCount(): Int {
        return activeMember.size
    }

    override fun onBindViewHolder(holder: ActiveMemberHolder, position: Int) {
        holder.bind(activeMember[position])
        holder.itemView.findViewById<TextView>(R.id.tvResume).setOnClickListener {
            onClickListener?.onItemClick(position, activeMember[position])
        }
    }

    interface OnClickListener {
        fun onItemClick(position: Int, item: Member)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}