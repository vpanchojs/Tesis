package ec.edu.unl.blockstudy.repository.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.repository.ui.QuestionnaireRepositoryFragment
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, QuestionaireRepositoryModule::class, MyAplicationModule::class, DomainModule::class))
interface QuestionaireRepositoryComponent {
    fun inject(fragment: QuestionnaireRepositoryFragment)
}