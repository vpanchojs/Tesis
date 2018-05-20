package ec.edu.unl.blockstudy.myrepository.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.myrepository.ui.MyRepositoryActivity
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MyRepositoryModule::class, MyAplicationModule::class, DomainModule::class))
interface MyRepositoryComponent {
    fun inject(activity: MyRepositoryActivity)
}