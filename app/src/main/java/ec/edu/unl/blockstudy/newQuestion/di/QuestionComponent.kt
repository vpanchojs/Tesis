package ec.edu.unl.blockstudy.newQuestion.di

import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.newQuestion.ui.QuestionActivity
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionModule::class, LibModule::class))
interface QuestionComponent {
    fun inject(questionActivity: QuestionActivity)
}