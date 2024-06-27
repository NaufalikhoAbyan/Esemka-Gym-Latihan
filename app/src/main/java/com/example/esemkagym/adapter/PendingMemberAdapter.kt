package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.R
import com.example.esemkagym.databinding.MemberItemBinding
import com.example.esemkagym.model.Member

class PendingMemberAdapter(private val pendingMembers: List<Member>): RecyclerView.Adapter<PendingMemberAdapter.PendingMemberViewHolder>() {
    class PendingMemberViewHolder(private val binding: MemberItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pendingMember: Member){
            binding.tvMemberName.text = pendingMember.name
            binding.tvDate.text = pendingMember.date
            binding.tvButton.text = "Approve"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingMemberViewHolder {
        val binding = MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingMemberViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pendingMembers.size
    }

    override fun onBindViewHolder(holder: PendingMemberViewHolder, position: Int) {
        holder.bind(pendingMembers[position])
        holder.itemView.findViewById<TextView>(R.id.tvButton).setOnClickListener {
            onClickListener?.onItemClick(pendingMembers[position], position)
        }
    }

    interface OnClickListener {
        fun onItemClick(item: Member, position: Int)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}