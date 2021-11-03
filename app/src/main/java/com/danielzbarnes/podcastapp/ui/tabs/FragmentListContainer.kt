package com.danielzbarnes.podcastapp.ui.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.danielzbarnes.podcastapp.MainActivity
import com.danielzbarnes.podcastapp.R
import com.danielzbarnes.podcastapp.network.ConnectionReceiver
import com.danielzbarnes.podcastapp.network.NetworkUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val TAB_NUM = 4
private const val TAG = "PodcastListContainer"

class FragmentListContainer: Fragment(), ConnectionReceiver.ConnectionListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: PodcastPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var noConnectionLayout: View

    private var connectionStatus: Boolean = NetworkUtils.NetworkStatus.isConnected
    private val tabNames = arrayListOf("Recent", "By Scripture", "By Series", "By Pastor")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (requireActivity() as MainActivity).toggleSearchBar(false)

        val view = inflater.inflate(R.layout.fragment_list_container, container, false)
        viewPager = view.findViewById(R.id.viewpager)
        tabLayout = view.findViewById(R.id.tab_layout)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        noConnectionLayout = view.findViewById(R.id.no_connection_layout)

        pageAdapter = PodcastPagerAdapter(requireActivity())
        viewPager.adapter = pageAdapter
        viewPager.isUserInputEnabled = false

        swipeRefresh.setOnRefreshListener {
            // lambda for onRefresh()

            connectionStatus = NetworkUtils.NetworkStatus.isConnected

            if (connectionStatus) {

                Log.d(TAG, "Connected: do database update call")

            }
            else {

                Log.d(TAG, "Not Connected: display no_connection UI")
            }

            toggleUi()

            swipeRefresh.isRefreshing = false
        }

        TabLayoutMediator(tabLayout, viewPager) {
                tab, position -> tab.text = tabNames[position] }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position, false)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        (requireActivity() as MainActivity).setAppbarTitle("List Container")

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleUi(){

        Log.d(TAG, "toggleUI(): isConnected: $connectionStatus")

        if (connectionStatus){
            viewPager.visibility = View.VISIBLE
            noConnectionLayout.visibility = View.GONE
        } else {
            viewPager.visibility = View.GONE
            noConnectionLayout.visibility = View.VISIBLE
        }
    }

    /***************
     * PagerAdapter
     * *************/
    private inner class PodcastPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa){

        override fun createFragment(position: Int): Fragment {
            return when (position){
                0 -> PodcastListFragment.newInstance("recent") // tab 1
                1 -> PodcastTopListFragment.newInstance("scripture")
                2 -> PodcastTopListFragment.newInstance("series")
                3 -> PodcastTopListFragment.newInstance("pastors")
                else -> PodcastListFragment.newInstance("recent")  // default to recent
            }
        }
        override fun getItemCount(): Int = TAB_NUM
    }
}