package com.danielzbarnes.podcastapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PodcastDao {

    @Query("SELECT * FROM podcast")
    fun getPodcasts(): LiveData<List<Podcast>>

    @Query("SELECT * FROM podcast WHERE id=(:id)")
    fun getPodcast(id: String): LiveData<Podcast?>

    @Query("SELECT * FROM podcast LIMIT :limit")
    fun getRecentPodcasts(limit: Int): LiveData<List<Podcast>>

    @Query("SELECT * FROM podcast WHERE series=(:series)")
    fun getBySeries(series: String): LiveData<List<Podcast>>

    @Query("SELECT * FROM podcast WHERE author=(:author)")
    fun getByAuthor(author: String): LiveData<List<Podcast>>

    @Query("SELECT DISTINCT series FROM podcast")
    fun getSeriesList(): List<String>

    @Query("SELECT DISTINCT author FROM podcast")
    fun getAuthorList(): List<String>

    @Query("SELECT * FROM podcast WHERE url=(:url)")
    fun getPodcastByUrl(url: String): Podcast

    @Query("SELECT * FROM podcast WHERE title LIKE :query")
    fun queryDb(query: String): LiveData<List<Podcast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPodcasts(podcasts: List<Podcast>)
}