package com.example.droidfakecamera.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidfakecamera.R
import com.example.droidfakecamera.databinding.FragmentModuleManagerBinding
import com.example.droidfakecamera.root.RootHelper
import com.example.droidfakecamera.virtual.VirtualCameraManager

class ModuleManagerFragment : Fragment() {

    private var _binding: FragmentModuleManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConfigViewModel by activityViewModels()
    private val moduleInstaller = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onModuleSelected(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = VirtualCameraManager(requireContext())

        binding.checkRootButton.setOnClickListener {
            val hasRoot = RootHelper.hasRootAccess()
            updateStatus(if (hasRoot) getString(R.string.module_ready) else getString(R.string.root_missing))
        }

        binding.checkMagiskButton.setOnClickListener {
            val hasMagisk = RootHelper.hasMagisk()
            updateStatus(if (hasMagisk) getString(R.string.module_ready) else getString(R.string.magisk_missing))
        }

        binding.installModuleButton.setOnClickListener {
            moduleInstaller.launch(arrayOf("application/zip"))
        }

        binding.virtualCamSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEnabled(isChecked)
            val toggled = manager.toggleVirtualCamera(isChecked)
            if (!toggled) {
                Toast.makeText(requireContext(), "Failed to toggle virtual camera", Toast.LENGTH_SHORT).show()
            }
        }

        binding.writeConfigButton.setOnClickListener {
            val config = viewModel.config.value ?: return@setOnClickListener
            val success = manager.writeConfig(config)
            val message = if (success) getString(R.string.config_written) else "Failed to write config"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        binding.broadcastConfigButton.setOnClickListener {
            viewModel.config.value?.let(manager::broadcastConfig)
        }

        viewModel.config.observe(viewLifecycleOwner) { config ->
            binding.virtualCamSwitch.isChecked = config.enabled
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onModuleSelected(uri: Uri) {
        val result = RootHelper.installModuleZip(requireContext(), uri)
        val message = if (result) getString(R.string.module_ready) else "Failed to install module"
        updateStatus(message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus(text: String) {
        binding.statusText.text = text
    }
}
