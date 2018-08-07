package com.sudo.nikhil.gro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;
import java.util.ArrayList;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.ActivityNotFoundException;

import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    // Interfaces
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;

    // Speech to Text input code
    private final int REQ_CODE_SPEECH_INPUT = 100;

    // String to be spoken by the user
    private String textSpoken;

    // Text to speech variables
    TextToSpeech tts;
    int result;

    RegexEngine regexEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pass the application context to the regex engine
        regexEngine = new RegexEngine(getApplicationContext());

        // Link the XML elements to Java code
        txtSpeechInput = findViewById(R.id.txtSpeechInput);
        btnSpeak = findViewById(R.id.btnSpeak);

        // Set onClickListener for button
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prompt speech input
                promptSpeechInput();
            }
        });

        // TTS onInitListener, called every time TTS is initialized
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // If initialization is a success, then set the language to English, UK
                if (status == TextToSpeech.SUCCESS) {
                    result = tts.setLanguage(Locale.UK);
                } else {
                    // Tell the user that TTS is not possible on their device
                    Toast.makeText(getApplicationContext(), R.string.tts_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Prompt speech input
    private void promptSpeechInput() {
        // Speech recognizer intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Consider input in free form English
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Get the default locale of the user's device and set it as an extra language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // Prompt string for speech input
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_prompt);

        // Try recognising and converting speech to text, if it doesn't work, it may not be available
        try {
            // Start activity
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e){
            // Tell the user that speech-to-text is not supported on their device
            Toast.makeText(getApplicationContext(), R.string.speech_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Switch for specific request code
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    // Get the result of speech-to-text conversion
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // Set the resulting string to the text label
                    txtSpeechInput.setText(result.get(0));
                    // Set the resulting string to the global variable
                    textSpoken = result.get(0);
                    // Pass the utterance to the regex engine
                    speakOut(regexEngine.coreUttProcessor(textSpoken));
                }
            }
        }
    }

    public void speakOut(String text) {
        // Inform the user if the language is not supported or its data is missing
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(getApplicationContext(), R.string.tts_not_available, Toast.LENGTH_LONG).show();
        } else {
            // Speak the text passed in as a parameter
            tts.speak(text, TextToSpeech.QUEUE_FLUSH,null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
