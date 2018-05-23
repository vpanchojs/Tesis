package ec.com.dovic.aprendiendo.updateQuestionnaire.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.updateQuestionnaire.ui.UpdateQuestionnaireActivity
import javax.inject.Singleton

/**
 * Created by victor on 5/2/18.
 */
@Singleton
@Component(modules = arrayOf(LibModule::class, UpdateQuestionaireModule::class, MyAplicationModule::class, DomainModule::class))
interface UpdateQuestionaireComponent {
    fun inject(newQuestionnaireActivity: UpdateQuestionnaireActivity)
}