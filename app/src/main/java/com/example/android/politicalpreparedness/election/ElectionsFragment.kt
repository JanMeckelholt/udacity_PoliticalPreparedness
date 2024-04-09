package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.google.android.material.snackbar.Snackbar
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
        binding.viewModel = viewModel
        binding.rvUpcomingElections.adapter = ElectionListAdapter(ElectionListener { viewModel.elections })
        binding.rvSavedElections.adapter = ElectionListAdapter(ElectionListener { viewModel.elections })
        Timber.i("viewmodel $viewModel - package name: ${requireContext().packageName}")
        viewModel.elections.observe(viewLifecycleOwner, Observer {
            Timber.i("elections changed: $it")
        })
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == CivicApiStatus.LOADING) {
                binding.statusLoadingWheel.visibility = View.VISIBLE
                binding.rvUpcomingElections.visibility = View.GONE
                binding.rvSavedElections.visibility = View.GONE
            } else {
                if (it == CivicApiStatus.ERROR) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.api_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.doneShowingSnackBar()
                }
                binding.statusLoadingWheel.visibility = View.GONE
                binding.rvUpcomingElections.visibility = View.VISIBLE
            }

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