/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.widget.Toast
import java.util.Locale

class TTS(context: Context?, written: String, ipa: String, locale: Locale?, voiceName: String?) {

    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(context, "Init failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Init failed")
                return@TextToSpeech
            }
            Log.d(TAG, "Init succeeded")
            if (locale != null) {
                val result = tts.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Set language failed $locale")
                    return@TextToSpeech
                }
            }
            if (voiceName != null) {
                val voice = getVoice(voiceName)
                if (voice != null) {
                    if (locale != null && voice.locale.country == locale.country) {
                        Log.d(TAG, "Set voice $voiceName")
                        val result = tts.setVoice(voice)
                        if (result != TextToSpeech.SUCCESS) {
                            Toast.makeText(context, "Voice $voiceName failed", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Error voice $voiceName failed")
                            return@TextToSpeech
                        }
                    }
                } else {
                    Toast.makeText(context, "Voice $voiceName not found", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error voice $voiceName not found")
                    return@TextToSpeech
                }
            }
            val phoneme = String.format("<phoneme alphabet='IPA' ph='%s'>%s</phoneme>", ipa, written)
            val text = String.format("<speak xml:lang='%s'>%s.</speak>", locale, phoneme)
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

                override fun onStart(s: String) {
                    Log.d(TAG, "start $s")
                }

                override fun onDone(s: String) {
                    Log.d(TAG, "done $s")
                    tts.shutdown()
                }

                @Deprecated("Deprecated in Java", ReplaceWith("onError(utteranceId: String, errorCode: Int)"))
                override fun onError(utteranceId: String) {
                    Log.e(TAG, "error $utteranceId")
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    Log.e(TAG, "error $errorCode $utteranceId")
                }
            })
            Log.d(TAG, "pronounce $written $ipa \"$text\"")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, written + '_' + ipa)
        }
    }

    private fun getVoice(voiceName: String): Voice? {
        val voices = tts.voices
        if (voices == null) {
            Log.w(TAG, "getVoices returned null")
            return null
        }
        for (voice in voices) {
            if (voice.name == voiceName) {
                return voice
            }
        }
        Log.w(TAG, "Could not find voice $voiceName in voice list")
        return null
    }

    companion object {

        const val TAG = "TTS"
        private val DEFAULT_LOCALE: Locale = Locale.UK
        private fun toLocale(locale: String?): Locale {
            return if (locale == null) {
                DEFAULT_LOCALE
            } else when (locale) {
                "GB" -> Locale.UK
                "US" -> Locale.US
                "CA" -> Locale.CANADA
                "AU" -> Locale.Builder().setLanguage("en").setRegion("AU").build()
                "IE" -> Locale.Builder().setLanguage("en").setRegion("IE").build()
                "NZ" -> Locale.Builder().setLanguage("en").setRegion("NZ").build()
                "ZA" -> Locale.Builder().setLanguage("en").setRegion("ZA").build()
                "SG" -> Locale.Builder().setLanguage("en").setRegion("SG").build()
                "NG" -> Locale.Builder().setLanguage("en").setRegion("NG").build()
                "IN" -> Locale.Builder().setLanguage("en").setRegion("IN").build()
                else -> DEFAULT_LOCALE
            }
        }

        fun pronounce(context: Context?, word: String, ipa: String, locale: String?, voiceName: String?) {
            TTS(context, word, ipa, toLocale(locale), voiceName)
        }
    }
}
