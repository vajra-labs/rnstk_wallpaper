package com.wallpaper

import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.util.Log
import java.io.File

/**
 * Live Wallpaper Service for video/animated wallpapers.
 * Supports MP4 video files and handles lifecycle, visibility, and surface changes.
 */
class LiveWallpaperService : WallpaperService() {

    companion object {
        private const val TAG = "LiveWallpaper"
        var videoPath: String? = null
    }

    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                startPlayback()
            } else {
                stopPlayback()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startPlayback()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            stopPlayback()
        }

        override fun onDestroy() {
            super.onDestroy()
            stopPlayback()
        }

        private fun startPlayback() {
            stopPlayback()

            val path = videoPath
            if (path.isNullOrEmpty()) {
                Log.w(TAG, "Video path is null or empty")
                return
            }

            val videoFile = File(path)
            if (!videoFile.exists()) {
                Log.e(TAG, "Video file does not exist: $path")
                return
            }

            try {
                mediaPlayer = MediaPlayer().apply {
                    setSurface(surfaceHolder.surface)
                    setDataSource(path)
                    isLooping = true
                    setVolume(0f, 0f)
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting playback: ${e.message}", e)
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }

        private fun stopPlayback() {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        }
    }
}