package ec.edu.unl.blockstudy.QuestionCompletesComplete.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionsComplete.*
import ec.edu.unl.blockstudy.questionsComplete.ui.QuestionCompleteView
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