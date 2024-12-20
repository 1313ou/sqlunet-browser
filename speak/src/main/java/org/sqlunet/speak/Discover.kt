/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.EngineInfo
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.Voice
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Locale
import java.util.function.Consumer

class Discover {

    private var tts: TextToSpeech? = null

    fun discoverVoices(context: Context, consumer: Consumer<List<Voice>>) {
        tts = TextToSpeech(context, OnInitListener { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "Init failed")
                return@OnInitListener
            }
            if (tts == null) {
                Log.e(TAG, "Null TTS")
                return@OnInitListener
            }

            val voices = tts!!.voices
                .asSequence()
                .filter { "en" == it.locale.language }
                .toList()
                .sortedWith { v1: Voice, v2: Voice -> v1.name.compareTo(v2.name) }

            consumer.accept(voices)
        })
    }

    fun discoverVoice(context: Context, consumer: Consumer<Voice>) {
        tts = TextToSpeech(context, OnInitListener { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "Init failed")
                return@OnInitListener
            }
            if (tts == null) {
                Log.e(TAG, "Null TTS")
                return@OnInitListener
            }
            val voice = tts!!.voice
            consumer.accept(voice)
        })
    }

    fun discoverEngines(context: Context, consumer: Consumer<List<EngineInfo>>) {
        tts = TextToSpeech(context, OnInitListener { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "Init failed")
                return@OnInitListener
            }
            if (tts == null) {
                Log.e(TAG, "Null TTS")
                return@OnInitListener
            }
            val engines = tts!!.engines
            consumer.accept(engines)
        })
    }

    fun discoverEngine(context: Context, consumer: Consumer<String>) {
        tts = TextToSpeech(context, OnInitListener { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "Init failed")
                return@OnInitListener
            }
            if (tts == null) {
                Log.e(TAG, "Null TTS")
                return@OnInitListener
            }
            val engine = tts!!.defaultEngine
            consumer.accept(engine)
        })
    }

    fun discoverLanguages(context: Context, consumer: Consumer<List<Locale>>) {
        tts = TextToSpeech(context, OnInitListener { status: Int ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "Init failed")
                return@OnInitListener
            }
            if (tts == null) {
                Log.e(TAG, "Null TTS")
                return@OnInitListener
            }
            val locales = tts!!.availableLanguages
                .asSequence()
                .filter { "en" == it.language }
                .toList()
                .sortedWith { l1: Locale, l2: Locale -> l1.country.compareTo(l2.country) }
            consumer.accept(locales)
        })
    }

    companion object {

        const val TAG = "Voice"
    }
}
