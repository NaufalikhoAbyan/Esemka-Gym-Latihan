package com.example.esemkagym.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.esemkagym.databinding.FragmentAttendanceBinding
import com.example.esemkagym.databinding.FragmentReportBinding
import com.example.esemkagym.model.MemberAttendance
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.reflect.typeOf

private var _binding: FragmentReportBinding? = null

// This property is only valid between onCreateView and
// onDestroyView.
private val binding get() = _binding!!

private var jsonResponse = JSONArray()
private val attendances: MutableList<MemberAttendance> = mutableListOf()

private lateinit var sharedPref: SharedPreferences
private var token = ""

class ReportFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "").toString()

        getAttendance(token)
    }

    private fun getAttendance(token: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance")
        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection) {
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
                        println(jsonResponse.getJSONObject(0).getJSONObject("user"))
//                        requireActivity().runOnUiThread {
//                            getAttendanceList(jsonResponse)
//                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}