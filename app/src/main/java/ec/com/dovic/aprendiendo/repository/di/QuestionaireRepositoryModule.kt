package ec.com.dovic.aprendiendo.repository.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.repository.*
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryView
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Module
class QuestionaireRepositoryModule(val view: QuestionnaireRepositoryView) {

    @Provides
    @Singleton
    fun providesView() = view


    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: QuestionnaireRepositoryView, interactor: QuestionnaireRepositoryInteractor): QuestionnaireRepositoryPresenter {
        return QuestionnaireRepositoryPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: QuestionnaireRepositoryRepository): QuestionnaireRepositoryInteractor {
        return QuestionnaireRepositoryInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): QuestionnaireRepositoryRepository {
        return QuestionnaireRepositoryRepositoryImp(eventBus, firebaseApi)
    }

}