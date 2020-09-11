package com.kravelteam.kravel_android.ui.home

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.data.request.ReviewLikeBody
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.data.response.PhotoReviewResponse
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NewPhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PopularRecyclerview
import com.kravelteam.kravel_android.ui.map.PlaceDetailActivity
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.setGone
import kotlinx.android.synthetic.main.activity_photo_review.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class PhotoReviewActivity : AppCompatActivity() {
    private val networkManager : NetworkManager by inject()
    private val photoAdapter : NewPhotoReviewRecyclerview by lazy { NewPhotoReviewRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_review)


        rv_photo_review.apply {
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }
        initPhotoReivew()
        img_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }
    private fun initPhotoReivew() {
        networkManager.getPhotoReview(0,7,"createdDate,desc").safeEnqueue (
            onSuccess = {

                rv_photo_review.apply {
                    adapter = photoAdapter
                    addItemDecoration(VerticalItemDecorator(4))
                    addItemDecoration(HorizontalItemDecorator(4))
                }
                photoAdapter.initData(it.data.result.content)

            },
            onFailure = {
                Timber.e("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
        photoAdapter.setOnItemClickListener(object : NewPhotoReviewRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PhotoReviewData, pos: Int) {
                val imgLike = v!!.findViewById<ImageView>(R.id.img_rv_photo_like)
                if(data.like) {
                  networkManager.postLikes(data.place.placeId,data.reviewId,ReviewLikeBody(false)).safeEnqueue(
                      onSuccess = {
                          GlideApp.with(v).load(R.drawable.btn_like_unclick).into(imgLike)
                          data.like = false
                      },
                      onFailure = {
                          Timber.e("실패")
                      },
                      onError = {
                          networkErrorToast()
                      })
                } else {
                    networkManager.postLikes(data.place.placeId,data.reviewId,ReviewLikeBody(true)).safeEnqueue(
                        onSuccess = {
                            GlideApp.with(v).load(R.drawable.btn_like).into(imgLike)
                            data.like = true
                        },
                        onFailure = {
                            Timber.e("실패")
                        },
                        onError = {
                            networkErrorToast()
                        })

                }
            }


        })


    }
}