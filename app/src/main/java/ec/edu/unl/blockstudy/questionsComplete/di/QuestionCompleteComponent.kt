package ec.edu.unl.blockstudy.questionsComplete.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.QuestionCompletesComplete.di.QuestionCompleteModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.questionsComplete.ui.QuestionsCompleteActivity
import javax.inject.Singleton

/**
 * Created by victor on 5/3/18.
 */

@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionCompleteModule::class, LibModule::class))
interface QuestionCompleteComponent {
    fun inject(activity: QuestionsCompleteActivity)
}