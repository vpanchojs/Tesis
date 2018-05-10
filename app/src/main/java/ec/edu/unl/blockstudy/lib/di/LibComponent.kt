package ec.edu.unl.blockstudy.lib.di

import ec.edu.unl.blockstudy.MyAplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf( LibModule::class, MyAplicationModule::class))
interface LibComponent {

}