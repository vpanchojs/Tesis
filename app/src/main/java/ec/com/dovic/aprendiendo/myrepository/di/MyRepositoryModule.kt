package ec.com.dovic.aprendiendo.myrepository.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myrepository.*
import ec.com.dovic.aprendiendo.myrepository.ui.MyRepositoryView
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
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): MyRepositoryRepository {
        return MyRepositoryRepositoryImp(eventBus, firebaseApi)
    }

}