package com.camtittle.photosharing.ui.auth.signup

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class SignUpModel : BaseObservable() {

    var email: String = ""
    @Bindable get() = field
    set(email) {
        field = email
        notifyPropertyChanged(BR.email)
    }

    var password: String = ""
    @Bindable get() = field
    set(password) {
        field = password
        notifyPropertyChanged(BR.password)
    }
}