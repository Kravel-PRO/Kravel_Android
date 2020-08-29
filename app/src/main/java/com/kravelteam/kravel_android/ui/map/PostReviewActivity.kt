package com.kravelteam.kravel_android.ui.map

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.util.setVisible
import kotlinx.android.synthetic.main.activity_post_review.*

class PostReviewActivity : AppCompatActivity() {

    private var selectedPicUri : Uri? = null
    private var selectImg: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_review)

        setImg()
        enableBtn()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SELECT_REVIEW_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                data?.let{
                    selectedPicUri = it.data!!
                    img_post_review_photo.setVisible()
                    GlideApp.with(this).load(selectedPicUri).thumbnail(0.1f).into(img_post_review_photo)
                    img_post_review_photo.scaleType = ImageView.ScaleType.CENTER_CROP
                    selectImg = true
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_REVIEW_IMAGE = 800
    }

}