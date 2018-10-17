package ec.com.dovic.aprendiendo.menu.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.menu.*
import ec.com.dovic.aprendiendo.menu.ui.MenusView
import javax.inject.Singleton

@Module
class MenusModule(var view: MenusView) {
    @Provides
    @Singleton
    fun providesMenuView(): MenusView {
        return view;
    }

    @Provides
    @Singleton
    fun providesMenusPresenter(eventBus: EventBusInterface, view: MenusView, interactor: MenusInteractor): MenusPresenter {
        return MenusPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesMenusInteractor(repository: MenusRepository): MenusInteractor {
        return MenusInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesMenusRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi,sharePreferencesApi: SharePreferencesApi): MenusRepository {
        return MenusRepositoryImp(eventBus, firebaseApi,sharePreferencesApi)
    }
}


