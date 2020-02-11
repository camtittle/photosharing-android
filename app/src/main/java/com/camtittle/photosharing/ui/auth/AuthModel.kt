package com.camtittle.photosharing.ui.auth

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class AuthModel : BaseObservable() {

    var email: String = ""
        @Bindable get
        set(email) {
            field = email
            notifyPropertyChanged(BR.email)
        }

    var password: String = ""
        @Bindable get
        set(password) {
            field = password
            notifyPropertyChanged(BR.password)
        }

    var name: String = ""
        @Bindable get
        set(name) {
            field = name
            notifyPropertyChanged(BR.name)
        }

    var confirmationCode: String = ""
        @Bindable get
        set(code) {
            field = code
            notifyPropertyChanged(BR.confirmationCode)
        }

    fun clear() {
        email = ""
        password = ""
        confirmationCode = ""
        name = ""
    }
}