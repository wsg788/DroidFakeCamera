package com.example.droidfakecamera.virtual

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.droidfakecamera.data.VirtualCameraConfig
import com.example.droidfakecamera.root.RootHelper

class VirtualCameraManager(private val context: Context) {

    fun writeConfig(config: VirtualCameraConfig): Boolean {
        return RootHelper.writeConfigToModule(config.toConfigFile())
    }

    fun broadcastConfig(config: VirtualCameraConfig) {
        val intent = Intent(ACTION_VCAM_CONFIG).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_ENABLED, config.enabled)
            putExtra(EXTRA_IMAGE_URI, config.imageUri)
            putExtra(EXTRA_VIDEO_URI, config.videoUri)
            putStringArrayListExtra(EXTRA_SCOPES, ArrayList(config.targetScopes))
        }
        try {
            context.sendBroadcast(intent)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to send broadcast", t)
        }
    }

    fun toggleVirtualCamera(enable: Boolean): Boolean {
        val command = if (enable) "setprop persist.droid.fakecamera 1" else "setprop persist.droid.fakecamera 0"
        return RootHelper.executeSuCommand("su", "-c", command)
    }

    companion object {
        const val ACTION_VCAM_CONFIG = "com.example.droidfakecamera.VCAM_CONFIG"
        const val EXTRA_ENABLED = "extra_enabled"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_VIDEO_URI = "extra_video_uri"
        const val EXTRA_SCOPES = "extra_scopes"
        private const val TAG = "VirtualCameraManager"
    }
}
