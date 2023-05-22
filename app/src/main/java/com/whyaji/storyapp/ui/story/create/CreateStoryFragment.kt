package com.whyaji.storyapp.ui.story.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.whyaji.storyapp.R
import com.whyaji.storyapp.util.Resource
import com.whyaji.storyapp.databinding.FragmentCreateStoryBinding
import com.whyaji.storyapp.util.ViewModelFactory
import com.whyaji.storyapp.util.reduceFileImage
import com.whyaji.storyapp.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryFragment : Fragment() {
    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var latLng: LatLng? = null

    private val createStoryViewModel: CreateStoryViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        requestCameraPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
            }
        })
        binding.cbAddLocation.setOnClickListener {
            val isChecked = binding.cbAddLocation.isChecked
            if (isChecked) {
                getMyLocation()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btOpenCamera.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_createStoryFragment_to_cameraFragment))
        binding.btOpenGallery.setOnClickListener{
            startGallery()
        }
        binding.buttonAdd.setOnClickListener{
            uploadData()
        }


        val fileUri = arguments?.getString("selected_image")
        if (fileUri != null) {
            val uri: Uri = Uri.parse(fileUri)
            getFile = uri.toFile()
            binding.ivImagePreview.setImageBitmap(BitmapFactory.decodeFile(uri.path))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.cbAddLocation.isChecked = true
            getMyLocation()
        } else {
            binding.cbAddLocation.isChecked = false
        }
    }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireActivity())
            getFile = myFile
            binding.ivImagePreview.setImageURI(selectedImg)
        }
    }

    private fun uploadData() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = binding.edAddDescription.text

            if (!descriptionText.isNullOrEmpty()) {
                val description = descriptionText.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                if (binding.cbAddLocation.isChecked) getMyLocation()

                createStoryViewModel.addNewStory(imageMultipart, description, latLng).observe(viewLifecycleOwner) {
                    if (it != null) {
                        when (it) {
                            is Resource.Success -> {
                                showLoading(false)
                                Toast.makeText(context, it.data.message, Toast.LENGTH_LONG).show()
                                findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
                            }
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Error -> {
                                showLoading(false)
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireActivity(), R.string.mandatory_desc, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), R.string.mandatory_image, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.pbCreateStory.isVisible = state
        binding.edAddDescription.isInvisible = state
        binding.ivImagePreview.isInvisible = state
        binding.btOpenCamera.isInvisible = state
        binding.btOpenGallery.isInvisible = state
        binding.buttonAdd.isInvisible = state
        binding.cbAddLocation.isInvisible = state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Camera Permission")
                    .setMessage("The camera permission is required to capture story photos.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CODE_CAMERA_PERMISSION
                        )
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        // Handle permission denial
                        findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
                    }
                    .show()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_CAMERA_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
    }
}