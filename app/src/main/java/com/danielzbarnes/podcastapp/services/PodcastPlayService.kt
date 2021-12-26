package com.danielzbarnes.podcastapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.danielzbarnes.podcastapp.R
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.ui.player.PodcastPlayFragment
import com.danielzbarnes.podcastapp.repository.DownloadRepository
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

private const val TAG = "PodcastPlayService"

class PodcastPlayService: Service() {

    private val downloadRepository = DownloadRepository.get()
    private val podcastPlayIBinder: IBinder = PodcastPlayServiceBinder()
    private val channelId = "BBC Channel"
    private val notificationId: Int = 200200

    private var exoPlayer: SimpleExoPlayer? = null

    private var playbackPos: Long = 0
    private var playWhenReady = true

    private lateinit var context: Context
    private lateinit var podcast: Podcast
    private lateinit var podcastId: String
    private lateinit var title: String
    private lateinit var reference: String
    private lateinit var url: String
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    override fun onBind(intent: Intent?): IBinder? {

        val extras: Bundle = intent!!.extras!!

        podcast = extras.get("podcast_obj") as Podcast

        setPodcastDetails(podcast)

        initializePlayer()

        return podcastPlayIBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        stopSelf()
        return super.onUnbind(intent)
    }

    private fun initializePlayer(){

        Log.d(TAG, "initializePlayer(), id from bundle: $podcastId")

        if (exoPlayer == null)
            exoPlayer = SimpleExoPlayer.Builder(context)
                .setSeekBackIncrementMs(15000)
                .setSeekForwardIncrementMs(15000)
                .build()

        if (isPodcastCached(url))  // play from the cache if the podcast is in the cached
            exoPlayer!!.setMediaSource(mediaSourceFromCache(url))
        else // else stream the podcast
            exoPlayer!!.setMediaItem(MediaItem.fromUri(url))

        exoPlayer!!.apply {
            playWhenReady = true
            seekTo(playbackPos)
            prepare()
        }

        initPlayerNotificationManager()

        playerNotificationManager.setPlayer(exoPlayer)
    }

    private fun initPlayerNotificationManager() {

        playerNotificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setMediaDescriptionAdapter( object : PlayerNotificationManager.MediaDescriptionAdapter{

                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return title
                }

                @SuppressLint("UnspecifiedImmutableFlag")
                // this is to suppress the Pending.Intent.FLAG_UPDATE_CURRENT Lint warning
                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(context, PodcastPlayFragment::class.java)
                    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player): CharSequence {
                    return reference
                }

                override fun getCurrentSubText(player: Player): CharSequence { return ""}

                override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                    return null // artwork for notification
                }
            }).setNotificationListener(object : PlayerNotificationManager.NotificationListener {

                override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
                    if (ongoing)
                        startForeground(notificationId, notification)
                    else
                        stopForeground(false)
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    stopSelf()
                    stopForeground(true)
                }
            }).setChannelNameResourceId(R.string.channel_name).setChannelDescriptionResourceId(R.string.channel_desc)
            .build()

        playerNotificationManager.apply {
            setPriority(NotificationCompat.PRIORITY_HIGH)

            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            setUseChronometer(true)
            setUseFastForwardActionInCompactView(true)
            setUseRewindActionInCompactView(true)
            setUsePreviousAction(false)
        }
    }


    private fun setPodcastDetails(podcast: Podcast){
        podcastId = podcast.id
        title = podcast.title
        reference = podcast.reference
        url = podcast.url
    }

    private fun isPodcastCached(url: String): Boolean {

        if ( downloadRepository.getDownloads().contains(url) )
            return true
        return false
    }

    // returns the MediaSource object from the download cache
    private fun mediaSourceFromCache(url: String): MediaSource {

        val downloadIndex = downloadRepository.getDownloads().indexOf(url)

        val cachedDataSourceFactory = CacheDataSource.Factory().setCache(downloadRepository.getCache())
            .setCacheWriteDataSinkFactory(null)

        return ProgressiveMediaSource.Factory(cachedDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(downloadRepository.getDownloads()[downloadIndex]))
    }

    private fun initDetails(podcastBundle: Bundle){
        podcastId = podcastBundle.getString("podcast_id")!!
        url = podcastBundle.getString("podcast_url")!!
        title = podcastBundle.getString("podcast_title")!!
        reference = podcastBundle.getString("podcast_ref")!!
    }

    private fun tempPodcastObj(podcast: Podcast){
        podcastId = podcast.id
        title = podcast.title
        reference = podcast.reference
        url = podcast.url
    }

    fun updatePlayer(podcast: Podcast){
        tempPodcastObj(podcast)
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer!!.apply {
            setMediaItem(mediaItem)
            playWhenReady = true
        }
    }

    private fun releasePlayer(){

        playerNotificationManager.setPlayer(null)

        if (exoPlayer != null){
            playWhenReady = exoPlayer!!.playWhenReady
            playbackPos = exoPlayer!!.currentPosition
            exoPlayer!!.release()
            exoPlayer = null
        }
    }

    inner class PodcastPlayServiceBinder: Binder(){
        fun getExoPlayer() = exoPlayer
        fun getService() = this@PodcastPlayService
        fun getCurrentPodcastID() = podcastId
    }
}