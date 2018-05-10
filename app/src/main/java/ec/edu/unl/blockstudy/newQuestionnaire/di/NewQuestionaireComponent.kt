package ec.edu.unl.blockstudy.newQuestionnaire.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.newQuestionnaire.ui.NewQuestionnaireActivity
import javax.inject.Singleton

/**
 * Created by victor on 5/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, NewQuestionaireModule::class, MyAplicationModule::class, DomainModule::class))
interface NewQuestionaireComponent {
    fun inject(newQuestionnaireActivity: NewQuestionnaireActivity)
}