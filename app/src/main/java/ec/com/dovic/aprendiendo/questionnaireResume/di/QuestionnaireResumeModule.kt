package ec.com.dovic.aprendiendo.QuestionnaireResumenaireResume.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.RetrofitApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.questionnaireResume.*
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeView
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
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, dbApi: DbApi, retrofitApi: RetrofitApi): QuestionnaireResumeRepository {
        return QuestionnaireResumeRepositoryImp(eventBus, firebaseApi, dbApi, retrofitApi)
    }
}