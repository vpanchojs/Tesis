package ec.com.dovic.aprendiendo.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import java.util.*

/**
 * Created by victor on 19/1/18.
 */
class User : Parcelable {
    var pk: String = ""
    var name: String = ""
    var lastname: String = ""
    var photo: String = ""
    var email: String = ""
    var password: String = ""
    var academics = ArrayList<Academic>()

    @Exclude
    var academic: Academic = Academic()
    @Exclude
    var preferences = ArrayList<Subject>()

    constructor(parcel: Parcel) {
        pk = parcel.readString()
        name = parcel.readString()
        lastname = parcel.readString()
        photo = parcel.readString()
        email = parcel.readString()
        academic = parcel.readParcelable(Academic::class.java.classLoader)
    }


    constructor(idUser: String, name: String, lastname: String, photo: String, email: String, password: String) {
        this.pk = idUser
        this.name = name
        this.lastname = lastname
        this.photo = photo
        this.email = email
        this.password = password
    }

    constructor(name: String, lastname: String, email: String) {
        this.name = name
        this.lastname = lastname
        this.email = email
    }

    constructor() {

    }


    @Exclude
    fun toMapPostSave(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["name"] = name
        result["lastname"] = lastname
        result["photo"] = photo
        return result
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pk)
        parcel.writeString(name)
        parcel.writeString(lastname)
        parcel.writeString(photo)
        parcel.writeString(email)
        parcel.writeParcelable(academic, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        const val FACEBOOK = 1
        const val GOOGLE = 2
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}