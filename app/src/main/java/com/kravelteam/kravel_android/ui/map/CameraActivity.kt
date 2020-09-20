package com.kravelteam.kravel_android.ui.map

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
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
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_camera.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity(){

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private var shoot: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        Timber.e(intent.getStringExtra("placeName"))
        //필터
        intent.getStringExtra("filter")?.let {
            GlideApp.with(applicationContext).load(it).into(img_camera_concept_ill)
            txt_camera_unselect_concept.setVisible()
            txt_camera_unselect_concept.text = intent.getStringExtra("placeName")
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //카메라 퍼미션
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        //갤러리 퍼미션
        if (!allStoragePermissionsGranted()){
            ActivityCompat.requestPermissions(
                this, REQUIRED_STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE_PERMISSIONS)
        }

        btn_camera_capture.setOnClickListener {
            //갤러리 퍼미션
            if (!allStoragePermissionsGranted()){
                ActivityCompat.requestPermissions(
                    this, REQUIRED_STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE_PERMISSIONS)
            } else {
                takePhoto()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        galleryPreview()
        initSelectConcept()

        img_camera_cancel.setOnDebounceClickListener {
            finish()
        }

        showEx()
    }

    private fun showEx(){
        intent.getStringExtra("subImg")?.let {
            GlideApp.with(this).load(it).into(img_camera_concept_example)
            img_camera_concept_example.setRound(10.dpToPx().toFloat())
        }
        img_camera_concept_example.setOnDebounceClickListener {
            Intent(KravelApplication.GlobalApp,ExampleActivity::class.java).apply {
                intent.getStringExtra("subImg")?.let {
                    putExtra("imgEx",it)
                }
            }.run { startActivity(this) }
        }
    }

    private fun galleryPreview(){
        img_gallery_icon.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type =MediaStore.Images.Media.CONTENT_TYPE
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivity(intent)
        }
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        //안드로이드 버전에 따라 저장 경로 지정
        val values = ContentValues()

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() +
                File.separator +
                "Camera"
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }

        val f = File(file, "kravel_${SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA).format(System.currentTimeMillis())}.jpg")

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

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            applicationContext.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()

        cl_camer_capture.setVisible()
        //카메라 셔터
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        if(volume!=0){
            if(shoot == null){
                shoot = MediaPlayer.create(applicationContext,Uri.parse("file:///system/media/audio/ui/camera_click.ogg"))
            }
            shoot?.start()
        }
        Handler().postDelayed({
            cl_camer_capture.setGone()
        },100)

        // 이미지 캡쳐
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    toast(resources.getString(R.string.errorSaveImg))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val savedUri = Uri.fromFile(f)

                    GlideApp.with(applicationContext).load(output.savedUri).into(img_gallery_take_picture)
                    img_gallery_take_picture.setRound(10.dpToPx().toFloat())

                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri?.toFile()?.extension)
                    MediaScannerConnection.scanFile(
                        applicationContext,
                        arrayOf(savedUri?.toFile()?.absolutePath),
                        arrayOf(mimeType)
                    ) { _, uri ->
                    }
                    toast(resources.getString(R.string.succesSaveImg))
                }
            })

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun allStoragePermissionsGranted() = REQUIRED_STORAGE_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview_camera.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {}

        }, ContextCompat.getMainExecutor(this))
    }

    private fun initSelectConcept(){
        txt_camera_unselect_basic.setOnDebounceClickListener {
            txt_camera_unselect_basic.setInvisible()
            txt_camera_unselect_concept.setVisible()
            txt_camera_select.text = "일반"
            img_camera_concept_ill.setGone()
            cl_camera_concept_example.setGone()
            txt_camera_concept_example.setGone()
        }
        txt_camera_unselect_concept.setOnDebounceClickListener {
            txt_camera_unselect_concept.setInvisible()
            txt_camera_unselect_basic.setVisible()
            txt_camera_select.text = intent.getStringExtra("placeName")
            img_camera_concept_ill.setVisible()
            cl_camera_concept_example.setVisible()
            txt_camera_concept_example.setVisible()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cl_no_access_camera.setGone()
                startCamera()
            } else {
                toast(resources.getString(R.string.allowCameraExplain))
                cl_no_access_camera.setVisible()
            }
        } else if (requestCode == REQUEST_CODE_STORAGE_PERMISSIONS) {
            if(!allStoragePermissionsGranted()){
                toast(resources.getString(R.string.allowGallery))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
//        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_STORAGE_PERMISSIONS = 11
        private val REQUIRED_STORAGE_PERMISSIONS = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

}