package com.kravelteam.kravel_android.ui.mypage

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
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private var selectedPicUri : Uri? = null
    private var checkTitle = false
    private var checkAddress = false
    private var checkExplain = false
    private var checkTag = false
    private var checkImg = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        initChangeEditText()
        initUploadImg()
        img_report_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initChangeEditText(){
        edt_report_place_name.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_name.text.isNullOrBlank()) {
                    edt_report_place_name.setBackgroundResource(R.drawable.signup_edit_style_true)
                    checkTitle = true
                    enableCompleteBtn()
                } else edt_report_place_name.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )

        edt_report_place_area.setOnDebounceClickListener {
            startActivityForResult(Intent(this,AddressActivity::class.java), REQUEST_CODE_SELECT_ADDRESS)
        }

        edt_report_place_explain.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_explain.text.isNullOrBlank()){
                    edt_report_place_explain.setBackgroundResource(R.drawable.signup_edit_style_true)
                    checkExplain = true
                    enableCompleteBtn()
                } else edt_report_place_explain.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )
        edt_report_place_tag.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_tag.text.isNullOrBlank()) {
                    edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style_true)
                    checkTag = true
                    enableCompleteBtn()
                } else edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style)
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
                    checkImg = true
                    enableCompleteBtn()
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_ADDRESS){
            if(resultCode == Activity.RESULT_OK){
                data?.let {
                    edt_report_place_area.text = it.getStringExtra("address")
                    edt_report_place_area.setTextColor(resources.getColor(R.color.colorBlack))
                    edt_report_place_area.setBackgroundResource(R.drawable.signup_edit_style)
                    checkAddress = true
                    enableCompleteBtn()
                }
            }
        }
    }

    private fun enableCompleteBtn(){
        if(checkTitle and checkAddress and checkExplain and checkTag and checkImg){
            btn_report_complete.isEnabled = true
            btn_report_complete.setTextColor(Color.WHITE)
        } else{
            btn_report_complete.isEnabled = false
            btn_report_complete.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
        private const val REQUEST_CODE_SELECT_ADDRESS = 1500
    }
}