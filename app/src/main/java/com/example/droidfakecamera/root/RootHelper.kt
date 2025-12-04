package com.example.droidfakecamera.root

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object RootHelper {
    private const val TAG = "RootHelper"

    fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val code = process.waitFor()
            code == 0
        } catch (t: Throwable) {
            Log.e(TAG, "Root check failed", t)
            false
        }
    }

    fun hasMagisk(): Boolean {
        return File("/sbin/.magisk").exists() || File("/data/adb/magisk").exists()
    }

    fun executeSuCommand(vararg command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor() == 0
        } catch (t: Throwable) {
            Log.e(TAG, "Error executing su command", t)
            false
        }
    }

    fun installModuleZip(context: Context, moduleUri: Uri): Boolean {
        val cacheFile = File(context.cacheDir, "module.zip")
        context.contentResolver.openInputStream(moduleUri)?.use { input ->
            cacheFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val moduleTarget = "/data/adb/modules_update/droidfakecamera"
        val commands = arrayOf(
            "su", "-c", "mkdir -p $moduleTarget && cp ${cacheFile.absolutePath} $moduleTarget/module.zip"
        )
        return executeSuCommand(*commands)
    }

    fun writeConfigToModule(config: String): Boolean {
        val modulePath = "/data/adb/modules/droidfakecamera"
        val tempFile = File.createTempFile("config", ".txt")
        tempFile.writeText(config)
        val commands = arrayOf(
            "su", "-c", "mkdir -p $modulePath && cp ${tempFile.absolutePath} $modulePath/config.txt"
        )
        val success = executeSuCommand(*commands)
        tempFile.delete()
        return success
    }

    fun readCommandOutput(vararg command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLines().joinToString("\n")
            reader.close()
            process.waitFor()
            output
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to read command output", t)
            ""
        }
    }
}
