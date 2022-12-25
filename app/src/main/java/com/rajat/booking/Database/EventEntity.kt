package com.rajat.booking.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val event_id: String,
    @ColumnInfo val event_name: String,
    @ColumnInfo val event_type: String,
    @ColumnInfo val price: String,
    @ColumnInfo val date: String,
    @ColumnInfo val time: String,
    @ColumnInfo val seat_id: String, //ArrayList<Int>
    @ColumnInfo val seat_no: String, //ArrayList<String>
    @ColumnInfo val poster: String,
    @ColumnInfo val total_price: String,
    @ColumnInfo val total_seats: String,
)