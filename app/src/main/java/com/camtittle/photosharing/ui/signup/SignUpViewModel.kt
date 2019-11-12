package com.camtittle.photosharing.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.CognitoService
import com.camtittle.photosharing.engine.auth.model.SignUpResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback

class SignUpViewModel : ViewModel() {

    val signUp = SignUpModel()

    private val _signUpResponse = MutableLiveData<SignUpResponse>()
    val signUpResponse: LiveData<SignUpResponse> = _signUpResponse

    private val tag = SignUpViewModel::class.java.name

    fun onClickSubmitButton() {
        Log.d(tag, "button clicked")
        Log.d(tag, signUp.email ?: "empty")

        val email = signUp.email;
        val password = signUp.password;
        if (email != null && password != null) {
            CognitoService.signUp(email, password, object : ServiceCallback<SignUpResponse> {
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

}
