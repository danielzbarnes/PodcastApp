package com.danielzbarnes.podcastapp.ui.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.danielzbarnes.podcastapp.databinding.FragmentNoConnectionBinding

class NoConnectionFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentNoConnectionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}