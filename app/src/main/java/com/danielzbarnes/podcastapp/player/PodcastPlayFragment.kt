package com.danielzbarnes.podcastapp.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.databinding.FragmentPodcastPlayBinding
import com.danielzbarnes.podcastapp.details.PodcastDetailViewModel
import com.danielzbarnes.podcastapp.network.NetworkUtils
import com.danielzbarnes.podcastapp.services.PodcastPlayService
import com.google.android.exoplayer2.ui.PlayerControlView
import java.io.Serializable

private const val TAG = "PodcastPlayFragment"

class PodcastPlayFragment : Fragment() {

    private lateinit var binding: FragmentPodcastPlayBinding
    private lateinit var podcast: Podcast
    private lateinit var playerControlView: PlayerControlView
    private lateinit var podcastPlayService: PodcastPlayService

    private val podcastDetailViewModel: PodcastDetailViewModel by activityViewModels()

    private var connectionStatus: Boolean = NetworkUtils.NetworkStatus.isConnected

    private fun getPodcastRef(): String =
        PodcastUtils.getScriptureText(podcast.reference, podcast.series)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PodcastPlayService.PodcastPlayServiceBinder) {

                if (service.getCurrentPodcastID() != podcast.id) {

                    podcastPlayService = service.getService()
                    podcastPlayService.updatePlayer(podcast)
                }

                // this connects the player to the controller
                playerControlView.player = service.getExoPlayer()

            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            unBindService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        podcast = Podcast()
        val podcastId = PodcastPlayFragmentArgs.fromBundle(requireArguments()).podcastId
        podcastDetailViewModel.loadPodcast(podcastId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPodcastPlayBinding.inflate(layoutInflater, container, false)
        playerControlView = binding.playerControllerView

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        podcastDetailViewModel.podcastLiveData.observe(viewLifecycleOwner) { podcast ->
            podcast?.let {
                this.podcast = podcast

                binding.podcast = this.podcast

                if (connectionStatus || podcastDetailViewModel.isPodcastCached(this.podcast.url))
                    initService()
            }
        }
    }

    private fun initService() {

        val intent = Intent(requireActivity(), PodcastPlayService::class.java)

        intent.putExtra("podcast_obj", podcast as Serializable)

        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun unBindService() {
        requireActivity().unbindService(connection)
    }

}