package ec.edu.unl.blockstudy.block.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.block.ui.BlockActivity
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, BlockModule::class, LibModule::class))
interface BlockComponent {
    fun inject(questionActivity: BlockActivity)
}