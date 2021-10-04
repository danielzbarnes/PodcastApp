package com.danielzbarnes.podcastapp.repository

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import java.lang.IllegalStateException

private const val TAG = "PodcastDownloadRepo"

class DownloadRepository private constructor(context: Context){

    private var downloadCache: Cache
    private var databaseProvider = ExoDatabaseProvider(context)
    private var downloadDirectory = File(context.getExternalFilesDir(null), "download_directory")

    init {
        Log.d(TAG, "init{ downloadCache }")
        downloadCache = SimpleCache(downloadDirectory, NoOpCacheEvictor(), databaseProvider)
    }

    // the cache and provider are needed to setup the download manager in the download service
    fun getCache(): Cache = downloadCache
    fun getProvider(): ExoDatabaseProvider = databaseProvider

    fun getDownloads(): List<String> = downloadCache.keys.toList() // this is a list of urls

    companion object{

        private var INSTANCE: DownloadRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null) { INSTANCE = DownloadRepository(context) }
        }

        fun get(): DownloadRepository{
            return INSTANCE ?: throw IllegalStateException("DownloadRepository must be initialized.")
        }
    }


}