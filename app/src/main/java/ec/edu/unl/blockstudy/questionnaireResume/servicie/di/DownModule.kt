package ec.edu.unl.blockstudy.questionnaireResume.servicie.di

import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.login.ui.LoginView
import ec.edu.unl.blockstudy.questionnaireResume.servicie.DownRepository
import javax.inject.Singleton

/**
 * Created by victor on 15/1/18.
 */
@Module
class DownModule() {

    @Provides
    @Singleton
    fun providesRepository(objectBoxApi: ObjectBoxApi, firebaseApi: FirebaseApi, sharePreferencesApi: SharePreferencesApi): DownRepository {
        return DownRepository(objectBoxApi, firebaseApi, sharePreferencesApi)
    }

}