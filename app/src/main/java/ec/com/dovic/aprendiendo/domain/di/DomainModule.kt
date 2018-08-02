package ec.com.dovic.aprendiendo.domain.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.database.Db
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
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
        return FirebaseApi(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance(), FirebaseStorage.getInstance().reference,FirebaseFunctions.getInstance())
    }


}