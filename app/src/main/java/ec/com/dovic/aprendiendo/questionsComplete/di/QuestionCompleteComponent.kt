package ec.com.dovic.aprendiendo.questionsComplete.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.QuestionCompletesComplete.di.QuestionCompleteModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.questionsComplete.ui.QuestionsCompleteActivity
import javax.inject.Singleton

/**
 * Created by victor on 5/3/18.
 */

@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionCompleteModule::class, LibModule::class))
interface QuestionCompleteComponent {
    fun inject(activity: QuestionsCompleteActivity)
}