package ec.edu.unl.blockstudy.menu.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.menu.ui.MenuFragment
import javax.inject.Singleton

/**
 * Created by victor on 27/1/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MenusModule::class, MyAplicationModule::class, DomainModule::class))
interface MenusComponent {
    fun inject(menuFragment: MenuFragment)
}