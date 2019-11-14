package com.camtittle.photosharing.ui.auth.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.CognitoService
import com.camtittle.photosharing.engine.auth.model.SignInResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback

class SignInViewModel : ViewModel() {

    val signInModel = SignInModel()

    private val _response = MutableLiveData<SignInResponse>()
    val response: LiveData<SignInResponse> = _response

    private val tag = SignInViewModel::class.java.name

    fun onClickSignInButton() {
        val email = signInModel.email
        val password = signInModel.password
        Log.d(tag, "$email $password")
        if (!email.isBlank() && !password.isBlank()) {
            CognitoService.signIn(email, password, object : ServiceCallback<SignInResponse> {
                override fun onSuccess(response: SignInResponse) {
                    _response.postValue(response)
                }

                override fun onError(error: CallbackError) {
                    Log.e(tag, error.msg)
                }

            })
        }
    }

}
