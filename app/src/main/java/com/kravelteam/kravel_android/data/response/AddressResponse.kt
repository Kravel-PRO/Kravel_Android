package com.kravelteam.kravel_android.data.response


data class AddressResponse(
    val documents: List<AddressData>
)

data class AddressData(
    val place_name: String,
    val address_name: String,
    val road_address_name: String
)