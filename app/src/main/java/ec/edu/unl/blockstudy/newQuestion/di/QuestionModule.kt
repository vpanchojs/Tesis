package ec.edu.unl.blockstudy.newQuestion.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestion.*
import ec.edu.unl.blockstudy.newQuestion.ui.QuestionView
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Module
class QuestionModule(var view: QuestionView) {

    @Provides
    @Singleton
    fun providesView(): QuestionView {
        return view;
    }

    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: QuestionView, interactor: QuestionInteractor): QuestionPresenter {
        return QuestionPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: QuestionRepository): QuestionInteractor {
        return QuestionInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): QuestionRepository {
        return QuestionRepositoryImp(eventBus, firebaseApi)
    }
}