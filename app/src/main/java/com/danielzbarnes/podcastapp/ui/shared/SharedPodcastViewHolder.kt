package com.danielzbarnes.podcastapp.ui.shared

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.databinding.ItemPodcastBinding
import com.danielzbarnes.podcastapp.services.PodcastDownloadService
import com.google.android.exoplayer2.offline.DownloadService

private const val TAG = "PodcastViewHolder"

class SharedPodcastViewHolder(view: View, val binding: ItemPodcastBinding):
    RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener  {

    private lateinit var podcast: Podcast
    private lateinit var date: String

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun bind(podcast: Podcast){

        this.podcast = podcast
        date = this.podcast.date
        binding.podcast = podcast
        binding.viewHolder = this
    }

    fun formatDate() = date.slice(IntRange(0, date.length - 7)) // returns the date as "MM DD"
    fun formatYear() = date.slice(IntRange(8, date.length-1)) // returns the year as "YYYY"

    override fun onClick(view: View?) {

        val action: NavDirections = when(view!!.findNavController().currentDestination?.label){
            // add navigation directions for each destination
            //NavHome
            //SubListFrag
            //d/l Frag
            //etc
        }
        view.findNavController().navigate(action)
    }

    override fun onLongClick(view: View?): Boolean {

        // this should actually pop up an alert dialogue to confirm delete podcast, but for testing this is sufficient.
        if (view!!.findNavController().currentDestination?.label == "DownloadFrag")
            DownloadService.sendRemoveDownload(itemView.context, PodcastDownloadService::class.java, podcast.id, false)
        return true
    }
}