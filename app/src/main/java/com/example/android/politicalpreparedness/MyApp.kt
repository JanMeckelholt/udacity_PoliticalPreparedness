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

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                ElectionsViewModel()
            }
            single {
                //This view model is declared singleton to be used across multiple fragments
                ElectionDetailViewModel(get() as ElectionDao)
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                RepresentativeViewModel()
            }

            single { ElectionDatabase.getInstance(this@MyApp).electionDao  }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}