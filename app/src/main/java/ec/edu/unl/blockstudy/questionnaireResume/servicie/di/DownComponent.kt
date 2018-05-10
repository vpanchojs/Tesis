package ec.edu.unl.blockstudy.questionnaireResume.servicie.di

import android.app.IntentService
import dagger.Component
import ec.edu.unl.blockstudy.MyAplicationModule
import ec.edu.unl.blockstudy.domain.di.DomainModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DownModule::class, MyAplicationModule::class, DomainModule::class))
interface DownComponent {
    fun inject(service: IntentService)
}