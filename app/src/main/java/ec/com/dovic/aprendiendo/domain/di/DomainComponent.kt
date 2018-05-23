package ec.com.dovic.aprendiendo.domain.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import javax.inject.Singleton

/**
 * Created by victor on 21/12/17.
 */
@Singleton
@Component(modules = arrayOf(DomainModule::class, MyAplicationModule::class))
interface DomainComponent {

}