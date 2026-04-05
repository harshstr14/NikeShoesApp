package com.example.nike.homeScreen.banner

import com.example.nike.homeScreen.Shoe
import com.google.firebase.database.FirebaseDatabase

class BannerRepository {
    private val bannerRef = FirebaseDatabase.getInstance().getReference("Banner")

    fun getBanners(
        onSuccess: (List<Shoe>, List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        bannerRef.get()
            .addOnSuccessListener { snapshot ->

                val bannerList = mutableListOf<Shoe>()
                val bannerImages = mutableListOf<String>()

                if (snapshot.exists()) {
                    for (shoeSnapshot in snapshot.children) {

                        val id = shoeSnapshot.child("id").getValue(Int::class.java) ?: 0
                        val name = shoeSnapshot.child("name").getValue(String::class.java) ?: ""
                        val description = shoeSnapshot.child("description").getValue(String::class.java) ?: ""
                        val imageURL = shoeSnapshot.child("imageURL").getValue(String::class.java) ?: ""
                        val price = shoeSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                        val type = shoeSnapshot.child("type").getValue(String::class.java) ?: ""
                        val bannerImage = shoeSnapshot.child("bannerURL").getValue(String::class.java) ?: ""

                        bannerImages.add(bannerImage)

                        val productDetails = mutableListOf<String>()
                        val detailsSnapshot = shoeSnapshot.child("productDetails")

                        for (detail in detailsSnapshot.children) {
                            detail.getValue(String::class.java)?.let {
                                productDetails.add(it)
                            }
                        }

                        val shoe = Shoe(
                            id = id,
                            name = name,
                            description = description,
                            imageURL = imageURL,
                            price = price,
                            type = type,
                            productDetails = productDetails
                        )

                        bannerList.add(shoe)
                    }
                }

                onSuccess(bannerList, bannerImages)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}