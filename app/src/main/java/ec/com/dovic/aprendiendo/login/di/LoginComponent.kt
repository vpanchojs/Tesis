package ec.com.dovic.aprendiendo.login.di

import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.login.ui.LoginActivity
import dagger.Component
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LibModule::class, LoginModule::class, MyAplicationModule::class, DomainModule::class))
interface LoginComponent {
    fun inject(activity: LoginActivity)
}