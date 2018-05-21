package ec.edu.unl.blockstudy.domain.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.database.Db
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.services.DbApi
import javax.inject.Singleton

@Module
class DomainModule(var app: MyApplication, var db: Db) {

    @Provides
    @Singleton
    fun providesDbApi(): DbApi {
        return DbApi(db)
    }

    @Provides
    @Singleton
    fun providesSharePreferences(): SharePreferencesApi {
        return SharePreferencesApi(app.getSharePreferences())
    }

    @Provides
    @Singleton
    fun providesFirebaseApi(): FirebaseApi {
        return FirebaseApi(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance(), FirebaseStorage.getInstance().reference)
    }


}