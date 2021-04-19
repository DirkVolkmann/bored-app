package com.dirk.bored

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dirk.bored.R.string
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GetActivityFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_getactivity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate view variables
        val button = view.findViewById<Button>(R.id.button_first)
        val textView = view.findViewById<TextView>(R.id.textview_first)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this.context)
        val url = "https://www.boredapi.com/api/activity/"
        val params = null
        val tag = "GetActivity"

        button.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            // Request a string response from the provided URL.
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, params,
                { response ->
                    // Display the first 500 characters of the response string.
                    Log.d("GetActivity", response.toString())
                    var activityText: String = "Failed to load activity"
                    if (response.has("activity")) {
                        activityText = response.getString("activity")
                        button.text = getString(string.get_another_activity)
                    }
                    textView.text = activityText
                },
                {
                    Log.d("GetActivity", "Failed to load activity")
                    textView.text = "Failed to load activity"
                }
            )

            // Use tag
            jsonObjectRequest.tag = tag

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)

        }
    }
}