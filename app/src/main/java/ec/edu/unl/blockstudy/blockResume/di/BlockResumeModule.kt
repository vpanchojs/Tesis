package ec.edu.unl.blockstudy.blockResume.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.blockResume.*
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeView
import ec.edu.unl.blockstudy.database.Db
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
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