package com.danielzbarnes.podcastapp.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

private const val TAG = "PodcastConnReceiver"

class ConnectionReceiver(): BroadcastReceiver() {

    interface ConnectionListener{
        fun onNetworkConnectionChanged(isConnected: Boolean){
            Log.d(TAG, "ConnectionReceiver.onNetworkConnectionChanged()")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (connectionListener != null) {

            val notConnected = intent!!.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            Log.d(TAG, "onReceive(), status: ${!notConnected}")
            NetworkUtils.NetworkStatus.isConnected = !notConnected // this might not be needed O.o?
            connectionListener!!.onNetworkConnectionChanged(!notConnected) // need to invert to make it isConnected not isNotConnected
        }
    }

    companion object{
        var connectionListener: ConnectionListener? = null
    }
}