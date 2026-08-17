package com.example.lihascalculator.ui.util

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

object FeedbackHelper {

    @Volatile
    private var vibrator: Vibrator? = null

    @Volatile
    private var audioManager: AudioManager? = null

    private fun getVibrator(context: Context): Vibrator? {
        if (vibrator == null) {
            synchronized(this) {
                if (vibrator == null) {
                    vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager = context.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                        vibratorManager?.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    }
                }
            }
        }
        return vibrator
    }

    private fun getAudioManager(context: Context): AudioManager? {
        if (audioManager == null) {
            synchronized(this) {
                if (audioManager == null) {
                    audioManager = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                }
            }
        }
        return audioManager
    }

    fun performHaptics(view: View?, context: Context, enabled: Boolean) {
        if (!enabled) return

        try {
            if (view != null && view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)) {
                return
            }

            val vib = getVibrator(context)
            if (vib != null && vib.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(10)
                }
            }
        } catch (e: Exception) {
            // Silently handle any vibration issues
        }
    }

    fun performSound(context: Context, enabled: Boolean) {
        if (!enabled) return

        try {
            getAudioManager(context)?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.4f)
        } catch (e: Exception) {
            // Silently handle audio issues
        }
    }
}

