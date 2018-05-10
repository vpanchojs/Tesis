package ec.edu.unl.blockstudy.detailQuestionaire.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.detailQuestionaire.ui.QuestionaireActivity
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionnaireModule::class, LibModule::class))
interface QuestionnaireComponent {
    fun inject(que: QuestionaireActivity)

}