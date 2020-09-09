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
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.home.PhotoReviewActivity
import com.kravelteam.kravel_android.ui.mypage.MyPhotoReviewActivity
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_post_review.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class PostReviewActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()

    private var selectedPicUri : Uri? = null
    private var selectImg: Boolean = false
    private var placeId: Int = 0
    private var part: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_review)


        placeId = intent.getIntExtra("placeId",0)
        if(!intent.getStringExtra("part").isNullOrEmpty()) {
            part = intent.getStringExtra("part")!!
        }

        img_post_review_back.setOnDebounceClickListener { finish() }

        setImg()
        enableBtn()
        postPhotoReview()
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
            //이미지 파일 내보내기
            val options = BitmapFactory.Options()
            val inputStream: InputStream = contentResolver.openInputStream(selectedPicUri!!)!!
            val bitmap = BitmapFactory.decodeStream(inputStream,null,options)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream)
            val photoBody = RequestBody.create("image/jpg".toMediaTypeOrNull(),byteArrayOutputStream.toByteArray())
            val picture = MultipartBody.Part.createFormData("file", File(selectedPicUri.toString()).name,photoBody)

            networkManager.requestPostPhotoReview(placeId,picture).safeEnqueue(
                onSuccess = {
                    toast("성공")
                    Intent(KravelApplication.GlobalApp, MyPhotoReviewActivity::class.java).apply {
                        putExtra("review", "default")
                        putExtra("part",part)
                        putExtra("id", placeId)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run { KravelApplication.GlobalApp.startActivity(this) }
                    finish()
                },
                onFailure = {
                    toast("이미지 업로드에 실패했습니다. 다시 시도 부탁드립니다.")
                },
                onError = {
                    toast("이미지 업로드에 실패했습니다. 다시 시도 부탁드립니다.")
                }
            )
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

    companion object {
        private const val REQUEST_CODE_SELECT_REVIEW_IMAGE = 800
    }

}