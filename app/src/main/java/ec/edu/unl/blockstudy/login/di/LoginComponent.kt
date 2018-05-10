package ec.edu.unl.blockstudy.login.di

import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.login.ui.LoginActivity
import dagger.Component
import ec.edu.unl.blockstudy.domain.di.DomainModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LibModule::class, LoginModule::class, MyAplicationModule::class, DomainModule::class))
interface LoginComponent {
    fun inject(activity: LoginActivity)
}