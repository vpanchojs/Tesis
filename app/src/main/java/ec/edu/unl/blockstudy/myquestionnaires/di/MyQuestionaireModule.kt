package ec.edu.unl.blockstudy.myquestionnaires.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.database.Db
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.myquestionnaires.*
import ec.edu.unl.blockstudy.myquestionnaires.ui.MyQuestionnariesView
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