package ec.com.dovic.aprendiendo.login.di

import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.login.*
import ec.com.dovic.aprendiendo.login.ui.LoginView
import javax.inject.Singleton

/**
 * Created by victor on 15/1/18.
 */
@Module
class LoginModule(var view: LoginView) {

    @Provides
    @Singleton
    fun providesLoginView(): LoginView {
        return view;
    }

    @Provides
    @Singleton
    fun providesLoginPresenter(eventBus: EventBusInterface, view: LoginView, interactor: LoginInteractor): LoginPresenter {
        return LoginPresenterImp(eventBus, view, interactor)
    }

    @Provides
    @Singleton
    fun providesLoginInteractor(repository: LoginRepository): LoginInteractor {
        return LoginInteractorImp(repository)
    }

    @Provides
    @Singleton
    fun providesLoginRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi): LoginRepository {
        return LoginRepositoryImp(eventBus, firebaseApi)
    }

}