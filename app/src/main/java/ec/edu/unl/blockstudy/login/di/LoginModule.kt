package ec.edu.unl.blockstudy.login.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.login.*
import ec.edu.unl.blockstudy.login.ui.LoginView
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
    fun providesLoginRepository(eventBus: EventBusInterface, firebaseApi: FirebaseApi, sharePreferencesApi: SharePreferencesApi): LoginRepository {
        return LoginRepositoryImp(eventBus, firebaseApi,  sharePreferencesApi)
    }

}