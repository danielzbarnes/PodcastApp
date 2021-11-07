package com.danielzbarnes.podcastapp.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.danielzbarnes.podcastapp.R
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.databinding.ItemPodcastBinding

class SharedPodcastListAdapter(var podcasts: List<Podcast>) : RecyclerView.Adapter<SharedPodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedPodcastViewHolder {

        var inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPodcastBinding>(
            inflater, R.layout.item_podcast, parent, false
        )
        return SharedPodcastViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: SharedPodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
    }

    override fun getItemCount(): Int = podcasts.size
}
