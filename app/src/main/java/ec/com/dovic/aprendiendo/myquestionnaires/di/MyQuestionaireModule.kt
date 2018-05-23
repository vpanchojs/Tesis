package ec.com.dovic.aprendiendo.myquestionnaires.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.database.Db
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myquestionnaires.*
import ec.com.dovic.aprendiendo.myquestionnaires.ui.MyQuestionnariesView
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Module
class MyQuestionaireModule(val view: MyQuestionnariesView) {

    @Provides
    @Singleton
    fun providesView() = view


    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: MyQuestionnariesView, interactor: MyQuestionaireInteractor): MyQuestionairePresenter {
        return MyQuestionairePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: MyQuestionaireRepository): MyQuestionaireInteractor {
        return MyQuestionaireInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, db: DbApi): MyQuestionaireRepository {
        return MyQuestionaireRepositoryImp(eventBus, firebaseApi, db)
    }

}