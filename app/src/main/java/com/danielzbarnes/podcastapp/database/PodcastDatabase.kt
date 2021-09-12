package com.danielzbarnes.podcastapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Podcast::class], version = 1, exportSchema = false)
abstract class PodcastDatabase: RoomDatabase() { }