package com.kravelteam.kravel_android.ui.home

import android.content.Intent
import android.location.LocationManager
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.*
import com.kravelteam.kravel_android.data.request.ReviewLikeBody
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.NewPhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.map.PlaceDetailActivity
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_photo_review.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.sql.Time

class PhotoReviewActivity : AppCompatActivity() {
    private val networkManager : NetworkManager by inject()
    private val photoAdapter : NewPhotoReviewRecyclerview by lazy { NewPhotoReviewRecyclerview() }
    private var position: Int = 0
    private val authManager : AuthManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_review)


        rv_photo_review.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(8))
            addItemDecoration(HorizontalItemDecorator(8))
        }
        initPhotoReivew()
        img_photo_review_back.setOnDebounceClickListener {
            finish()
        }

        sw_new_photo_review.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                initPhotoReivew()
                sw_new_photo_review.isRefreshing = false

            }

        })
    }
    private fun initPhotoReivew() {
        if(newToken(authManager,networkManager)) {
            networkManager.getPhotoReview(0, 20, "createdDate,desc").safeEnqueue(
                onSuccess = {

                    val data = it.data.result.content

                    photoAdapter.initData(data)
                    if (intent.getIntExtra("position", 0) != 0) {
                        data.forEachIndexed { index, data ->
                            if (data.reviewId == intent.getIntExtra("position", 0))
                                position = index
                        }
                        rv_photo_review.scrollToPosition(position)
                    }

                },
                onFailure = {
                    if (it.code() == 403) {
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
        photoAdapter.setOnItemClickListener(object : NewPhotoReviewRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PhotoReviewData, pos: Int) {
                val imgLike = v!!.findViewById<ImageView>(R.id.img_rv_photo_like)
                val txtHeart = v!!.findViewById<TextView>(R.id.txt_rv_photo_like)
                val img = v!!.findViewById<ImageView>(R.id.img_rv_photo)
                img.setOnDebounceClickListener {
                    Intent(KravelApplication.GlobalApp, PlaceDetailActivity::class.java).apply {
                        putExtra("placeId", data.place.placeId)
                        putExtra("mode", "home")
                    }.run {
                        KravelApplication.GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }

                imgLike.setOnDebounceClickListener {
                    if (data.like) {
                        if (newToken(authManager, networkManager)) {
                            networkManager.postLikes(
                                data.place.placeId,
                                data.reviewId,
                                ReviewLikeBody(false)
                            ).safeEnqueue(
                                onSuccess = {
                                    GlideApp.with(v).load(R.drawable.btn_like_unclick).into(imgLike)
                                    data.likeCount = data.likeCount - 1
                                    txtHeart.text = data.likeCount.toString()

                                    data.like = false
                                },
                                onFailure = {
                                    if (it.code() == 403) {
                                        toast(resources.getString(R.string.errorReLogin))
                                    } else {
                                        toast(resources.getString(R.string.errorClient))
                                    }
                                    Timber.e("data id :: " + data.reviewId)
                                    Timber.e("place id :: " + data.place.placeId)
                                    Timber.e("실패")
                                },
                                onError = {
                                    networkErrorToast()
                                })
                        } else {
                            toast(resources.getString(R.string.errorNetwork))
                        }
                    } else {
                        if (newToken(authManager, networkManager)) {
                            networkManager.postLikes(
                                data.place.placeId,
                                data.reviewId,
                                ReviewLikeBody(true)
                            ).safeEnqueue(
                                onSuccess = {
                                    GlideApp.with(v).load(R.drawable.btn_like).into(imgLike)
                                    data.likeCount = data.likeCount + 1
                                    txtHeart.text = data.likeCount.toString()
                                    data.like = true
                                },
                                onFailure = {
                                    if (it.code() == 403) {
                                        toast(resources.getString(R.string.errorReLogin))
                                    } else {
                                        toast(resources.getString(R.string.errorClient))
                                    }
                                    Timber.e("data id :: " + data.reviewId)
                                    Timber.e("place id :: " + data.place.placeId)
                                    Timber.e("실패")
                                },
                                onError = {
                                    networkErrorToast()
                                })
                        } else {
                            toast(resources.getString(R.string.errorNetwork))
                        }
                    }
                }
            }


        })
    }
}