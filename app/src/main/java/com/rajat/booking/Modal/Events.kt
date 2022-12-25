package com.rajat.booking.Modal

data class Events(

    val event_id: String? = "",
    val event_name: String? = "",
    val event_type: String? = "",
    val price: String? = "",
    val date : String? = "",
    val time: String? = "",
    val poster: String? = "",
    val created_on: String? = "",
    val row: String? = "",
    val col: String? = "",
    val seats : ArrayList<Boolean> = ArrayList()
)