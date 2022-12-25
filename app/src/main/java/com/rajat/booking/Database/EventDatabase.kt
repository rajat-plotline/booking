package com.rajat.booking.Database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [EventEntity::class], version = 2)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventDao(): EventDao

}