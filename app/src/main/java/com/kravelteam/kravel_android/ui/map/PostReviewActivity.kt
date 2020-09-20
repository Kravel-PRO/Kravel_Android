package com.kravelteam.kravel_android.ui.map

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.mypage.AllPhotoReviewActivity
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_post_review.*
import kotlinx.android.synthetic.main.activity_post_review.lottie_detail_loading
import kotlinx.android.synthetic.main.activity_post_review.root
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class PostReviewActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private var selectedPicUri : Uri? = null
    private var selectImg: Boolean = false
    private var placeId: Int = 0
    private var part: String = ""
    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_review)

        lottie = lottie_detail_loading

        placeId = intent.getIntExtra("placeId",0)
        if(!intent.getStringExtra("part").isNullOrEmpty()) {
            part = intent.getStringExtra("part")!!
        }

        img_post_review_back.setOnDebounceClickListener { finish() }

        setImg()
        enableBtn()
        postPhotoReview()
    }

    private fun onLoading(){
        lottie.apply {
            setAnimation("heart_loading.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        lottie_detail_loading.setVisible()
    }

    private fun offLoading(){
        root.setVisible()
        lottie_detail_loading.setGone()
    }

    private fun setImg(){
        img_post_review_photo.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_CODE_SELECT_REVIEW_IMAGE)
        }
    }

    private fun enableBtn(){
        if(selectImg){
            btn_post_review_upload.setTextColor(Color.WHITE)
            btn_post_review_upload.isEnabled = true
        } else {
            btn_post_review_upload.setTextColor(resources.getColor(R.color.colorDarkGrey))
            btn_post_review_upload.isEnabled = false
        }
    }

    private fun postPhotoReview(){
        btn_post_review_upload.setOnDebounceClickListener {
            onLoading()
            //이미지 파일 내보내기
            val options = BitmapFactory.Options()
            val inputStream: InputStream = contentResolver.openInputStream(selectedPicUri!!)!!
            val bitmap = BitmapFactory.decodeStream(inputStream,null,options)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
            val photoBody = RequestBody.create("image/jpg".toMediaTypeOrNull(),byteArrayOutputStream.toByteArray())
            val picture = MultipartBody.Part.createFormData("file", File(selectedPicUri.toString()).name+".jpg",photoBody)

            if (newToken(authManager,networkManager)) {
            networkManager.requestPostPhotoReview(placeId,picture).safeEnqueue(
                onSuccess = {
                    Intent(KravelApplication.GlobalApp, AllPhotoReviewActivity::class.java).apply {
                        putExtra("review", "default")
                        putExtra("part",part)
                        putExtra("id", placeId)
                        putExtra("sort","createdDate,desc")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run { KravelApplication.GlobalApp.startActivity(this) }
                    finish()
                },
                onFailure = {
                    when {
                        it.code() == 400 -> {
                            toast(resources.getString(R.string.errorImg))
                        }
                        it.code() == 403 -> {
                            toast(resources.getString(R.string.errorReLogin))
                        }
                        else -> {
                            toast(resources.getString(R.string.errorClient))
                        }
                    }
                },
                onError = {
                    networkErrorToast()
                }
            )
            } else {
                toast(resources.getString(R.string.errorNetwork))
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SELECT_REVIEW_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                data?.let{
                    selectedPicUri = it.data!!
                    img_post_review_photo.setVisible()
                    img_post_review_icon.setGone()
                    txt_post_review_explain.setGone()
                    GlideApp.with(this).load(selectedPicUri).thumbnail(0.1f).into(img_post_review_photo)
                    img_post_review_photo.scaleType = ImageView.ScaleType.CENTER_CROP
                    selectImg = true
                    enableBtn()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        offLoading()
    }

    companion object {
        private const val REQUEST_CODE_SELECT_REVIEW_IMAGE = 800
    }

}