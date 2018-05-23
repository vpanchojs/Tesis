package ec.com.dovic.aprendiendo.profile.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.profile.*
import ec.com.dovic.aprendiendo.profile.ui.ProfileView
import javax.inject.Singleton

@Module
class ProfileModule(var view: ProfileView) {

    @Provides
    @Singleton
    fun providesProfileView(): ProfileView {
        return view;
    }

    @Provides
    @Singleton
    fun providesProfilePresenter(eventBus: EventBusInterface, view: ProfileView, interactor: ProfileInteractor): ProfilePresenter {
        return ProfilePresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesProfileInteractor(repository: ProfileRepository): ProfileInteractor {
        return ProfileInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesProfileRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi,sharePreferencesApi: SharePreferencesApi): ProfileRepository {
        return ProfileRepositoryImp(eventBus, firebaseApi,  sharePreferencesApi)
    }

}