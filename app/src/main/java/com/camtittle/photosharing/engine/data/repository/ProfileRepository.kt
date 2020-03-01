package com.camtittle.photosharing.engine.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.camtittle.photosharing.engine.common.result.Result
import com.camtittle.photosharing.engine.data.model.Profile
import com.camtittle.photosharing.engine.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProfileRepository {

    private val TAG = ProfileRepository::class.java.name

    fun getProfiles(userIds: List<String>): LiveData<Result<Map<String, Profile>>> {

        val commaSeparatedIds = userIds.joinToString(",")
        Log.d(TAG, "Fetching profiles for user IDs $commaSeparatedIds")
        val liveData = MutableLiveData<Result<Map<String, Profile>>>()

        ApiService.api.getProfiles(commaSeparatedIds).enqueue(object : Callback<Map<String, Profile>> {

            override fun onFailure(call: Call<Map<String, Profile>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch profiles for user IDs: $commaSeparatedIds")
                liveData.postValue(Result.error(t.message ?: "Error fetching profile"))
            }

            override fun onResponse(call: Call<Map<String, Profile>>,
                                    response: Response<Map<String, Profile>>) {
                response.body().let {
                    if (it == null) {
                        liveData.postValue(Result.error("Error fetching profiles. Status code: ${response.code()}"))
                    } else {
                        liveData.postValue(Result.success(it))
                    }
                }
            }
        })

        return liveData
    }

    fun getProfile(userId: String): LiveData<Result<Profile>> {
        Log.d(TAG, "Fetching profile for user ID $userId")
        val liveData = MutableLiveData<Result<Profile>>()

        ApiService.api.getProfile(userId).enqueue(object : Callback<Profile> {

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.e(TAG, "Failed to fetch profile for user ID: $userId")
                liveData.postValue(Result.error(t.message ?: "Error fetching profile"))
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                response.body().let {
                    if (it == null) {
                        liveData.postValue(Result.error("Error fetching profile. Status code: ${response.code()}"))
                    } else {
                        liveData.postValue(Result.success(it))
                    }
                }
            }
        })

        return liveData
    }

}