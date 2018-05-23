package ec.edu.unl.blockstudy.updateQuestionnaire.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.updateQuestionnaire.ui.UpdateQuestionnaireActivity
import javax.inject.Singleton

/**
 * Created by victor on 5/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, UpdateQuestionaireModule::class, MyAplicationModule::class, DomainModule::class))
interface UpdateQuestionaireComponent {
    fun inject(newQuestionnaireActivity: UpdateQuestionnaireActivity)
}