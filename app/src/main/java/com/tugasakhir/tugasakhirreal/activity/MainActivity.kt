package com.tugasakhir.tugasakhirreal.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tugasakhir.tugasakhirreal.Constants
import com.tugasakhir.tugasakhirreal.utils.DetectorHelper
import com.tugasakhir.tugasakhirreal.utils.ImageUtils
import com.tugasakhir.tugasakhirreal.R
import com.tugasakhir.tugasakhirreal.utils.PermissionUtils
import com.tugasakhir.tugasakhirreal.utils.SpinnerUtils
import com.tugasakhir.tugasakhirreal.activity.CameraActivity.Companion.CAMERAX_RESULT
import com.tugasakhir.tugasakhirreal.databinding.ActivityMainBinding
import com.tugasakhir.tugasakhirreal.ml.BoundingBox
import com.tugasakhir.tugasakhirreal.ml.Detector

class MainActivity : AppCompatActivity(), Detector.DetectorListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var currentImgUri: Uri? = null
    private lateinit var detector: Detector
    private lateinit var spinner: Spinner

    private var modelPath = "model640_grayscale_16.tflite"
    private var labelPath = "labels2.txt"

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImgUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("Permission", "Permission Granted")
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Log.d("Permission", "Permission Denied")
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        PermissionUtils.requestPermission(this, REQUIRED_PERMISSION, requestPermissionLauncher)

        spinner = binding.modelSpinner
        SpinnerUtils.setupSpinner(this, spinner, R.array.model_type, this)

        // Initialize the Detector
        detector = DetectorHelper.createDetector(this, modelPath, labelPath, this)
        detector.setup()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.realTimeButton.setOnClickListener { startRealTimeDetect() }
    }

    override fun onEmptyDetect() {
        Toast.makeText(this@MainActivity, "No objects detected.", Toast.LENGTH_SHORT).show()
        binding.previewImageView.setImageDrawable(null)
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        currentImgUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Draw bounding boxes on the original image
            val detectedBitmap = ImageUtils.drawBoundingBoxes(originalBitmap, boundingBoxes)

            // Display the modified bitmap in the ImageView
            binding.previewImageView.setImageBitmap(detectedBitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.clear()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.selectedItem
        if (selectedItem is String) {
            // Find the model in the Constants.models list that matches the selected item
            val selectedModel = Constants.models.find { it.modelPath == selectedItem }

            if (selectedModel != null) {
                modelPath = selectedModel.modelPath
                labelPath = selectedModel.labelPath

                // Clear the previous detector before reinitializing
                detector.clear()

                // Reinitialize the detector with the new model and labels
                detector = DetectorHelper.createDetector(this, modelPath, labelPath, this)
                detector.setup() // Load the new model
            } else {
                // Fallback in case the model is not found
                modelPath = Constants.models[0].modelPath
                labelPath = Constants.models[0].labelPath

                // Optionally reinitialize with the default model
                detector =DetectorHelper.createDetector(this, modelPath, labelPath, this)
                detector.setup()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        modelPath = Constants.models[0].modelPath
        labelPath = Constants.models[0].labelPath
    }

    private fun startGallery() {
        launchGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private fun startRealTimeDetect() {
        val intent = Intent(this, RealTimeCameraActivity::class.java)
        intent.putExtra(RealTimeCameraActivity.MODEL, modelPath)
        intent.putExtra(RealTimeCameraActivity.LABEL, labelPath)
        launcherIntentCamera.launch(intent)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImgUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImgUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Run the detector on the image
            detector.detect(bitmap)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
