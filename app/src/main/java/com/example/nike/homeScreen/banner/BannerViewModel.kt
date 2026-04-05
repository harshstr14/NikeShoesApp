package com.example.nike.homeScreen.banner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nike.homeScreen.Shoe

class BannerViewModel : ViewModel() {
    private val repository = BannerRepository()

    private val _banners = MutableLiveData<List<Shoe>>()
    val banners: LiveData<List<Shoe>> = _banners

    private val _bannerImages = MutableLiveData<List<String>>()
    val bannerImages: LiveData<List<String>> = _bannerImages

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchBanners() {
        _loading.value = true

        repository.getBanners(
            onSuccess = { shoes, images ->
                _banners.value = shoes
                _bannerImages.value = images
                _loading.value = false
            },
            onFailure = {
                _loading.value = false
            }
        )
    }
}