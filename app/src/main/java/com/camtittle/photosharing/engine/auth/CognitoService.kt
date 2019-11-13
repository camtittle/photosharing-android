package com.camtittle.photosharing.engine.auth

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.results.SignUpResult
import com.camtittle.photosharing.engine.auth.model.ConfirmResponse
import com.camtittle.photosharing.engine.auth.model.SignUpResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback
import java.lang.Exception

object CognitoService {

    val instance = AWSMobileClient.getInstance()

    fun signUp(email: String, password: String, callback: ServiceCallback<SignUpResponse>) {
        // We are using email as username here, but also add email as a user attribute
        // in case other forms of username are used in the future
        val attributes = HashMap<String, String>()
        attributes["email"] = email
        Log.d("SignUpViewModel", password)
        instance.signUp(email, password, attributes, null, object : Callback<SignUpResult> {

            override fun onResult(result: SignUpResult?) {
                if (result == null) {
                    callback.onError(CallbackError("SignUp Result was null"))
                    return
                }

                val confirmationDestination = result.userCodeDeliveryDetails.destination
                val response = SignUpResponse(result.confirmationState, confirmationDestination)
                callback.onSuccess(response)
            }

            override fun onError(e: Exception?) {
                callback.onError(CallbackError(e?.message, e))
            }

        })
    }

    fun confirmAccount(email: String, code: String, callback: ServiceCallback<ConfirmResponse>) {
        instance.confirmSignUp(email, code, object : Callback<SignUpResult> {

            override fun onResult(result: SignUpResult?) {
                if (result == null) {
                    callback.onError(CallbackError("SignUp Result was null"))
                    return
                }

                val response = ConfirmResponse(result.confirmationState)
                callback.onSuccess(response)
            }

            override fun onError(e: Exception?) {
                callback.onError(CallbackError(e?.message, e))
            }

        })
    }

    fun isSignedIn(): Boolean {
        return instance.currentUserState().userState == UserState.SIGNED_IN
    }

}