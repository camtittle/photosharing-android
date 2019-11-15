package com.camtittle.photosharing.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager
import com.camtittle.photosharing.engine.auth.model.ConfirmResponse
import com.camtittle.photosharing.engine.auth.model.SignInResponse
import com.camtittle.photosharing.engine.auth.model.SignUpResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback
import com.camtittle.photosharing.engine.common.result.Event

class AuthViewModel : ViewModel() {

    val model = AuthModel()

    private val _signUpResponse = MutableLiveData<SignUpResponse>()
    val signUpResponse: LiveData<SignUpResponse> = _signUpResponse

    private val _signInResponse = MutableLiveData<Event<SignInResponse>>()
    val signInResponse: LiveData<Event<SignInResponse>> = _signInResponse

    private val _confirmResponse = MutableLiveData<ConfirmResponse>()
    val confirmResponse: LiveData<ConfirmResponse> = _confirmResponse

    val tag = AuthViewModel::class.java.name

    fun signUp() {
        val email = model.email
        val password = model.password
        if (email.isNotBlank() && password.isNotBlank()) {
            AuthManager.signUp(email, password, object : ServiceCallback<SignUpResponse> {
                override fun onError(error: CallbackError) {
                    Log.d(tag, "ERROR. " + error.msg)
                }

                override fun onSuccess(response: SignUpResponse) {
                    Log.d(tag, "SUCCESS. Confirmation sent to:" + response.confirmationDestination)
                    _signUpResponse.postValue(response)
                }

            })
        }
    }

    fun confirmAccount() {
        val email = model.email
        val code = model.confirmationCode
        Log.d(tag, "Confirming $email with code: $code")
        if (email.isNotEmpty() && code.isNotEmpty()) {
            AuthManager.confirmAccount(email, code, object : ServiceCallback<ConfirmResponse> {
                override fun onSuccess(response: ConfirmResponse) {
                    if (response.confirmed) {
                        Log.d(tag, "Confirmation successful for email $email")
                        // Now sign in
                        signIn()
                    } else {
                        Log.e(tag, "Something went wrong with confirmation")
                    }
                }

                override fun onError(error: CallbackError) {
                    Log.d(tag, "ERROR. " + error.msg)
                }
            })

        }
    }

    fun signIn() {
        val email = model.email
        val password = model.password
        Log.d(tag, "$email $password")
        if (!email.isBlank() && !password.isBlank()) {
            AuthManager.signIn(email, password, object : ServiceCallback<SignInResponse> {
                override fun onSuccess(response: SignInResponse) {
                    _signInResponse.postValue(Event(response))
                    model.clear()
                }

                override fun onError(error: CallbackError) {
                    Log.e(tag, error.msg?: "Something went wrong with sign in")
                }

            })
        }
    }
}