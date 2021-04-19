package com.dirk.bored

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
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

        // Read from settings
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val prefTypeSwitch =            sharedPref.getBoolean("switch_type", false)
        val prefParticipantsSwitch =    sharedPref.getBoolean("switch_participants", false)
        val prefPriceSwitch =           sharedPref.getBoolean("switch_price", false)
        val prefAccessibilitySwitch =   sharedPref.getBoolean("switch_accessibility", false)

        val prefType =          sharedPref.getString("pref_type", getString(string.summary_not_set))
        val prefParticipants =  sharedPref.getString("pref_participants", getString(string.summary_not_set))
        val prefPrice =         sharedPref.getInt("pref_price", 50)
        val prefAccessibility = sharedPref.getInt("pref_accessibility", 50)

        Log.d("GetActivity", "switchTypePref $prefTypeSwitch")
        Log.d("GetActivity", "switchParticipantsPref $prefParticipantsSwitch")
        Log.d("GetActivity", "switchPricePref $prefPriceSwitch")
        Log.d("GetActivity", "switchAccessibilityPref $prefAccessibilitySwitch")
        Log.d("GetActivity", "typePref $prefType")
        Log.d("GetActivity", "participantsPref $prefParticipants")
        Log.d("GetActivity", "pricePref $prefPrice")
        Log.d("GetActivity", "accessibilityPref $prefAccessibility")

        // Instantiate view variables
        val button = view.findViewById<Button>(R.id.button_first)
        val textView = view.findViewById<TextView>(R.id.textview_first)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this.context)
        val url = "https://www.boredapi.com/api/activity/"
        val tag = "GetActivity"

        // Build params
        var paramsMap = ArrayMap<String, String>()
        if (!prefTypeSwitch) {
            paramsMap.put("type", prefType)
        }
        if (!prefParticipantsSwitch) {
            paramsMap.put("participants", prefParticipants)
        }
        if (!prefPriceSwitch) {
            val paramPrice = (prefPrice.toFloat()/100).toString()
            paramsMap.put("price", paramPrice)
        }
        if (!prefAccessibilitySwitch) {
            val paramAccessibility = (prefAccessibility.toFloat()/100).toString()
            paramsMap.put("accessibility", paramAccessibility)
        }

        val paramsString = paramsMap.entries.map { (it.key + "=" +  it.value) }.joinToString(prefix = "?", separator = "&")

        Log.d("GetActivity", paramsString)

        button.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            // Request a string response from the provided URL.
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url + paramsString, null,
                { response ->
                    // Display the first 500 characters of the response string.
                    Log.d("GetActivity", response.toString())
                    var activityText: String = getString(string.request_failed)
                    if (response.has("activity")) {
                        activityText = response.getString("activity")
                        button.text = getString(string.get_another_activity)
                    }
                    textView.text = activityText
                },
                {
                    textView.text = getString(string.request_failed)
                    Log.d("GetActivity", getString(string.request_failed))
                }
            )

            // Use tag
            jsonObjectRequest.tag = tag

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)

        }
    }
}