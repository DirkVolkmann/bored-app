package com.dirk.bored

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dirk.bored.R.string


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

        /**
         * Read stuff form settings
         */

        // Create shared preference object
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)

        // Switch values
        val prefTypeSwitch =            sharedPrefs.getBoolean("switch_type", false)
        val prefParticipantsSwitch =    sharedPrefs.getBoolean("switch_participants", false)
        val prefPriceSwitch =           sharedPrefs.getBoolean("switch_price", false)
        val prefAccessibilitySwitch =   sharedPrefs.getBoolean("switch_accessibility", false)

        Log.d("GetActivity", "prefTypeSwitch $prefTypeSwitch")
        Log.d("GetActivity", "prefParticipantsSwitch $prefParticipantsSwitch")
        Log.d("GetActivity", "prefPriceSwitch $prefPriceSwitch")
        Log.d("GetActivity", "prefAccessibilitySwitch $prefAccessibilitySwitch")

        /**
         * Handle views
         */

        // Instantiate view values
        val buttonBored = view.findViewById<Button>(R.id.button_bored)
        val buttonLink = view.findViewById<Button>(R.id.button_link)
        val textView = view.findViewById<TextView>(R.id.textview_activity)

        /**
         * Build params
         */

        var paramsMap = ArrayMap<String, String>()

        // Type
        if (!prefTypeSwitch) {
            // Get value
            val prefType = sharedPrefs.getString("pref_type", getString(string.summary_not_set))
            Log.d("GetActivity", "prefType $prefType")

            // Set parameter
            paramsMap.put("type", prefType)
        }

        // Participants
        if (!prefParticipantsSwitch) {
            // Get value
            val prefParticipants = sharedPrefs.getString("pref_participants", getString(string.summary_not_set))
            Log.d("GetActivity", "prefParticipants $prefParticipants")

            // Set parameter
            paramsMap.put("participants", prefParticipants)
        }

        // Price
        if (!prefPriceSwitch) {
            // Get value
            val prefPrice = sharedPrefs.getInt("pref_price", 50)
            Log.d("GetActivity", "prefPrice $prefPrice")

            val paramPrice = (prefPrice.toFloat()/100).toString()

            // Set parameter
            paramsMap.put("price", paramPrice)
        }

        // Accessibility
        if (!prefAccessibilitySwitch) {
            // Get value
            val prefAccessibility = sharedPrefs.getInt("pref_accessibility", 50)
            Log.d("GetActivity", "prefAccessibility $prefAccessibility")

            // Set range depending on setting
            var min: Int
            var max: Int
            when {
                prefAccessibility == 100 -> {
                    min = 100
                    max = min
                }
                prefAccessibility > 66 -> {
                    min = 66
                    max = 100
                }
                prefAccessibility > 33 -> {
                    min = 33
                    max = 66
                }
                prefAccessibility > 0 -> {
                    min = 0
                    max = 33
                }
                else -> {
                    min = 0
                    max = min
                }
            }

            // Set parameter(s)
            if (min >= 0 && max >= 0) {
                if (min == max) {
                    paramsMap.put("accessibility", (min.toFloat() / 100).toString())
                }
                else {
                    paramsMap.put("minaccessibility", (min.toFloat() / 100).toString())
                    paramsMap.put("maxaccessibility", (max.toFloat() / 100).toString())
                }
            }
        }

        val paramsString = paramsMap.entries.map { (it.key + "=" +  it.value) }.joinToString(prefix = "?", separator = "&")

        Log.d("GetActivity", "params $paramsString")

        /**
         * Send request onClick and add to queue
         */

        // Instantiate the RequestQueue
        val queue = Volley.newRequestQueue(this.context)
        val url = "https://www.boredapi.com/api/activity/"
        val tag = "GetActivity"

        buttonBored.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            // Request a string response from the provided URL.
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url + paramsString, null,
                    { response ->
                        // Display the first 500 characters of the response string.
                        Log.d("GetActivity", response.toString())
                        var activityText: String = getString(string.request_failed)

                        // Error
                        if (response.has("error")) {
                            activityText = response.getString("error")
                        }
                        // Display activity
                        else if (response.has("activity")) {
                            buttonBored.text = getString(string.get_another_activity)
                            activityText = response.getString("activity")

                            // Activate link button if link is available
                            if (response.has("link") && response.getString("link") != "") {
                                val link = response.getString("link")
                                Log.d("GetActivity", "link $link")
                                buttonLink.contentDescription = link
                                buttonLink.visibility = VISIBLE
                            } else {
                                buttonLink.visibility = GONE
                            }
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

        buttonLink.setOnClickListener {
            val link: String = it.contentDescription.toString()
            if (link != "") {
                val uriUrl: Uri = Uri.parse(link)
                val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                startActivity(launchBrowser)
            }
        }
    }
}