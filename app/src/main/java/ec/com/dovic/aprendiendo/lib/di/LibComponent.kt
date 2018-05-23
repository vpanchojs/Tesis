package ec.com.dovic.aprendiendo.lib.di

import ec.com.dovic.aprendiendo.MyAplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf( LibModule::class, MyAplicationModule::class))
interface LibComponent {

}