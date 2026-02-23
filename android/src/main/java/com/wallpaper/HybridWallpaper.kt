package com.wallpaper

import com.margelo.nitro.core.Promise
import android.app.WallpaperManager
import com.margelo.nitro.wallpaper.HybridWallpaperSpec
import com.margelo.nitro.wallpaper.Location
import com.margelo.nitro.NitroModules
import androidx.core.net.toUri
import com.margelo.nitro.core.ArrayBuffer
import android.content.ComponentName
import android.content.Intent

class HybridWallpaper : HybridWallpaperSpec() {
    private val context get() = NitroModules.applicationContext ?: throw Error("Android content is null")

    override fun setWallpaper(url: String, location: Location): Promise<Unit> {
        return Promise.async {
            var stream: java.io.InputStream? = null
            try {
                val wm = WallpaperManager.getInstance(context)
                stream = when {
                    // Base64 data URI
                    url.startsWith("data:image") -> {
                        val base64Data = url.substringAfter("base64,")
                        val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                        java.io.ByteArrayInputStream(bytes)
                    }
                    // Content URI
                    url.startsWith("content://") -> {
                        context.contentResolver.openInputStream(url.toUri())
                            ?: throw Error("Cannot open content URI")
                    }
                    // Local file path
                    url.startsWith("file://") -> {
                        val file = java.io.File(url.removePrefix("file://"))
                        java.io.FileInputStream(file)
                    }
                    // Absolute path
                    url.startsWith("/") -> {
                        val file = java.io.File(url)
                        java.io.FileInputStream(file)
                    }
                    // Network URL
                    url.startsWith("http://") || url.startsWith("https://") -> {
                        java.net.URL(url).openStream()
                    }
                    else -> throw Error("Unsupported URI format")
                }
                val flag = when (location) {
                    Location.HOME -> WallpaperManager.FLAG_SYSTEM
                    Location.LOCK -> WallpaperManager.FLAG_LOCK
                    Location.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wm.setStream(stream, null, true, flag)
            } catch (e: java.io.IOException) {
                throw Error("Network or stream error: ${e.localizedMessage}")
            } catch (e: SecurityException) {
                throw Error("Permission denied: ${e.localizedMessage}")
            } catch (e: IllegalArgumentException) {
                throw Error(e.localizedMessage)
            } catch (e: Exception) {
                throw Error(e.localizedMessage)
            } finally {
                stream?.close()
            }
        }
    }

    override fun setByteWallpaper(bytes: ArrayBuffer, location: Location): Promise<Unit> {
        // Copy on JS thread if non-owning
        val copy = if (bytes.isOwner) bytes else ArrayBuffer.copy(bytes)
        
        return Promise.async {
            var stream: java.io.InputStream? = null
            try {
                val wm = WallpaperManager.getInstance(context)
                val byteArray = copy.toByteArray()
                stream = java.io.ByteArrayInputStream(byteArray)
                
                val flag = when (location) {
                    Location.HOME -> WallpaperManager.FLAG_SYSTEM
                    Location.LOCK -> WallpaperManager.FLAG_LOCK
                    Location.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wm.setStream(stream, null, true, flag)
            } catch (e: SecurityException) {
                throw Error("Permission denied: ${e.localizedMessage}")
            } catch (e: Exception) {
                throw Error("Failed to set wallpaper: ${e.localizedMessage}")
            } finally {
                stream?.close()
            }
        }
    }

    override fun setLiveWallpaper(videoPath: String): Promise<Unit> {
        return Promise.async {
            // Validate video file exists
            val videoFile = java.io.File(videoPath)
            if (!videoFile.exists()) {
                throw Error("Video file does not exist: $videoPath")
            }

            // Set video path for service
            LiveWallpaperService.videoPath = videoPath

            try {
                // Try ACTION_CHANGE_LIVE_WALLPAPER first
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, LiveWallpaperService::class.java)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {
                // Fallback to ACTION_LIVE_WALLPAPER_CHOOSER
                try {
                    val intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (fallbackException: Exception) {
                    throw Error("Failed to open wallpaper chooser: ${fallbackException.message}")
                }
            }
        }
    }

    override fun clearWallpaper(location: Location): Promise<Unit> {
        return Promise.async {
            try {
                val wm = WallpaperManager.getInstance(context)
                val flag = when (location) {
                    Location.HOME -> WallpaperManager.FLAG_SYSTEM
                    Location.LOCK -> WallpaperManager.FLAG_LOCK
                    Location.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wm.clear(flag)
            } catch (e: SecurityException) {
                throw Error("Permission denied: ${e.localizedMessage}")
            } catch (e: Exception) {
                throw Error("Failed to clear wallpaper: ${e.localizedMessage}")
            }
        }
    }

    override fun isSupported(): Boolean {
        return WallpaperManager.getInstance(context).isWallpaperSupported
    }

    override fun isSetAllowed(): Boolean {
        return WallpaperManager.getInstance(context).isSetWallpaperAllowed
    }
}
