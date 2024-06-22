package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.databinding.ActiveMemberItemBinding
import com.example.esemkagym.model.ActiveMember

class ActiveMemberAdapter(private val activeMember: List<ActiveMember>): RecyclerView.Adapter<ActiveMemberAdapter.ActiveMemberHolder>() {
    class ActiveMemberHolder(private val binding: ActiveMemberItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(activeMember: ActiveMember) {
            binding.tvActiveMemberName.text = activeMember.name
            binding.tvMemberUntil.text = activeMember.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveMemberHolder {
        val binding = ActiveMemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiveMemberHolder(binding)
    }

    override fun getItemCount(): Int {
        return activeMember.size
    }

    override fun onBindViewHolder(holder: ActiveMemberHolder, position: Int) {
        holder.bind(activeMember[position])
    }
}