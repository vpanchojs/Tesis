package ec.com.dovic.aprendiendo.QuestionCompletesComplete.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.questionsComplete.*
import ec.com.dovic.aprendiendo.questionsComplete.ui.QuestionCompleteView
import javax.inject.Singleton


@Module
class QuestionCompleteModule(var view: QuestionCompleteView) {

    @Provides
    @Singleton
    fun providesView(): QuestionCompleteView {
        return view;
    }

    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: QuestionCompleteView, interactor: QuestionCompleteInteractor): QuestionCompletePresenter {
        return QuestionCompletePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: QuestionCompleteRepository): QuestionCompleteInteractor {
        return QuestionCompleteInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, dbApi: DbApi): QuestionCompleteRepository {
        return QuestionCompleteRepositoryImp(eventBus, firebaseApi,dbApi)
    }
}