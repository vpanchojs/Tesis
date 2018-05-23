package ec.com.dovic.aprendiendo.block.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.block.*
import ec.com.dovic.aprendiendo.block.ui.BlockView
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
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
    fun providesRepository(eventBus: EventBusInterface, dbApi: DbApi): BlockRepository {
        return BlockRepositoryImp(eventBus, dbApi)
    }
}