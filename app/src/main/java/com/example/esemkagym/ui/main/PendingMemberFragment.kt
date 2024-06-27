package com.example.esemkagym.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkagym.adapter.PendingMemberAdapter
import com.example.esemkagym.databinding.FragmentPendingMemberBinding
import com.example.esemkagym.model.Member
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PendingMemberFragment : Fragment() {
    private var _binding: FragmentPendingMemberBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: PendingMemberAdapter

    private lateinit var sharedPref: SharedPreferences

    var jsonResponse: JSONArray = JSONArray()

    private var pendingMembers: MutableList<Member> = mutableListOf()

    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPendingMemberBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvActiveMember.layoutManager = LinearLayoutManager(requireContext())
        adapter = PendingMemberAdapter(pendingMembers)
        binding.rvActiveMember.adapter = adapter

        adapter.notifyDataSetChanged()

        sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "").toString()

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            populateView(text.toString())
        }

        adapter.setOnClickListener(object: PendingMemberAdapter.OnClickListener {
            override fun onItemClick(item: Member, position: Int) {
                approveMembership(item.id)
            }
        })
    }

    private fun approveMembership(id: String) {
        val url = URL("http://10.0.2.2:8081/api/member/$id/approve")
        val thread = Thread {
            with(url.openConnection() as HttpURLConnection) {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "PUT"

                    setRequestProperty("Authorization", "Bearer $token")
                    println("Response Code: $responseCode")
                    if (responseCode == 200){
                        requireActivity().runOnUiThread {
                            populateView(binding.etSearch.text.toString())
                            Toast.makeText(requireContext(), "Membership berhasil dimulai", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        thread.start()
    }

    private fun populateView(text: String){
        pendingMembers.clear()
        getPendingMembers(token!!, text)
    }

    private fun getPendingMembers(token: String, name: String) {
        val url = URL("http://10.0.2.2:8081/api/member?status=PENDING_APPROVAL&name=$name")
        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection){
                    requestMethod = "GET"

                    setRequestProperty("Authorization", "Bearer $token")

                    println("Response Code: $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }

                        it.close()

                        jsonResponse = JSONArray(response.toString())
                        println("URL: $url")
                        println("Response: $jsonResponse")
                        requireActivity().runOnUiThread {
                            getActiveMembersList(jsonResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun getActiveMembersList(jsonArray: JSONArray){
        if (jsonArray.length() != 0){
            for(i in 0 until jsonArray.length()){
                val jsonObject = JSONObject(jsonArray[i].toString())
                val id = jsonObject["id"]
                val name = jsonObject["name"]
                val date = jsonObject["registerAt"]
                pendingMembers.add(Member(id.toString(), name.toString(), date.toString()))
                adapter.notifyDataSetChanged()
            }
        }else{
            pendingMembers.clear()
            adapter.notifyDataSetChanged()
        }

        Log.d("Active Members : ", pendingMembers.toString())
    }
}