package com.example.ui.screens.notes.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.io.File

class AudioRecorderHelper(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var currentOutputFile: File? = null
        private set
    var isRecording: Boolean = false
        private set

    fun startRecording(onSuccess: (File) -> Unit, onError: (Exception) -> Unit) {
        try {
            val audioDir = File(context.filesDir, "audio_notes").apply { mkdirs() }
            val outputFile = File(audioDir, "audio_${System.currentTimeMillis()}.m4a")
            currentOutputFile = outputFile

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            onSuccess(outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
            recorder?.release()
            recorder = null
            onError(e)
        }
    }

    fun stopRecording(): File? {
        return try {
            if (isRecording) {
                recorder?.stop()
                recorder?.release()
                recorder = null
                isRecording = false
                currentOutputFile
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            recorder?.release()
            recorder = null
            isRecording = false
            null
        }
    }

    fun cancelRecording() {
        try {
            if (isRecording) {
                recorder?.stop()
                recorder?.release()
                recorder = null
                isRecording = false
                currentOutputFile?.delete()
                currentOutputFile = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class AudioPlayerHelper(private val onCompletion: () -> Unit) {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
        private set
    var durationMs: Int = 0
        private set

    fun play(filePath: String, onProgress: (Int) -> Unit) {
        try {
            stop()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                this@AudioPlayerHelper.durationMs = duration
                start()
                this@AudioPlayerHelper.isPlaying = true
                setOnCompletionListener {
                    this@AudioPlayerHelper.isPlaying = false
                    onCompletion()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            this@AudioPlayerHelper.isPlaying = false
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPlaying = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            isPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
            mediaPlayer = null
            isPlaying = false
        }
    }
}
