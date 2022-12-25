package com.rajat.booking.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {

    @Insert
    fun insertEvent(eventEntity: EventEntity)

    @Delete
    fun deleteEvent(eventEntity: EventEntity)

    @Query("Delete FROM events")
    fun deleteAllEvent()

    @Query("SELECT * FROM events")
    fun getAllEvent(): List<EventEntity>

    @Query("SELECT * FROM events WHERE event_id = :eventId")
    fun getEventById(eventId: String): EventEntity
}