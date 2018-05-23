package ec.com.dovic.aprendiendo.myrepository.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.myrepository.ui.MyRepositoryActivity
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MyRepositoryModule::class, MyAplicationModule::class, DomainModule::class))
interface MyRepositoryComponent {
    fun inject(activity: MyRepositoryActivity)
}