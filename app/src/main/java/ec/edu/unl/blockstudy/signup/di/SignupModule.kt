package ec.edu.unl.blockstudy.signup.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.signup.*
import ec.edu.unl.blockstudy.signup.ui.SignupView
import javax.inject.Singleton

/**
 * Created by Yavac on 15/1/2018.
 */

@Module
class SignupModule(private val signupView: SignupView) {

    @Provides
    @Singleton
    fun providesSignupView() = signupView

    @Provides
    @Singleton
    fun providesSignupPresenter(signupView: SignupView, signupInteractor: SignupInteractor, eventBusInterface: EventBusInterface)
            : SignupPresenter = SignupPresenterImpl(signupView, signupInteractor, eventBusInterface)


    @Provides
    @Singleton
    fun providesSignupInteractor(signupRepository: SignupRepository)
            : SignupInteractor = SignupInteractorImpl(signupRepository)

    @Provides
    @Singleton
    fun providesSignupRepository(firebaseApi: FirebaseApi, eventBusInterface: EventBusInterface)
            : SignupRepository = SignupRepositoryImpl(firebaseApi, eventBusInterface)

}