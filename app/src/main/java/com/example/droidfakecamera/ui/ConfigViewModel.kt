package com.example.droidfakecamera.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.droidfakecamera.data.VirtualCameraConfig

class ConfigViewModel : ViewModel() {
    private val _config = MutableLiveData(VirtualCameraConfig())
    val config: LiveData<VirtualCameraConfig> = _config

    fun updateImage(uri: String?) {
        _config.value = _config.value?.copy(imageUri = uri)
    }

    fun updateVideo(uri: String?) {
        _config.value = _config.value?.copy(videoUri = uri)
    }

    fun updateScopes(scopes: List<String>) {
        _config.value = _config.value?.copy(targetScopes = scopes)
    }

    fun setEnabled(enabled: Boolean) {
        _config.value = _config.value?.copy(enabled = enabled)
    }
}
