package ec.edu.unl.blockstudy.profile.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.profile.*
import ec.edu.unl.blockstudy.profile.ui.ProfileView
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