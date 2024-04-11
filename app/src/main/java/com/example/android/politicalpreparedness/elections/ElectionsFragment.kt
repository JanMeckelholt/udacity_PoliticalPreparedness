package com.example.android.politicalpreparedness.elections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionsBinding
import com.example.android.politicalpreparedness.elections.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.elections.adapter.ElectionListener
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

        val binding = FragmentElectionsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvUpcomingElections.adapter = ElectionListAdapter(ElectionListener { viewModel.displayElectionDetails(it) })
        binding.rvSavedElections.adapter = ElectionListAdapter(ElectionListener { viewModel.displayElectionDetails(it) })

        viewModel.elections.observe(viewLifecycleOwner, Observer {
            Timber.i("elections changed: $it")
        })
        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            Timber.i("saved elections changed: $it")
        })
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == Constants.Status.LOADING) {
                binding.statusApiLoadingWheel.visibility = View.VISIBLE
                binding.rvUpcomingElections.visibility = View.GONE
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
                binding.rvUpcomingElections.visibility = View.VISIBLE
            }

        })

        viewModel.navigateToSelectedElection.observe(viewLifecycleOwner, Observer {
            it?.let {
                val bundle = Bundle()
                bundle.putParcelable("selectedElection", it)
                Timber.i("selectedElection ${it.name}")
                this.findNavController().navigate(R.id.action_electionsFragment_to_electionDetailFragment, bundle)
                viewModel.navigationDone()
            }
        })

//        viewModel.dataSource.getElectionsLiveData().observe(viewLifecycleOwner, Observer {
//            val adapter = binding.rvSavedElections.adapter as ElectionListAdapter
//            adapter.submitList(it)
//        })



        // TODO: Add ViewModel values and create ViewModel

        // TODO: Add binding values

        // TODO: Link elections to voter info

        // TODO: Initiate recycler adapters

        // TODO: Populate recycler adapters
        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
}