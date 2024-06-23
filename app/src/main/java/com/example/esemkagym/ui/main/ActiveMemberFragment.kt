package com.example.esemkagym.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkagym.adapter.ActiveMemberAdapter
import com.example.esemkagym.databinding.FragmentActiveMemberBinding
import com.example.esemkagym.model.ActiveMember
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ActiveMemberFragment : Fragment() {
    private var _binding: FragmentActiveMemberBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ActiveMemberAdapter

    private lateinit var sharedPref: SharedPreferences

    var jsonResponse: JSONArray = JSONArray()

    private var activeMembers: MutableList<ActiveMember> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActiveMemberBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvActiveMember.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActiveMemberAdapter(activeMembers)
        binding.rvActiveMember.adapter = adapter

        adapter.notifyDataSetChanged()

        sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")


        binding.etSearch.doOnTextChanged { text, start, before, count ->
            activeMembers.clear()
            getActiveMembers(token!!, text.toString())
        }
    }

    private fun getActiveMembers(token: String, name: String) {
        val url = URL("http://10.0.2.2:8081/api/member?status=ACTIVE&name=$name")
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
                val name = jsonObject["name"]
                val date = jsonObject["membershipEnd"]
                activeMembers.add(ActiveMember(name.toString(), date.toString()))
                adapter.notifyDataSetChanged()
            }
        }else{
            activeMembers.clear()
            adapter.notifyDataSetChanged()
        }

        Log.d("Active Members : ", activeMembers.toString())
    }
}