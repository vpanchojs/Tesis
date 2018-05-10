package ec.edu.unl.blockstudy.myquestionnaires.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.myquestionnaires.ui.MyQuestionnairesFragment
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MyQuestionaireModule::class, MyAplicationModule::class, DomainModule::class))
interface MyQuestionaireComponent {
    fun inject(fragment: MyQuestionnairesFragment)
}