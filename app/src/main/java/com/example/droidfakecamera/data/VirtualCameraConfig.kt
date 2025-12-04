package com.example.droidfakecamera.data

data class VirtualCameraConfig(
    val imageUri: String? = null,
    val videoUri: String? = null,
    val targetScopes: List<String> = emptyList(),
    val enabled: Boolean = false
) {
    fun toConfigFile(): String {
        val builder = StringBuilder()
        builder.appendLine("enabled=${enabled}")
        imageUri?.let { builder.appendLine("image=${it}") }
        videoUri?.let { builder.appendLine("video=${it}") }
        if (targetScopes.isNotEmpty()) {
            builder.appendLine("scopes=${targetScopes.joinToString(",")}")
        }
        return builder.toString()
    }
}
