package com.example.mazeescapeboard.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundAndHapticFeedback(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playTileRotation(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            triggerLightHaptic()
        }
        if (soundEnabled) {
            scope.launch {
                // Crisp acoustic click with resonant overtone
                playTone(freq = 680f, durationMs = 38, decayRate = 35f)
            }
        }
    }

    fun playTokenMove(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            triggerLightHaptic()
        }
        if (soundEnabled) {
            scope.launch {
                // Warm wooden stone slide
                playTone(freq = 340f, durationMs = 60, decayRate = 20f)
            }
        }
    }

    fun playBlockedTap(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            triggerDoubleTickHaptic()
        }
        if (soundEnabled) {
            scope.launch {
                // Double muffled knock
                playTone(freq = 200f, durationMs = 35, decayRate = 45f)
                kotlinx.coroutines.delay(45)
                playTone(freq = 170f, durationMs = 35, decayRate = 45f)
            }
        }
    }

    fun playEscapeComplete(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            triggerSuccessHaptic()
        }
        if (soundEnabled) {
            scope.launch {
                // Architectural triumphant chime sequence (A4 -> C#5 -> E5 -> A5)
                val notes = listOf(440f, 554.37f, 659.25f, 880f)
                for (freq in notes) {
                    playTone(freq = freq, durationMs = 140, decayRate = 8f)
                    kotlinx.coroutines.delay(80)
                }
            }
        }
    }

    private fun triggerLightHaptic() {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(20)
                }
            }
        }
    }

    private fun triggerDoubleTickHaptic() {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 15, 40, 15)
                    val amplitudes = intArrayOf(0, 120, 0, 120)
                    v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 15, 40, 15), -1)
                }
            }
        }
    }

    private fun triggerSuccessHaptic() {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 30, 40, 45, 50, 80)
                    val amplitudes = intArrayOf(0, 100, 0, 180, 0, 255)
                    v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 30, 40, 45, 50, 80), -1)
                }
            }
        }
    }

    private fun playTone(freq: Float, durationMs: Int, decayRate: Float) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(1)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val envelope = exp(-decayRate * t)
            // Fundamental tone + subtle overtone for warm timbre
            val sample = (sin(2 * PI * freq * t) * 0.75 + sin(4 * PI * freq * t) * 0.25) * envelope
            generatedSnd[i] = (sample * 32767).toInt().coerceIn(-32768, 32767).toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, numSamples)
            audioTrack.play()
            // Release after playing
            scope.launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack allocation fallback
        }
    }
}
