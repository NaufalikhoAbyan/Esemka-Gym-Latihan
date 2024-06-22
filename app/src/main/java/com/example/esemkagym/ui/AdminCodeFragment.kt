package com.example.esemkagym.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.esemkagym.databinding.FragmentAdminCodeBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AdminCodeFragment : Fragment() {
    private var _binding: FragmentAdminCodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var token: String? = ""
    var jsonResponse: JSONObject = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminCodeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        var sharedPref = requireActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        token = sharedPref.getString("TOKEN", "")

        getCheckInCode(token!!)

        val date = sharedPref.getString("DATE", "")

        binding.tvDate.text = date
    }

    private fun getCheckInCode (bearerToken: String) {
        val url = URL("http://10.0.2.2:8081/api/attendance/checkin/code")
        val thread = Thread {
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    setRequestProperty("Authorization", "Bearer $bearerToken")

                    println("Code: $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }

                        it.close()

                        jsonResponse = JSONObject(response.toString())
                        println("Response: $jsonResponse")

                        this@AdminCodeFragment.requireActivity().runOnUiThread {
                            binding.tvCode.text = jsonResponse["code"].toString()
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}