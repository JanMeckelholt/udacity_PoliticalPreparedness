package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import org.koin.android.ext.android.inject
import timber.log.Timber

class ElectionsFragment: Fragment() {

    private val viewModel : ElectionsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        Timber.i("viewmodel $viewModel")
        viewModel.elections.observe(viewLifecycleOwner, Observer {
            Timber.i("elections changed: $it")
        })
        // TODO: Add ViewModel values and create ViewModel

        // TODO: Add binding values

        // TODO: Link elections to voter info

        // TODO: Initiate recycler adapters

        // TODO: Populate recycler adapters
        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
}