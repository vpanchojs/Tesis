package ec.edu.unl.blockstudy.QuestionnaireResumenaireResume.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumeInteractorImp
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumePresenterImp
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumeRepositoryImp
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumeInteractor
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumePresenter
import ec.edu.unl.blockstudy.questionnaireResume.QuestionnaireResumeRepository
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
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, sharePreferencesApi: SharePreferencesApi, objectBoxApi: ObjectBoxApi): QuestionnaireResumeRepository {
        return QuestionnaireResumeRepositoryImp(eventBus, firebaseApi, sharePreferencesApi, objectBoxApi)
    }
}