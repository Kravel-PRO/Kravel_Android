package com.kravelteam.kravel_android.ui.mypage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_report.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class ReportActivity : AppCompatActivity() {

    private var selectedPicUri : Uri? = null
    private var checkTitle = false
    private var checkAddress = false
    private var checkExplain = false
    private var checkTag = false
    private var checkImg = false

    private val networkManager : NetworkManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        initChangeEditText()
        initUploadImg()
        initReport()

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
                } else {
                    edt_report_place_name.setBackgroundResource(R.drawable.signup_edit_style)
                    checkTitle = false
                }
                enableCompleteBtn()
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
                } else {
                    edt_report_place_explain.setBackgroundResource(R.drawable.signup_edit_style)
                    checkExplain = false
                }
                enableCompleteBtn()
            }
        )
        edt_report_place_tag.onTextChangeListener(
            onTextChanged = {
                if(!edt_report_place_tag.text.isNullOrBlank()) {
                    edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style_true)
                    checkTag = true
                } else {
                    edt_report_place_tag.setBackgroundResource(R.drawable.signup_edit_style)
                    checkTag = false
                }
                enableCompleteBtn()
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
                    edt_report_place_area.setBackgroundResource(R.drawable.signup_edit_style_true)
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

    private fun initReport(){
        btn_report_complete.setOnDebounceClickListener {
            //이미지 파일 내보내기
            val options = BitmapFactory.Options()
            val inputStream: InputStream = contentResolver.openInputStream(selectedPicUri!!)!!
            val bitmap = BitmapFactory.decodeStream(inputStream,null,options)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream)
            val photoBody = RequestBody.create("image/jpg".toMediaTypeOrNull(),byteArrayOutputStream.toByteArray())
            val picture = MultipartBody.Part.createFormData("files", File(selectedPicUri.toString()).name,photoBody)
            val pictureList = ArrayList<MultipartBody.Part?>()
            pictureList.add(picture)

            val title = RequestBody.create("text/plain".toMediaTypeOrNull(), edt_report_place_name.text.toString())
            val contents = RequestBody.create("text/plain".toMediaTypeOrNull(), edt_report_place_explain.text.toString())
            val address = RequestBody.create("text/plain".toMediaTypeOrNull(), edt_report_place_area.text.toString())
            val tags = RequestBody.create("text/plain".toMediaTypeOrNull(), edt_report_place_tag.text.toString())
            val inquireCategory = RequestBody.create("text/plain".toMediaTypeOrNull(), "PLACE_REPORT")

            networkManager.requestReport(pictureList, title, contents, address, tags, inquireCategory).safeEnqueue(
                onSuccess = {
                    toast("제보가 완료되었습니다! 감사합니다!")
                    finish()
                },
                onFailure = {
                    toast("네트워크 에러 발생")
                },
                onError = {
                    toast("네트워크 에러 발생")
                }
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1000
        private const val REQUEST_CODE_SELECT_ADDRESS = 1500
    }
}