package ec.com.dovic.aprendiendo.repository.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryFragment
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, QuestionaireRepositoryModule::class, MyAplicationModule::class, DomainModule::class))
interface QuestionaireRepositoryComponent {
    fun inject(fragment: QuestionnaireRepositoryFragment)
}