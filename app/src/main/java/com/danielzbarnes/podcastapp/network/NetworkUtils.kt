package com.danielzbarnes.podcastapp.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

private const val TAG = "NetworkUtils"

// class to monitor network status using network callbacks
class NetworkUtils() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    fun getNetworkRequest():NetworkRequest = networkRequest

    fun getNetworkCallback():ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback(){

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                Log.d(TAG, "onAvailable()")
                NetworkStatus.isConnected = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)

                Log.d(TAG, "onLost()")
                NetworkStatus.isConnected = false
            }
        }

    object NetworkStatus{
        var isConnected: Boolean = false
    }
}