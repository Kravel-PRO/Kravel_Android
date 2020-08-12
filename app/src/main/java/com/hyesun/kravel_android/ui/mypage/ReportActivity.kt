package com.hyesun.kravel_android.ui.mypage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.common.setOnDebounceClickListener
import com.hyesun.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private var selectedPicUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        initChangeEditText()
        initUploadImg()
    }

    private fun initChangeEditText(){
        edt_report_place_name.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_name.text.isNullOrBlank()) edt_report_place_name.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_report_place_name.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )

        edt_report_place_area.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_area.text.isNullOrBlank()) edt_report_place_area.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_report_place_area.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )

        edt_report_place_tag.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_tag.text.isNullOrBlank()) edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )
    }

    private fun initUploadImg(){
        cl_report_img_upload.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SELECT_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                data?.let{
                    selectedPicUri = it.data!!
                    img_report_upload_photo.setVisible()
                    GlideApp.with(this).load(selectedPicUri).thumbnail(0.1f).into(img_report_upload_photo)
                    img_report_upload_photo.setRound(12.dpToPx().toFloat())
                    img_report_upload_photo.scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
    }
}