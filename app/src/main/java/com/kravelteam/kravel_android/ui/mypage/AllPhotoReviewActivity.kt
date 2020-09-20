package com.kravelteam.kravel_android.ui.mypage

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.ReviewLikeBody
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.AllPhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_all_photo_review.*
import kotlinx.android.synthetic.main.activity_all_photo_review.lottie_detail_loading
import kotlinx.android.synthetic.main.dialog_logout.view.*
import org.koin.android.ext.android.inject

class AllPhotoReviewActivity : AppCompatActivity() {

    private lateinit var allPhotoReviewAdapter: AllPhotoReviewRecyclerview
    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private var checkReview = ""
    private var checkPart = ""
    private var id : Int = -1
    private var position: Int = 0
    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_photo_review)

        lottie = lottie_detail_loading

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
        root.fadeInWithVisible(500)
        lottie_detail_loading.setGone()
    }

    private fun initSetting(){
        img_my_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        allPhotoReviewAdapter = AllPhotoReviewRecyclerview(
            checkReview,
            onLike = { like: Boolean, reviewId: Int, likeSuccess: ()-> Unit ->
                if (newToken(authManager,networkManager)) {
                    networkManager.postLikes(id,reviewId, ReviewLikeBody(like)).safeEnqueue(
                        onSuccess = {
                            likeSuccess()
                        },
                        onFailure = {
                            if(it.code() == 403) {
                                toast(resources.getString(R.string.errorReLogin))
                            } else {
                                toast(resources.getString(R.string.errorClient))
                            }
                        },
                        onError = {
                            networkErrorToast()
                        }
                    )
                } else {
                    toast(resources.getString(R.string.errorNetwork))
                }
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
                    if (newToken(authManager,networkManager)) {
                        networkManager.requestDeletePhotoReview(i).safeEnqueue(
                            onSuccess = {
                                delete()
                                dialog.dismiss()
                                toast(resources.getString(R.string.deletePhoto))
                            },
                            onFailure = {
                                if(it.code() == 403) {
                                    toast(resources.getString(R.string.errorReLogin))
                                } else {
                                    toast(resources.getString(R.string.errorClient))
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
        onLoading()
        if (newToken(authManager,networkManager)) {
            networkManager.requestMyPhotoReviews(0,100,"createdDate,desc").safeEnqueue(
                onSuccess = {
                    if(it.data.result.content.isNullOrEmpty()){
                        emptyMyPhoto()
                    } else {
                        img_my_photo_review_empty_icon.setGone()
                        textView2.setGone()
                        rv_my_photo_review.setVisible()
                        allPhotoReviewAdapter.initData(it.data.result.content)
                    }
                    offLoading()
                },
                onFailure = {
                    if(it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
        }
    }

    private fun initGetCelebPhotoReview(){
        onLoading()
        if (newToken(authManager,networkManager)) {
            networkManager.getCelebPhotoReview(id,0,60,"reviewLikes-count,desc").safeEnqueue(
                onSuccess = {
                    val data = it.data.result.content
                    allPhotoReviewAdapter.initData(data)
                    if(intent.getIntExtra("position",0) != 0) {
                        data.forEachIndexed{ index, data ->
                            if(data.reviewId == intent.getIntExtra("position",0))
                                position = index
                        }
                        rv_my_photo_review.setVisible()
                        rv_my_photo_review.scrollToPosition(position)
                    }
                    offLoading()
                },
                onFailure = {
                    if(it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }

    private fun initGetMediaPhotoReview(){
        onLoading()
        if (newToken(authManager,networkManager)) {
            networkManager.requestMediaPhotoReview(id,0,60,"reviewLikes-count,desc").safeEnqueue(
                onSuccess = {
                    val data = it.data.result.content
                    allPhotoReviewAdapter.initData(data)
                    if(intent.getIntExtra("position",0) != 0) {
                        data.forEachIndexed{ index, data ->
                            if(data.reviewId == intent.getIntExtra("position",0))
                                position = index
                        }
                        rv_my_photo_review.setVisible()
                        rv_my_photo_review.scrollToPosition(position)
                    }
                    offLoading()
                },
                onFailure = {
                    if(it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }

    private fun initGetPlacePhotoReview(){
        onLoading()
        if (newToken(authManager,networkManager)) {
            var sort = "reviewLikes-count,desc"
            intent.getStringExtra("sort")?.let {
                sort = it
            }
            networkManager.getPlaceReview(id,0,60,sort).safeEnqueue(
                onSuccess = {
                    val data = it.data.result.content
                    allPhotoReviewAdapter.initData(data)
                    if(intent.getIntExtra("position",0) != 0) {
                        data.forEachIndexed{ index, data ->
                            if(data.reviewId == intent.getIntExtra("position",0))
                                position = index
                        }
                        rv_my_photo_review.setVisible()
                        rv_my_photo_review.scrollToPosition(position)
                    }
                    offLoading()
                },
                onFailure = {
                    if(it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }
}