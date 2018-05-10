package ec.edu.unl.blockstudy.questionnaireResume.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.QuestionnaireResumenaireResume.di.QuestionnaireResumeModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.questionnaireResume.ui.QuestionnaireResumeActivity
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionnaireResumeModule::class, LibModule::class))
interface QuestionnaireResumeComponent {
    fun inject(activity: QuestionnaireResumeActivity)
}