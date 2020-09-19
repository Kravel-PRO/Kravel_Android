package com.kravelteam.kravel_android.ui.mypage

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.widget.TextView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.ReviewLikeBody
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.AllPhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_all_photo_review.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import org.koin.android.ext.android.inject

class AllPhotoReviewActivity : AppCompatActivity() {

    private lateinit var allPhotoReviewAdapter: AllPhotoReviewRecyclerview
    private val networkManager : NetworkManager by inject()

    private var checkReview = ""
    private var checkPart = ""
    private var id : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_photo_review)

        checkReview = intent.getStringExtra("review")!!
        initRecycler()

        if(checkReview == "my") { //내 포토리뷰

            initGetMyPhotoReview()
            txt_my_photo_review_title.text = resources.getString(R.string.myPhotoReview)

        } else if(checkReview == "default"){ //포토리뷰

            checkPart = intent.getStringExtra("part")!!
            id = intent.getIntExtra("id",0)

            txt_my_photo_review_title.text = resources.getString(R.string.homeNewPhotoReview2)

            when (checkPart) {
                "celeb" -> { //셀럽 리뷰 불러오기

                    initGetCelebPhotoReview()

                }
                "media" -> { // 미디어 리뷰 불러오기

                    initGetMediaPhotoReview()

                }
                "place" -> { //장소 리뷰 불러오기

                    initGetPlacePhotoReview()

                }
            }

        }

        initSetting()
    }

    private fun initSetting(){
        img_my_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        allPhotoReviewAdapter = AllPhotoReviewRecyclerview(
            checkReview,
            onLike = { like,reviewId ->
                networkManager.postLikes(id,reviewId, ReviewLikeBody(like)).safeEnqueue(
                    onSuccess = {
                    },
                    onFailure = {
                    },
                    onError = {
                        networkErrorToast()
                    }
                )
            },
            onDelete = { i: Int, delete: () -> Unit ->
                val dialog = AlertDialog.Builder(this).create()
                val view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val txtTitle = view.findViewById<TextView>(R.id.txt_logout_title)
                val txtDelete = view.findViewById<TextView>(R.id.btn_logout)
                txtTitle?.text = resources.getString(R.string.deletePhotoReview)
                txtDelete?.text = resources.getString(R.string.deleteTxt)

                view.btn_logout_cancel.setOnDebounceClickListener {
                    dialog.cancel()
                }

                view.btn_logout.setOnDebounceClickListener {
                    networkManager.requestDeletePhotoReview(i).safeEnqueue(
                        onSuccess = {
                            delete()
                            dialog.dismiss()
                            toast("삭제되었습니다")
                        },
                        onFailure = {

                        },
                        onError = {
                            networkErrorToast()
                        }
                    )
                }

                dialog.apply {
                    setView(view)
                    setCancelable(false)
                    show()
                }
            },
            onAllDelete = {
                emptyMyPhoto()
            }
        )
        rv_my_photo_review.apply {
            adapter = allPhotoReviewAdapter
        }
    }

    private fun emptyMyPhoto(){
        img_my_photo_review_empty_icon.setVisible()
        textView2.setVisible()
    }

    private fun initGetMyPhotoReview(){
        networkManager.requestMyPhotoReviews(0,100,"createdDate,desc").safeEnqueue(
            onSuccess = {
                if(it.data.result.content.isNullOrEmpty()){
                    emptyMyPhoto()
                } else {
                    img_my_photo_review_empty_icon.setGone()
                    textView2.setGone()
                    allPhotoReviewAdapter.initData(it.data.result.content)
                }
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
    }

    private fun initGetCelebPhotoReview(){
        networkManager.getCelebPhotoReview(id,0,60,"reviewLikes-count,desc").safeEnqueue(
            onSuccess = {
                allPhotoReviewAdapter.initData(it.data.result.content)
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
    }

    private fun initGetMediaPhotoReview(){
        networkManager.requestMediaPhotoReview(id,0,60,"reviewLikes-count,desc").safeEnqueue(
            onSuccess = {
                allPhotoReviewAdapter.initData(it.data.result.content)
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
    }

    private fun initGetPlacePhotoReview(){
        networkManager.getPlaceReview(id,0,60,"reviewLikes-count,desc").safeEnqueue(
            onSuccess = {
                allPhotoReviewAdapter.initData(it.data.result.content)
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
    }
}