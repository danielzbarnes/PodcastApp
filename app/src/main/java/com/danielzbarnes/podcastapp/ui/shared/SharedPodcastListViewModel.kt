package com.danielzbarnes.podcastapp.ui.shared

import androidx.lifecycle.ViewModel
import com.danielzbarnes.podcastapp.repository.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "PodcastListViewModel"

class SharedPodcastListViewModel: ViewModel() {

    private val podcastRepository = PodcastRepository.get()

    val podcastListLiveData = podcastRepository.getPodcasts()
    val podcastRecentLiveData = podcastRepository.getRecent()

    fun getBySeries(series: String) = podcastRepository.getBySeries(series)
    fun getByAuthor(author: String) = podcastRepository.getByAuthor(author)
    fun queryDb(query: String) = podcastRepository.queryDb(query)

    // call this one from swipe refresh
    fun updatePodcasts() = podcastRepository.updatePodcasts()

    suspend fun getSeriesList(): List<String> =
        withContext(Dispatchers.IO){
            return@withContext podcastRepository.getSeriesList()
        }

    suspend fun getAuthorsList(): List<String> =
        withContext(Dispatchers.IO){
            return@withContext podcastRepository.getAuthorsList()
        }
}