package com.example.mobile_intern_test.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items")
    val items: List<AddressItem>
)

data class AddressItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("address")
    val address: Address,
    @SerializedName("position")
    val position: Position,
    @SerializedName("mapView")
    val mapView: MapView
)

data class Address(
    @SerializedName("label")
    val label: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("countryName")
    val countryName: String,
    @SerializedName("state")
    val state: String?,
    @SerializedName("county")
    val county: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("street")
    val street: String?,
    @SerializedName("postalCode")
    val postalCode: String?
)

data class Position(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double
)

data class MapView(
    @SerializedName("west")
    val west: Double,
    @SerializedName("south")
    val south: Double,
    @SerializedName("east")
    val east: Double,
    @SerializedName("north")
    val north: Double
)
