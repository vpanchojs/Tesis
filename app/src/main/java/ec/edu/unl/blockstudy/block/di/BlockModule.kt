package ec.edu.unl.blockstudy.block.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.block.*
import ec.edu.unl.blockstudy.block.ui.BlockView
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import javax.inject.Singleton

/**
 * Created by victor on 26/2/18.
 */
@Module
class BlockModule(var view: BlockView) {

    @Provides
    @Singleton
    fun providesView(): BlockView {
        return view;
    }

    @Provides
    @Singleton
    fun providesPresenter(eventBus: EventBusInterface, view: BlockView, interactor: BlockInteractor): BlockPresenter {
        return BlockPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesInteractor(repository: BlockRepository): BlockInteractor {
        return BlockInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, sharePreferencesApi: SharePreferencesApi, objectBoxApi: ObjectBoxApi): BlockRepository {
        return BlockRepositoryImp(eventBus, firebaseApi, sharePreferencesApi, objectBoxApi)
    }
}