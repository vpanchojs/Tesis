package ec.com.dovic.aprendiendo.signup.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.signup.ui.SignupActivity
import javax.inject.Singleton

/**
 * Created by Yavac on 15/1/2018.
 */

@Singleton
@Component(modules = arrayOf(LibModule::class, SignupModule::class, MyAplicationModule::class, DomainModule::class))
interface SignupComponent {
    fun inject(activity: SignupActivity)
}