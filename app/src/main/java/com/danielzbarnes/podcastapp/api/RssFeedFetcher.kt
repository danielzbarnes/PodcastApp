package com.danielzbarnes.podcastapp.api

import com.danielzbarnes.podcastapp.database.Podcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "PodcastRssFetcher"
private const val RSS = "" // RSS feed url goes here
/*  Important Note: if the RSS url is NOT HTTPS(ie HTTP) then network-security-config needs to be defined
*                   and set in the AndroidManifest.xml to allow cleartextTraffic
*                   starting with Android 9(API 28) cleartext support is disabled by default  */

class RssFeedFetcher {

    private val rssFeedParser = RssFeedParser()

    suspend fun fetchRss(): List<Podcast> =
        withContext(Dispatchers.IO) {

            var podcastList: List<Podcast> = emptyList()
            try {
                val url = URL(RSS)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                connection.apply {
                    readTimeout = 10000 // in millis
                    connectTimeout = 15000
                    requestMethod = "GET"
                    connect()
                }

                val input: InputStream = connection.inputStream

                podcastList = rssFeedParser.parseRss(input) as ArrayList<Podcast>

                input.close()


            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext podcastList
    }
}
