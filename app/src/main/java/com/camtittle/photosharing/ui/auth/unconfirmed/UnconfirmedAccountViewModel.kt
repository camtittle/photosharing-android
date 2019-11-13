package com.camtittle.photosharing.ui.auth.unconfirmed

import android.util.Log
import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.CognitoService
import com.camtittle.photosharing.engine.auth.model.ConfirmResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback

class UnconfirmedAccountViewModel : ViewModel() {

    var unconfirmedEmail: String? = null
    var code: String = ""

    private val tag = UnconfirmedAccountViewModel::class.java.name

    fun onClickConfirmAccountButton() {
        Log.d(tag, "Confirming with code: $code")
        val email = unconfirmedEmail
        if (!email.isNullOrEmpty() && code.isNotEmpty()) {
            CognitoService.confirmAccount(email, code, object : ServiceCallback<ConfirmResponse> {
                override fun onSuccess(response: ConfirmResponse) {
                    if (response.confirmed) {
                        Log.d(tag, "Confirmation successful for email $unconfirmedEmail")
                        val signedIn = CognitoService.isSignedIn()
                        Log.d(tag, "isSignedIn: $signedIn")
                    }
                }

                override fun onError(error: CallbackError) {
                    Log.d(tag, "ERROR. " + error.msg)
                }

            })

        }
    }
}