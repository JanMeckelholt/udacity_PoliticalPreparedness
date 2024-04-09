package com.example.android.politicalpreparedness.elections.electiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding
import com.example.android.politicalpreparedness.elections.CivicApiStatus
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber

class ElectionDetailFragment : Fragment() {

    private val args: ElectionDetailFragmentArgs by navArgs()
    private val viewModel : ElectionDetailViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View? {
        val election = args.selectedElection
        Timber.i("selectedElection: $election")
        val binding = FragmentElectionDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setElection(election)

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == CivicApiStatus.LOADING) {
                binding.statusLoadingWheel.visibility = View.VISIBLE
                binding.stateHeader.visibility = View.GONE
                binding.stateLocations.visibility = View.GONE
                binding.stateBallot.visibility = View.GONE
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
                binding.stateHeader.visibility = View.VISIBLE
                binding.stateLocations.visibility = View.VISIBLE
                binding.stateBallot.visibility = View.VISIBLE
            }

        })
        // TODO: Add ViewModel values and create ViewModel

        // TODO: Add binding values

        // TODO: Populate voter info -- hide views without provided data.

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        // TODO: Handle loading of URLs

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }

    // TODO: Create method to load URL intents
}