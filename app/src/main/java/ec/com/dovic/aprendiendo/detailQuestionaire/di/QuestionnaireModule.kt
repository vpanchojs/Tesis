package ec.com.dovic.aprendiendo.detailQuestionaire.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.detailQuestionaire.*
import ec.com.dovic.aprendiendo.detailQuestionaire.ui.QuestionnaireView
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Module
class QuestionnaireModule(var view: QuestionnaireView) {

    @Provides
    @Singleton
    fun providesView() = view


    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: QuestionnaireView, interactor: QuestionnariesInteractor): QuestionnariesPresenter {
        return QuestionnariesPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: QuestionnariesRepository): QuestionnariesInteractor {
        return QuestionnariesInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): QuestionnariesRepository {
        return QuestionnariesRepositoryImp(eventBus, firebaseApi)
    }
}