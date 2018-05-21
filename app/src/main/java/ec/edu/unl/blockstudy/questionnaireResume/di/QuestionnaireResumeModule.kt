package ec.edu.unl.blockstudy.QuestionnaireResumenaireResume.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionnaireResume.*
import ec.edu.unl.blockstudy.questionnaireResume.ui.QuestionnaireResumeView
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Module
class QuestionnaireResumeModule(var view: QuestionnaireResumeView) {

    @Provides
    @Singleton
    fun providesView(): QuestionnaireResumeView {
        return view;
    }

    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: QuestionnaireResumeView, interactor: QuestionnaireResumeInteractor): QuestionnaireResumePresenter {
        return QuestionnaireResumePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: QuestionnaireResumeRepository): QuestionnaireResumeInteractor {
        return QuestionnaireResumeInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): QuestionnaireResumeRepository {
        return QuestionnaireResumeRepositoryImp(eventBus, firebaseApi)
    }
}