package com.kravelteam.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.ScrapResponse
import com.kravelteam.kravel_android.ui.adapter.ScrapRecyclerview
import kotlinx.android.synthetic.main.activity_scrap.*

class ScrapActivity : AppCompatActivity() {

    private val scrapAdapter: ScrapRecyclerview by lazy { ScrapRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrap)

        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        img_scrap_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        rv_scrap.apply {
            adapter = scrapAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        scrapAdapter.initData(
            listOf(
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "dd"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "aaaaaaaaaaaa"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "eeeeeeeeeeee"
                ),
                ScrapResponse(
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "사이코"
                )
            )
        )
    }
}