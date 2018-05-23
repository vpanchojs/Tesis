package ec.edu.unl.blockstudy.repository.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.repository.*
import ec.edu.unl.blockstudy.repository.ui.QuestionnaireRepositoryView
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