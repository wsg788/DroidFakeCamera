package com.example.droidfakecamera.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidfakecamera.databinding.FragmentSourcePickerBinding

class SourcePickerFragment : Fragment() {

    private var _binding: FragmentSourcePickerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConfigViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    private val pickVideo = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onVideoSelected(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourcePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectImageButton.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        binding.selectVideoButton.setOnClickListener {
            pickVideo.launch(arrayOf("video/*"))
        }

        binding.saveScopeButton.setOnClickListener {
            val scopes = binding.scopeInput.text?.toString()?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()
            viewModel.updateScopes(scopes)
        }

        viewModel.config.observe(viewLifecycleOwner) { config ->
            config.imageUri?.let { binding.imagePreview.setImageURI(Uri.parse(it)) }
            if (config.videoUri != null) {
                binding.videoPreview.setVideoURI(Uri.parse(config.videoUri))
                binding.videoPreview.setOnPreparedListener(MediaPlayer::start)
            }
            binding.scopeInput.setText(config.targetScopes.joinToString(","))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.videoPreview.stopPlayback()
        _binding = null
    }

    private fun onImageSelected(uri: Uri) {
        binding.imagePreview.setImageURI(uri)
        viewModel.updateImage(uri.toString())
    }

    private fun onVideoSelected(uri: Uri) {
        binding.videoPreview.setVideoURI(uri)
        binding.videoPreview.setOnPreparedListener(MediaPlayer::start)
        viewModel.updateVideo(uri.toString())
    }
}
