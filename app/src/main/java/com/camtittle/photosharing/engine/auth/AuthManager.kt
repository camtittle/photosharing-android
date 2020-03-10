package com.camtittle.photosharing.engine.auth

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.*
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import com.amazonaws.mobile.client.results.SignUpResult
import com.camtittle.photosharing.engine.auth.model.ConfirmResponse
import com.camtittle.photosharing.engine.auth.model.SignInResponse
import com.camtittle.photosharing.engine.auth.model.SignUpResponse
import com.camtittle.photosharing.engine.common.async.CallbackError
import com.camtittle.photosharing.engine.common.async.ServiceCallback
import java.lang.Exception
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails

object AuthManager {

    private val instance: AWSMobileClient = AWSMobileClient.getInstance()

    private val tag = AuthManager::class.java.name

    private var signOutListener: () -> Unit = {}

    var userId: String? = null
        private set

    fun init(context: Context) {
        instance.initialize(context, object : Callback<UserStateDetails?> {
            override fun onResult(result: UserStateDetails?) {

                if (result?.userState != UserState.SIGNED_IN) {
                    signOutListener()
                } else {
                    // Sometimes AWS SDK returns SignedIn state even when tokens are invalid
                    try {
                        val token = instance.tokens.idToken.tokenString
                        Log.d(tag, token)
                    } catch (e: Exception) {
                        // If no token available, go to signIn
                        signOutListener()
                    }
                }
            }

            override fun onError(e: Exception?) {
                Log.e("AWSINIT", "onError: ", e)
            }
        })

        instance.addUserStateListener {
            instance.currentUserState().userState.let {
                if (it != UserState.SIGNED_IN) {
                    signOutListener()
                } else {
                    refreshUserAttributes()
                }

            }

        }
    }

    fun signUp(email: String, password: String, callback: ServiceCallback<SignUpResponse>) {
        // We are using email as username here, but also add email as a user attribute
        // in case other forms of username are used in the future
        val attributes = HashMap<String, String>()
        attributes["email"] = email
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
        val state = instance.currentUserState().userState == UserState.SIGNED_IN
        Log.d(tag, "isSignedIn: $state")
        return state
    }

    fun signIn(username: String, password: String, callback: ServiceCallback<SignInResponse>) {
        instance.signIn(username, password, null, object : Callback<SignInResult> {
            override fun onResult(result: SignInResult?) {
                if (result?.signInState == SignInState.DONE) {
                    Log.d(tag, instance.tokens.idToken.tokenString)
                    callback.onSuccess(SignInResponse())
                } else {
                    callback.onError(CallbackError("Sign In state was not 'DONE'"))
                }
            }

            override fun onError(e: Exception?) {
                callback.onError(CallbackError(e?.message, e))
            }
        })
    }

    fun signOut() {
        Log.d(tag, "signOut")
        instance.signOut()
    }

    fun getIdToken(): String {
        return try {
            instance.tokens.idToken.tokenString
        } catch (ex: Exception) {
            signOutListener()
            ""
        }
    }

    private fun refreshUserAttributes() {
        instance.getUserAttributes(object : Callback<Map<String, String>> {
            override fun onError(e: Exception?) {
                Log.e(tag, "Error fetching user attributes")
            }

            override fun onResult(result: Map<String, String>?) {
                result?.let {
                    if (it.containsKey("sub")) {
                        userId = it["sub"]
                    }
                }
            }
        })
    }

    fun setSignOutListener(listener: () -> Unit) {
        userId = null
        signOutListener = listener
    }

}