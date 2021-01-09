package es.kix2902.cd2spotify.ui.qr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import es.kix2902.cd2spotify.databinding.ActivityQrBinding
import es.kix2902.cd2spotify.ui.error.ErrorActivity
import es.kix2902.cd2spotify.ui.spotify.SpotifyActivity
import java.util.concurrent.Executors

class QrActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1234
    }

    private lateinit var binding: ActivityQrBinding

    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    private var previewUseCase: Preview? = null

    private var analysisUseCase: ImageAnalysis? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val barcodeOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        .build()
    private val barcodeScanner = BarcodeScanning.getClient(barcodeOptions)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCamera()
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_CAMERA_REQUEST
                )
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                val intent = Intent(this, ErrorActivity::class.java)
                intent.putExtra(ErrorActivity.EXTRA_ERROR, ErrorActivity.ERROR_PERMISSION)
                startActivity(intent)
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun bindCameraUseCases() {
        if (cameraProvider == null) {
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra(ErrorActivity.EXTRA_ERROR, ErrorActivity.ERROR_INITIALIZATION)
            startActivity(intent)
            finish()
        }

        val rotation = binding.cameraPreview.display.rotation
        val aspectRatio = AspectRatio.RATIO_4_3

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .build()

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, this@QrActivity::processImageProxy)
            }


        cameraProvider!!.unbindAll()

        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector, previewUseCase, analysisUseCase)
            previewUseCase?.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        } catch (e: Exception) {
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra(ErrorActivity.EXTRA_ERROR, ErrorActivity.ERROR_INITIALIZATION)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()
                if ((barcode != null) && (barcode.rawValue != null)) {
                    binding.cameraPreview.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    cameraExecutor.shutdown()

                    val intent = Intent(this, SpotifyActivity::class.java)
                    intent.putExtra(SpotifyActivity.EXTRA_BARCODE, barcode.rawValue!!)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                val intent = Intent(this, ErrorActivity::class.java)
                intent.putExtra(ErrorActivity.EXTRA_ERROR, ErrorActivity.ERROR_READING)
                startActivity(intent)
                finish()
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }
}