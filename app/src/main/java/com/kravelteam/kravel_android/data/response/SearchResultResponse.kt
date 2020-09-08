package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class SearchResultResponse(
    val celebrities: List<CelebResponse>,
    val medias: List<MediaResponse>
) : Serializable