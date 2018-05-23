package ec.com.dovic.aprendiendo.blockResume.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.blockResume.ui.BlockResumeFragment
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, BlockResumeModule::class, LibModule::class))
interface BlockResumeComponent {
    fun inject(fragment: BlockResumeFragment)

}