package ec.edu.unl.blockstudy.myrepository.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.myrepository.*
import ec.edu.unl.blockstudy.myrepository.ui.MyRepositoryView
import javax.inject.Singleton

/**
 * Created by victor on 24/2/18.
 */
@Module
class MyRepositoryModule(val view: MyRepositoryView) {

    @Provides
    @Singleton
    fun providesView() = view


    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: MyRepositoryView, interactor: MyRepositoryInteractor): MyRepositoryPresenter {
        return MyRepositoryPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: MyRepositoryRepository): MyRepositoryInteractor {
        return MyRepositoryInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, objectBoxApi: ObjectBoxApi): MyRepositoryRepository {
        return MyRepositoryRepositoryImp(eventBus, firebaseApi, objectBoxApi)
    }

}