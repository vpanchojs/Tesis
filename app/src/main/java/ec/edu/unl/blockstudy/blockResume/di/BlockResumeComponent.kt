package ec.edu.unl.blockstudy.blockResume.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeFragment
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, BlockResumeModule::class, LibModule::class))
interface BlockResumeComponent {
    fun inject(fragment: BlockResumeFragment)

}