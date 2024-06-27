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
import com.example.esemkagym.adapter.InactiveMemberAdapter
import com.example.esemkagym.databinding.FragmentInactiveMemberBinding
import com.example.esemkagym.model.Member
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InactiveMemberFragment : Fragment() {
    private var _binding: FragmentInactiveMemberBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: InactiveMemberAdapter

    private val inactiveMembers: MutableList<Member> = mutableListOf()

    private var jsonResponse = JSONArray()

    private lateinit var sharedPref: SharedPreferences
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInactiveMemberBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvInactiveMember.layoutManager = LinearLayoutManager(requireContext())
        adapter = InactiveMemberAdapter(inactiveMembers)
        binding.rvInactiveMember.adapter = adapter

        sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "").toString()

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            populateView(text.toString())
        }

        adapter.setOnclickListener(object: InactiveMemberAdapter.OnClickListener {
            override fun onClick(item: Member) {
                resumeMembership(item.id)
            }
        })
    }

    private fun populateView(text: String){
        inactiveMembers.clear()
        getInactiveMember(token!!, text)
    }

    private fun getInactiveMember(token: String, name: String) {
        val url = URL("http://10.0.2.2:8081/api/member?status=INACTIVE&name=$name")
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
                            getInactiveMembersList(jsonResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun getInactiveMembersList(jsonArray: JSONArray){
        if (jsonArray.length() != 0){
            for(i in 0 until jsonArray.length()){
                val jsonObject = JSONObject(jsonArray[i].toString())
                val id = jsonObject["id"]
                val name = jsonObject["name"]
                val date = jsonObject["membershipEnd"]
                inactiveMembers.add(Member(id.toString(), name.toString(), date.toString()))
                adapter.notifyDataSetChanged()
            }
        }else{
            inactiveMembers.clear()
            adapter.notifyDataSetChanged()
        }

        Log.d("Active Members : ", inactiveMembers.toString())
    }

    private fun resumeMembership(id: String) {
        val url = URL("http://10.0.2.2:8081/api/member/$id/approve")

        val thread = Thread{
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "PUT"

                setRequestProperty("Authorization", "Bearer $token")
                println("Response Code: $responseCode")
                if (responseCode == 200){
                    requireActivity().runOnUiThread {
                        populateView(binding.etSearch.text.toString())
                        Toast.makeText(requireContext(), "Membership berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        thread.start()
    }
}