package com.camtittle.photosharing.ui.feed

import androidx.lifecycle.ViewModel
import com.camtittle.photosharing.engine.auth.AuthManager

class FeedViewModel : ViewModel() {

    fun isSignedIn(): Boolean {
        return AuthManager.isSignedIn()
    }

}
