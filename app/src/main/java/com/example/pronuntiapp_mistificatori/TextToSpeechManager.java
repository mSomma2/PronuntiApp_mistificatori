package com.example.pronuntiapp_mistificatori;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TextToSpeechManager implements TextToSpeech.OnInitListener {

    private final TextToSpeech textToSpeech;
    private boolean isInitialized = false;

    public TextToSpeechManager(Context context) {
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.ITALIAN);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TAG", "italiano non supportato");
            } else {
                isInitialized = true;
            }
        }
    }

    public void speak(String word, float speed) {
        if (isInitialized) {
            textToSpeech.setSpeechRate(speed);
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
