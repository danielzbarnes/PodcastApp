package com.danielzbarnes.podcastapp.services

import android.app.Notification
import android.content.Context
import android.util.Log
import com.danielzbarnes.podcastapp.R
import com.danielzbarnes.podcastapp.repository.DownloadRepository
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import java.lang.Exception


private const val SERVICE_NOTIFICATION_CHANNEL = "service_notification"
private const val NOTIFICATION_ID = 123456
private const val TAG = "PodcastDownloadService"

class PodcastDownloadService: DownloadService(321123, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    "Podcast Downloader", R.string.app_name, R.string.channel_desc) {

    private lateinit var notificationHelper: DownloadNotificationHelper
    private lateinit var downloadManager: DownloadManager
    private lateinit var context: Context

    private val downloadRepository = DownloadRepository.get()

    override fun onCreate() {
        super.onCreate()
        context = this
        notificationHelper = DownloadNotificationHelper(this, "Podcast Downloader")
    }

    override fun getDownloadManager(): DownloadManager {

        context = this

        val dataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setUserAgent(Util.getUserAgent(context, "PodcastApp"))
            setConnectTimeoutMs(30*1000)
            setReadTimeoutMs(30*1000)
            setAllowCrossProtocolRedirects(false)
        }
        downloadManager = DownloadManager(context, downloadRepository.getProvider(), downloadRepository.getCache(),
            dataSourceFactory, Runnable::run)

        notificationHelper = DownloadNotificationHelper(context, SERVICE_NOTIFICATION_CHANNEL)

        downloadManager.addListener(TerminalStateNotificationManager(context, notificationHelper, NOTIFICATION_ID+1))

        return downloadManager
    }

    override fun getScheduler(): Scheduler? { return null }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        return notificationHelper.buildProgressNotification(context, R.drawable.exo_icon_fastforward,
            null, "{podcast.title goes here}", downloads)
    }

    private class TerminalStateNotificationManager(context: Context, private val notificationHelper: DownloadNotificationHelper,
                                                   firstNotificationId: Int): DownloadManager.Listener {

        private val context: Context = context.applicationContext
        private var nextNotificationId = firstNotificationId

        override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
            super.onDownloadChanged(downloadManager, download, finalException)

            Log.d(TAG, "onDownloadChanged()")
            val notification: Notification = when(download.state)
            {
                Download.STATE_COMPLETED ->
                    notificationHelper.buildDownloadCompletedNotification(context, R.drawable.exo_icon_circular_play,
                        null, Util.fromUtf8Bytes(download.request.data))
                Download.STATE_FAILED ->
                    notificationHelper.buildDownloadFailedNotification(context, R.drawable.exo_icon_fullscreen_exit,
                        null, Util.fromUtf8Bytes(download.request.data))
                else -> return
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }
    }
}