package ec.com.dovic.aprendiendo.blockResume.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.blockResume.*
import ec.com.dovic.aprendiendo.blockResume.ui.BlockResumeView
import ec.com.dovic.aprendiendo.database.Db
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import javax.inject.Singleton

/**
 * Created by victor on 25/2/18.
 */
@Module
class BlockResumeModule(var view: BlockResumeView) {

    @Provides
    @Singleton
    fun providesView() = view


    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: BlockResumeView, interactor: BlockResumeInteractor): BlockResumePresenter {
        return BlockResumePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: BlockResumeRepository): BlockResumeInteractor {
        return BlockResumeInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, db: DbApi): BlockResumeRepository {
        return BlockResumeRepositoryImp(eventBus, firebaseApi, db)
    }
}