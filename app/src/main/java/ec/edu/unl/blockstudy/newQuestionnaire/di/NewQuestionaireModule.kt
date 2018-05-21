package ec.edu.unl.blockstudy.newQuestionnaire.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestionnaire.*
import ec.edu.unl.blockstudy.newQuestionnaire.ui.NewQuestionaireView
import javax.inject.Singleton

/**
 * Created by victor on 5/2/18.
 */

@Module
class NewQuestionaireModule(var view: NewQuestionaireView) {

    @Provides
    @Singleton
    fun providesLoginView(): NewQuestionaireView {
        return view;
    }

    @Provides
    @Singleton
    fun providesLoginPresenter(eventBus: EventBusInterface, view: NewQuestionaireView, interactor: NewQuestionaireInteractor): NewQuestionairePresenter {
        return NewQuestionairePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesLoginInteractor(repository: NewQuestionaireRepository): NewQuestionaireInteractor {
        return NewQuestionaireInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesLoginRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): NewQuestionaireRepository {
        return NewQuestionaireRepositoryImp(eventBus, firebaseApi)
    }
}