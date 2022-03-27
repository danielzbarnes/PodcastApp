package com.danielzbarnes.podcastapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.danielzbarnes.podcastapp.database.Podcast
import com.danielzbarnes.podcastapp.repository.DownloadRepository
import com.danielzbarnes.podcastapp.repository.PodcastRepository

class PodcastDetailViewModel: ViewModel() {

    private val podcastRepository = PodcastRepository.get()
    private val downloadRepository = DownloadRepository.get()
    private val podcastIdLiveData = MutableLiveData<String>()

    var podcastLiveData: LiveData<Podcast?> = Transformations.switchMap(podcastIdLiveData){
            podcastId -> podcastRepository.getPodcast(podcastId)
    }

    fun loadPodcast(podcastId: String){
        podcastIdLiveData.value = podcastId
    }

    fun isPodcastCached(url: String): Boolean{
        return downloadRepository.getDownloads().contains(url)
    }
}