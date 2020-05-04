package com.camtittle.photosharing.ui.auth

import android.util.Log
import androidx.databinding.ObservableBoolean
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
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.common.result.ResultEvent
import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.ApiService
import com.camtittle.photosharing.engine.data.network.model.UpdateProfileRequest
import com.camtittle.photosharing.engine.data.repository.ProfileRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    val model = AuthModel()

    private val _signUpResponse = MutableLiveData<ResultEvent<SignUpResponse>>()
    val signUpResponse: LiveData<ResultEvent<SignUpResponse>> = _signUpResponse

    private val _signInResponse = MutableLiveData<ResultEvent<SignInResponse>>()
    val signInResponse: LiveData<ResultEvent<SignInResponse>> = _signInResponse

    private val _saveProfileDetailsResponse = MutableLiveData<Event<Unit>>()
    val saveProfileDetailsResponse: LiveData<Event<Unit>> = _saveProfileDetailsResponse

    val tag = AuthViewModel::class.java.name

    var loading = ObservableBoolean(false)

    fun signUp() {
        val email = model.email
        val password = model.password
        if (email.isBlank() || password.isBlank()) {
            _signUpResponse.postValue(ResultEvent.error("Email and password required"))
            return
        }

        AuthManager.signUp(email, password, object : ServiceCallback<SignUpResponse> {
            override fun onError(error: CallbackError) {
                Log.d(tag, "ERROR. " + error.msg)
                _signUpResponse.postValue(ResultEvent.error(error.msg ?: "Something went wrong. Please try again"))
            }

            override fun onSuccess(response: SignUpResponse) {
                Log.d(tag, "SUCCESS. Confirmation sent to:" + response.confirmationDestination)
                _signUpResponse.postValue(ResultEvent.success(response))
            }

        })
    }

    fun confirmAccount() {
        val email = model.email
        val code = model.confirmationCode
        if (email.isNotEmpty() && code.isNotEmpty()) {
            Log.d(tag, "Confirming $email with code: $code")
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

    fun refreshProfileData(): LiveData<Result<Profile>>? {
        AuthManager.userId?.let { userId ->
            return ProfileRepository.getProfile(userId)
        }

        return null
    }

    fun saveProfileDetails() {
        val name = model.name
        val token = AuthManager.getIdToken()
        Log.d(tag, "Saving profile details with name: $name")
        if (name.isNotEmpty()) {
            loading.set(true)
            val updateRequest = UpdateProfileRequest(name)
            ApiService.api.updateProfile(token, updateRequest).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(tag, "Error saving profile details " + t.message)
                    loading.set(false)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d(tag, "Successfully saved profile details")
                    _saveProfileDetailsResponse.postValue(Event(Unit))
                    model.clear()
                }

            })
        }
    }

    fun signIn() {
        val email = model.email
        val password = model.password
        if (email.isBlank() || password.isBlank()) {
            _signInResponse.postValue(ResultEvent.error("Email and password required"))
            return
        }

        AuthManager.signIn(email, password, object : ServiceCallback<SignInResponse> {
            override fun onSuccess(response: SignInResponse) {
                _signInResponse.postValue(ResultEvent.success(response))
                if (response.confirmed) {
                    model.clear()
                }
            }

            override fun onError(error: CallbackError) {
                Log.e(tag, error.msg?: "Something went wrong with sign in")
                _signInResponse.postValue(ResultEvent.error(error.msg ?: "Something went wrong signing you in"))
            }

        })
    }
}