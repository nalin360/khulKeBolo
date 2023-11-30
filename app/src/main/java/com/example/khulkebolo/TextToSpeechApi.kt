package com.example.khulkebolo

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class TextToSpeechApi {
    private val client = OkHttpClient()

    // Extension function to encode text for a URL
    fun String.urlEncode(): String {
        return URLEncoder.encode(this, "UTF-8")
    }
    // Function to synthesize text to speech
    fun synthesizeText(
        text: String,
        languageCode: String,
        onResponse: (audioUrl: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val apiKey = API_KEY
        val url = API_URL // Replace with your new API endpoint

//        val jsonBody = JSONObject()
//        jsonBody.put("text", text)
//        jsonBody.put("language_code", languageCode)

        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, "msg=${text.urlEncode()}&lang=${languageCode}&source=ttsmp3")

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            // Add any necessary headers for your new API
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("X-RapidAPI-Key", apiKey)
            .addHeader("X-RapidAPI-Host", "text-to-speech-for-28-languages.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonResponse = JSONObject(response.body?.string())
                        val audioUrl = jsonResponse.getString("URL") // Extract the audio URL
                        onResponse(audioUrl)
                    } catch (e: JSONException) {
                        onFailure("Error parsing JSON: ${e.message}")
                    }
                } else {
                    onFailure("Error: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure("Network error: ${e.message}")
            }
        })
    }

    // Function to get supported languages
    // Function to get supported languages
    fun getSupportedLanguages(
        onResponse: (languages: List<Pair<String, String>>) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val url = "https://cloudlabs-text-to-speech.p.rapidapi.com/languages"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Host", "cloudlabs-text-to-speech.p.rapidapi.com")
            .addHeader("X-RapidAPI-Key", "8292bc0f34msh9c332479f6a0823p14a994jsnb22418fc602f") // Replace with your actual API key
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonResponse = JSONObject(response.body?.string())
                        if (jsonResponse.has("languages")) {
                            val languagesArray = jsonResponse.getJSONArray("languages")
                            val languages = mutableListOf<Pair<String, String>>()
                            for (i in 0 until languagesArray.length()) {
                                val languageObject = languagesArray.getJSONObject(i)
                                val languageName = languageObject.getString("language_name")
                                val languageCode = languageObject.getString("language_code")
                                languages.add(Pair(languageName, languageCode))
                            }
                            onResponse(languages)
                        } else {
                            onFailure("Error: 'languages' key not found in the response")
                        }
                    } catch (e: JSONException) {
                        onFailure("Error parsing JSON: ${e.message}")
                    }
                } else {
                    onFailure("Error: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure("Network error: ${e.message}")
            }
        })
    }

}
