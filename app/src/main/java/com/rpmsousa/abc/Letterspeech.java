package com.rpmsousa.abc;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.lang.System;

public class Letterspeech {
    private final TextToSpeech mSpeech;
    private ArrayList<Voice> mVoice;
    private final HashMap<String, callback> mHash;

    private class ProgressListener extends UtteranceProgressListener {
        @Override
        public void onStart(String utteranceId) {
            System.out.println("start");
        }

        @Override
        public void onError(String utteranceId) {
            System.out.println("error");

            callback c = mHash.remove(utteranceId);

            if (c != null)
                c.done(-1);
        }

        @Override
        public void onDone(String utteranceId) {
            System.out.println("done");

            callback c = mHash.remove(utteranceId);

            if (c != null)
                c.done(1);
        }

        @Override
        public void onStop(String utteranceId, boolean interrupted) {
            System.out.println("stop");

            callback c = mHash.remove(utteranceId);

            if (c != null)
                c.done(0);
        }
    }

    private class InitListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {

            //Locale locale = new Locale("pt", "PT");
            //Locale locale = new Locale("fr", "FR");

            mSpeech.setPitch(1.0f);
            mSpeech.setSpeechRate(1.0f);

            set_language(mSpeech.getDefaultVoice().getLocale());
            //  if (mSpeech.isLanguageAvailable(locale_pt) == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE)
            //      mSpeech.setLanguage(locale_pt);
            //  else if (mSpeech.isLanguageAvailable(locale_fr) == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE)
            //          mSpeech.setLanguage(locale);
        }
    }

    Letterspeech(Context context) {
        InitListener mInitListener = new InitListener();

        mHash = new HashMap<>();

        mVoice = new ArrayList<>(0);

        mSpeech = new TextToSpeech(context, mInitListener);

        ProgressListener mProgressListener = new ProgressListener();
        mSpeech.setOnUtteranceProgressListener(mProgressListener);

        }

    public void set_language(Locale locale) {
        Set<Voice> voiceset;
        Voice[] voice;

        mSpeech.setLanguage(locale);

        voiceset = mSpeech.getVoices();
        voice = voiceset.toArray(new Voice[0]);

        mVoice.clear();

        for (Voice value : voice) {

            if (value.isNetworkConnectionRequired())
                continue;

            if (value.getQuality() < Voice.QUALITY_NORMAL)
                continue;

            if (!value.getLocale().getISO3Language().equals(locale.getISO3Language()))
                continue;

            if (!value.getLocale().getISO3Country().equals(locale.getISO3Country()))
                continue;

            mVoice.add(value);
        }
    }

    public Set<Locale> get_languages() {
        return mSpeech.getAvailableLanguages();
    }

    public void speak(String s, callback c) {
        int i = (int)(Math.random() * mVoice.size());
        float pitch = 1.0f /* (float)(Math.random() * 0.2 + 0.9) */;
        float rate = 1.0f; /* (float)(Math.random() * 0.2 + 0.9); */

        mSpeech.setPitch(pitch);
        mSpeech.setSpeechRate(rate);

        System.out.println("speak");

        mSpeech.setVoice(mVoice.get(i));
        mSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, "first");
        mSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "silence");
        mSpeech.setSpeechRate(rate/2);
        mSpeech.speak(s, TextToSpeech.QUEUE_ADD, null, "second");
        mHash.put("second", c);
    }
}
