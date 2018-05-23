package ec.com.dovic.aprendiendo.profile.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.profile.ui.ProfileActivity
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LibModule::class, ProfileModule::class, MyAplicationModule::class, DomainModule::class))
interface ProfileComponent {
    fun inject(activity: ProfileActivity)
}