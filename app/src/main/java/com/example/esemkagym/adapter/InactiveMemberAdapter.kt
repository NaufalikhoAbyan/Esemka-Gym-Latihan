package com.example.esemkagym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkagym.R
import com.example.esemkagym.databinding.MemberItemBinding
import com.example.esemkagym.model.Member

class InactiveMemberAdapter(private val inactiveMember: List<Member>): RecyclerView.Adapter<InactiveMemberAdapter.InactiveMemberHolder>() {
    class InactiveMemberHolder(private val binding: MemberItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(inactiveMember: Member){
            binding.tvMemberName.text = inactiveMember.name
            binding.tvDate.text = inactiveMember.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InactiveMemberHolder {
        val binding = MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InactiveMemberHolder(binding)
    }

    override fun getItemCount(): Int {
        return inactiveMember.size
    }

    override fun onBindViewHolder(holder: InactiveMemberHolder, position: Int) {
        holder.bind(inactiveMember[position])
        holder.itemView.findViewById<TextView>(R.id.tvResume).setOnClickListener {
            onClickListener?.onClick(inactiveMember[position])
        }
    }

    interface OnClickListener {
        fun onClick(item: Member)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnclickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}