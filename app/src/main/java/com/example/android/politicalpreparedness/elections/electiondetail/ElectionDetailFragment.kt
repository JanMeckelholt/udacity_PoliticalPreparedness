package com.example.android.politicalpreparedness.elections.electiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber

class ElectionDetailFragment : Fragment() {

    private val args: ElectionDetailFragmentArgs by navArgs()
    private val viewModel: ElectionDetailViewModel by inject()
    private lateinit var binding: FragmentElectionDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View? {
        val election = args.selectedElection
        Timber.i("selectedElection: $election")
        binding = FragmentElectionDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setElection(election)

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == Constants.Status.LOADING) {
                binding.statusApiLoadingWheel.visibility = View.VISIBLE
                binding.tvElectionInformationHeader.visibility = View.GONE
                binding.tvVotingLocations.visibility = View.GONE
                binding.tvBallotInformation.visibility = View.GONE
            } else {
                if (it == Constants.Status.ERROR) {
                    Snackbar
                        .make(
                            requireActivity().findViewById(android.R.id.content),
                            getString(R.string.api_error),
                            Snackbar.LENGTH_LONG
                        )
                        .setBackgroundTint(resources.getColor(R.color.colorError))
                        .setTextColor(resources.getColor(R.color.colorBlack))
                        .show()
                    viewModel.doneShowingSnackBar()
                }
                binding.statusApiLoadingWheel.visibility = View.GONE
                binding.tvElectionInformationHeader.visibility = View.VISIBLE
                binding.tvVotingLocations.visibility = View.VISIBLE
                binding.tvBallotInformation.visibility = View.VISIBLE
            }

        })

        viewModel.isFollowed.observe(viewLifecycleOwner, Observer {
            setButtonToFollow(it)
        })

        return binding.root
    }

    private fun setButtonToFollow(follow: Boolean?) {
        if (follow == null) {
            binding.btnFollowElection.visibility = View.GONE
        } else {
            binding.btnFollowElection.visibility = View.VISIBLE
            if (follow) {
                binding.btnFollowElection.text = getString(R.string.unfollow_election)
            } else {
                binding.btnFollowElection.text = getString(R.string.follow_election)
            }
        }
    }

}