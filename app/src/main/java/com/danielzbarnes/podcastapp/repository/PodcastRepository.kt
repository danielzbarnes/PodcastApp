package com.danielzbarnes.podcastapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.database.PodcastDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException


private const val TAG = "PodcastRepository"
private const val PODCAST_DATABASE = "podcast-database"
private const val MAX_RECENT_PODCASTS = 35

class PodcastRepository private constructor(context: Context){

    private val database: PodcastDatabase = Room.databaseBuilder(context.applicationContext,
        PodcastDatabase::class.java, PODCAST_DATABASE).build()

    private val podcastDao = database.podcastDao()

    fun getPodcasts(): LiveData<List<Podcast>> = podcastDao.getPodcasts()
    fun getRecent(): LiveData<List<Podcast>> = podcastDao.getRecentPodcasts(MAX_RECENT_PODCASTS)
    fun getPodcast(id: String): LiveData<Podcast?> = podcastDao.getPodcast(id)

    fun getSeriesList(): List<String> = podcastDao.getSeriesList()
    fun getAuthorsList(): List<String> = podcastDao.getAuthorList()

    fun getBySeries(series: String): LiveData<List<Podcast>> = podcastDao.getBySeries(series)
    fun getByAuthor(pastor: String): LiveData<List<Podcast>> = podcastDao.getByAuthor(pastor)

    fun getPodcastByUrl(url: String): Podcast = podcastDao.getPodcastByUrl(url)

    fun queryDb(query: String): LiveData<List<Podcast>> = podcastDao.queryDb(query)

    private suspend fun addPodcasts(Podcasts: List<Podcast>){
        withContext(Dispatchers.IO){
            podcastDao.addPodcasts(Podcasts)
        }
    }

    // this is intended to be called in swipe to refresh
    fun updatePodcasts(){
        Log.d(TAG, "updatePodcasts(), swipe refresh network call")
    }

    companion object{
        private var INSTANCE: PodcastRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null) {
                INSTANCE = PodcastRepository(context)
            }
        }

        fun get(): PodcastRepository{

            return INSTANCE ?: throw IllegalStateException("PodcastRepository must be initialized.")
        }
    }

}