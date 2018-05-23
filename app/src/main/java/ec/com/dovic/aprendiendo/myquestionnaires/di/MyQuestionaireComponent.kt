package ec.com.dovic.aprendiendo.myquestionnaires.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.myquestionnaires.ui.MyQuestionnairesFragment
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, MyQuestionaireModule::class, MyAplicationModule::class, DomainModule::class))
interface MyQuestionaireComponent {
    fun inject(fragment: MyQuestionnairesFragment)
}