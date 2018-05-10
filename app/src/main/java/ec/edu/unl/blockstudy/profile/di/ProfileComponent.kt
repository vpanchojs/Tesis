package ec.edu.unl.blockstudy.profile.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.profile.ui.ProfileActivity
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LibModule::class, ProfileModule::class, MyAplicationModule::class, DomainModule::class))
interface ProfileComponent {
    fun inject(activity: ProfileActivity)
}