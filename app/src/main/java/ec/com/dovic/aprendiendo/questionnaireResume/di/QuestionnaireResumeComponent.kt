package ec.com.dovic.aprendiendo.questionnaireResume.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.QuestionnaireResumenaireResume.di.QuestionnaireResumeModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeActivity
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionnaireResumeModule::class, LibModule::class))
interface QuestionnaireResumeComponent {
    fun inject(activity: QuestionnaireResumeActivity)
}