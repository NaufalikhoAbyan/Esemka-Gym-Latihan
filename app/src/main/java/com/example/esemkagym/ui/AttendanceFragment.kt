package com.example.esemkagym.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.esemkagym.adapter.MemberAttendanceAdapter
import com.example.esemkagym.databinding.FragmentAttendanceBinding
import com.example.esemkagym.model.Member
import com.example.esemkagym.model.MemberAttendance
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private var _binding: FragmentAttendanceBinding? = null

// This property is only valid between onCreateView and
// onDestroyView.
private val binding get() = _binding!!

private var jsonResponse = JSONArray()
private val attendances: MutableList<MemberAttendance> = mutableListOf()

private lateinit var adapter: MemberAttendanceAdapter

private lateinit var sharedPref: SharedPreferences
private var token = ""

class AttendanceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMemberAttendance.layoutManager = LinearLayoutManager(requireContext())
        adapter = MemberAttendanceAdapter(attendances)
        binding.rvMemberAttendance.adapter = adapter

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
                attendances.add(MemberAttendance(checkIn.toString(), checkOut.toString()))
                adapter.notifyDataSetChanged()
            }
        }else{
            attendances.clear()
            adapter.notifyDataSetChanged()
        }

        Log.d("Active Members : ", attendances.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        attendances.clear()
    }
}