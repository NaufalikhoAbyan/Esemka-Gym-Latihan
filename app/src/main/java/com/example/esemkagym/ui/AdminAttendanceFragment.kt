package com.example.esemkagym.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkagym.R
import com.example.esemkagym.adapter.AdminAttendanceAdapter
import com.example.esemkagym.adapter.MemberAttendanceAdapter
import com.example.esemkagym.databinding.FragmentAdminAttendanceBinding
import com.example.esemkagym.model.MemberAttendance
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private var _binding: FragmentAdminAttendanceBinding? = null
private val binding get() = _binding!!

private var jsonResponse = JSONArray()
private val attendances: MutableList<MemberAttendance> = mutableListOf()

private lateinit var adapter: AdminAttendanceAdapter

private lateinit var sharedPref: SharedPreferences
private var token = ""

class AdminAttendanceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAdminAttendance.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdminAttendanceAdapter(attendances)
        binding.rvAdminAttendance.adapter = adapter

        sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "").toString()
        getAttendance(token)
    }

    private fun getAttendance(token: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance")
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
                            getAttendanceList(jsonResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun getAttendanceList(jsonArray: JSONArray) {
        if (jsonArray.length() != 0){
            for(i in 0 until jsonArray.length()){
                val jsonObject = JSONObject(jsonArray[i].toString())
                val checkIn = jsonObject["checkIn"]
                val checkOut = jsonObject["checkOut"]
                val user = jsonArray.getJSONObject(i).getJSONObject("user")
                val gender = user["gender"]
                val name = user["name"]
                attendances.add(MemberAttendance(gender.toString(), name.toString(), checkIn.toString(), checkOut.toString()))
                adapter.notifyDataSetChanged()
            }
        }else{
            attendances.clear()
            adapter.notifyDataSetChanged()
        }

        Log.d("Attendance Data : ", attendances.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        attendances.clear()
    }
}