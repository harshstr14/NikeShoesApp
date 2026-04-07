package com.example.nike.homeScreen.shoes

import com.example.nike.homeScreen.Shoe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShoesRepository {
    private val database = FirebaseDatabase.getInstance().getReference()

    fun getShoesByCategory(
        category: String,
        onSuccess: (List<Shoe>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.child(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val shoesList = mutableListOf<Shoe>()

                    for (shoeSnapshot in snapshot.children) {

                        val id = shoeSnapshot.child("id").getValue(Int::class.java) ?: 0
                        val name = shoeSnapshot.child("name").getValue(String::class.java) ?: ""
                        val description = shoeSnapshot.child("description").getValue(String::class.java) ?: ""
                        val imageURL = shoeSnapshot.child("imageURL").getValue(String::class.java) ?: ""
                        val price = shoeSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                        val type = shoeSnapshot.child("type").getValue(String::class.java) ?: ""

                        val productDetails = mutableListOf<String>()
                        val detailsSnapshot = shoeSnapshot.child("productDetails")

                        for (detail in detailsSnapshot.children) {
                            detail.getValue(String::class.java)?.let {
                                productDetails.add(it)
                            }
                        }

                        val shoeImages = mutableListOf<String>()
                        val shoeImagesSnapshot = shoeSnapshot.child("shoeImages")

                        for (shoeImage in shoeImagesSnapshot.children) {
                            shoeImage.getValue(String::class.java)?.let {
                                shoeImages.add(it)
                            }
                        }

                        val shoe = Shoe(
                            id = id,
                            name = name,
                            description = description,
                            imageURL = imageURL,
                            price = price,
                            type = type,
                            productDetails = productDetails,
                            shoeImages = shoeImages
                        )

                        shoesList.add(shoe)
                    }

                    onSuccess(shoesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(Exception(error.message))
                }
            })
    }
}