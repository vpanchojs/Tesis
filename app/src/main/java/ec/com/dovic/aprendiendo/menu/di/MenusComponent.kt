package ec.com.dovic.aprendiendo.menu.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.menu.ui.MenuFragment
import javax.inject.Singleton

/**
 * Created by victor on 27/1/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MenusModule::class, MyAplicationModule::class, DomainModule::class))
interface MenusComponent {
    fun inject(menuFragment: MenuFragment)
}