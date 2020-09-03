package com.kravelteam.kravel_android.ui.map

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.google.common.util.concurrent.ListenableFuture
import com.kravelteam.kravel_android.R
import kotlinx.android.synthetic.main.activity_camera.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(){

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
        btn_camera_capture.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val values = ContentValues()

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() +
                File.separator +
                "Camera"
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }

        val f = File(file, "test_capture.jpg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            with(values) {
                put(MediaStore.Images.Media.TITLE, "Kravel")
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        } else {
            with(values) {
                put(MediaStore.Images.Media.TITLE, "Kravel")
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.BUCKET_ID, "Kravel")
                put(MediaStore.Images.Media.DATA, f.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            applicationContext.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Timber.e("Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(f)

                    // We can only change the foreground Drawable using API level 23+ API
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Update the gallery thumbnail with latest picture taken
                        //setGalleryThumbnail(savedUri)
                    }

                    // Implicit broadcasts will be ignored for devices running API level >= 24
                    // so if you only target API level 24+ you can remove this statement
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        this@CameraActivity.sendBroadcast(
                            Intent(Camera.ACTION_NEW_PICTURE, savedUri)
                        )
                    }

                    // If the folder selected is an external media directory, this is
                    // unnecessary but otherwise other apps will not be able to access our
                    // images unless we scan them using [MediaScannerConnection]
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri?.toFile()?.extension)
                    Timber.e(savedUri?.toFile()?.absolutePath)
                    MediaScannerConnection.scanFile(
                        applicationContext,
                        arrayOf(savedUri?.toFile()?.absolutePath),
                        arrayOf(mimeType)
                    ) { _, uri ->
                        Timber.e( "Image capture scanned into media store: $uri")
                    }
                }
            })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview_camera.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {}

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

}