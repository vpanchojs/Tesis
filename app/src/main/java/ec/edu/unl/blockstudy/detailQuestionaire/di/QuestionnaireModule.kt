package ec.edu.unl.blockstudy.detailQuestionaire.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.detailQuestionaire.*
import ec.edu.unl.blockstudy.detailQuestionaire.ui.QuestionnaireView
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
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
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, objectBoxApi: ObjectBoxApi): QuestionnariesRepository {
        return QuestionnariesRepositoryImp(eventBus, firebaseApi, objectBoxApi)
    }
}