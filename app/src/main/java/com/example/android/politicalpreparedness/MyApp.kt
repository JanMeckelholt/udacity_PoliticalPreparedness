package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.elections.ElectionsViewModel
import com.example.android.politicalpreparedness.elections.electiondetail.ElectionDetailViewModel
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : BaseAppliction() {

    override fun onCreate() {
        super.onCreate()
        val myModule = module {
            viewModel {
                ElectionsViewModel(get() as ElectionDao)
            }
            viewModel {
                ElectionDetailViewModel(get() as ElectionDao)
            }
            viewModel {
                RepresentativeViewModel(this@MyApp)
            }
            single { ElectionDatabase.getInstance(this@MyApp).electionDao  }
        }
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}