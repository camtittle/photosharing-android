package com.camtittle.photosharing.ui.auth.unconfirmed

import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.model.SignUpResponse

class UnconfirmedAccountViewModel : ViewModel() {

    var message: String? = null

    private val tag = UnconfirmedAccountViewModel::class.java.name

    fun onClickCheckStatusButton() {

    }

    fun onSignUpResponse(result: SignUpResponse) {
        message = result.confirmationDestination
    }
}