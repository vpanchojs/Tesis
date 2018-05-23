package ec.edu.unl.blockstudy.updateQuestionnaire.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.updateQuestionnaire.*
import ec.edu.unl.blockstudy.updateQuestionnaire.ui.UpdateQuestionaireView
import javax.inject.Singleton

/**
 * Created by victor on 5/2/18.
 */

@Module
class UpdateQuestionaireModule(var view: UpdateQuestionaireView) {

    @Provides
    @Singleton
    fun providesLoginView(): UpdateQuestionaireView {
        return view;
    }

    @Provides
    @Singleton
    fun providesLoginPresenter(eventBus: EventBusInterface, view: UpdateQuestionaireView, interactor: UpdateQuestionaireInteractor): UpdateQuestionairePresenter {
        return UpdateQuestionairePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesLoginInteractor(repository: UpdateQuestionaireRepository): UpdateQuestionaireInteractor {
        return UpdateQuestionaireInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesLoginRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): UpdateQuestionaireRepository {
        return UpdateQuestionaireRepositoryImp(eventBus, firebaseApi)
    }
}