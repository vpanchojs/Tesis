package ec.edu.unl.blockstudy.signup.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.signup.ui.SignupActivity
import javax.inject.Singleton

/**
 * Created by Yavac on 15/1/2018.
 */

@Singleton
@Component(modules = arrayOf(LibModule::class, SignupModule::class, MyAplicationModule::class, DomainModule::class))
interface SignupComponent {
    fun inject(activity: SignupActivity)
}