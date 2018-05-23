package ec.com.dovic.aprendiendo.updateQuestionnaire.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.updateQuestionnaire.*
import ec.com.dovic.aprendiendo.updateQuestionnaire.ui.UpdateQuestionaireView
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