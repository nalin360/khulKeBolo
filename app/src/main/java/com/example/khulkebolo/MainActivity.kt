package com.example.khulkebolo

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
//import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
import android.widget.EditText
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
//import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
//import com.example.khulkebolo.LanguageData.languages
//import com.google.android.material.textfield.TextInputEditText

//import com.google.android.material.textfield.MaterialAutoCompleteTextView
//import com.google.android.material.text-field.TextInputLayout
//import okio.IOException

class MainActivity : AppCompatActivity() {


    private var mediaPlayer: MediaPlayer? = null
//    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var isPaused = false
    val textToSpeechApi = TextToSpeechApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Replace with your layout file

//        val myUri: Uri = Uri.parse()

        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer()
        // Set up the MediaPlayer
//        initializeMediaPlayer(myUri)

        val textInputEditText = findViewById<EditText>(R.id.editTextText3)


//        mediaPlayer = MediaPlayer()



        // Set up onClickListeners for buttons
        findViewById<ImageButton>(R.id.play).setOnClickListener {
            val textToSynthesize = textInputEditText.text.toString()
//            val selectedLanguageCode = autoCompleteTextView.text.toString() // You might need to adjust this based on how your AutoCompleteTextView is set up
            synthesizeText(textToSynthesize)
        }

        findViewById<ImageButton>(R.id.pause).setOnClickListener {
            pauseMediaPlayer()
        }

        findViewById<ImageButton>(R.id.stop).setOnClickListener {
            stopMediaPlayer()
        }

        // Set up the SeekBar
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        mediaPlayer?.duration?.let { seekBar.max = it }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up the language selection AutoCompleteTextView



        // Update the SeekBar progress continuously
        updateSeekBarProgress()


    }

    private fun synthesizeText(textToSynthesize: String) {
        val loadingScreen = findViewById<LinearLayout>(R.id.loadingScreen)
        loadingScreen.visibility = View.VISIBLE
        // Get the selected language code from your dropdown or other input method
        val selectedLanguageCode = "Salli"

        textToSpeechApi.synthesizeText(
            text = textToSynthesize,
            languageCode = selectedLanguageCode,
            onResponse = { audioUrl ->
                // Hide loading screen on successful response
                loadingScreen.visibility = View.INVISIBLE
                // Now you have the audio URL, you can use it as needed
                // For example, you can update the MediaPlayer URI
                val audioUri = Uri.parse(audioUrl)
                mediaPlayer?.reset()
                initializeMediaPlayer(audioUri)
                startMediaPlayer()
            },
            onFailure = { error ->
                // Handle failure
                // Hide loading screen on failure
                loadingScreen.visibility = View.INVISIBLE
                Toast.makeText(this, "Failed to synthesize text: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun initializeMediaPlayer(uri: Uri) {
        // Show loading screen

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(applicationContext, uri)
                prepare()
            }
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            // Handle the IOException (log or display an error message)
        }
    }


    private fun startMediaPlayer() {
        mediaPlayer?.apply {
            if (!isPlaying && !isPaused) {
                start()
            } else if (isPaused) {
                // Resume playback if paused
                start()
                isPaused = false
            }
        }
    }

    private fun pauseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                isPaused = true
            }
        }
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying || isPaused) {
                stop()
                reset()
                isPaused = false
            }
        }
    }

    private fun updateSeekBarProgress() {
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val handler = Handler(Looper.getMainLooper())

        // Update SeekBar progress every 100 milliseconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    seekBar.progress = it.currentPosition
                }

                // Repeat the update every 100 milliseconds
                handler.postDelayed(this, 100)
            }
        }, 100)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}




