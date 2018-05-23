package ec.com.dovic.aprendiendo.detailQuestionaire.di

import dagger.Component
import ec.com.dovic.aprendiendo.MyAplicationModule
import ec.com.dovic.aprendiendo.detailQuestionaire.ui.QuestionaireActivity
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Singleton
@Component(modules = arrayOf(MyAplicationModule::class, DomainModule::class, QuestionnaireModule::class, LibModule::class))
interface QuestionnaireComponent {
    fun inject(que: QuestionaireActivity)

}