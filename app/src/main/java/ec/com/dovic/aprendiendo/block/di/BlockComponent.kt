package ec.com.dovic.aprendiendo.block.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.block.ui.BlockActivity
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, BlockModule::class, LibModule::class))
interface BlockComponent {
    fun inject(questionActivity: BlockActivity)
}