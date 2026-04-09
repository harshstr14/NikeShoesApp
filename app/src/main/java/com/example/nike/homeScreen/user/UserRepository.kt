package com.example.nike.homeScreen.user

import com.example.nike.homeScreen.Shoe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun addToCart(item: Shoe): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users").child(userId)
            .child("MyCart").child(item.id.toString())

        ref.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                ref.setValue(item).addOnSuccessListener {
                    trySend("Added to cart")
                }.addOnFailureListener {
                    trySend("Failed: ${it.message}")
                }
            } else {
                trySend("Already in cart")
            }
        }.addOnFailureListener {
            trySend("Error: ${it.message}")
        }

        awaitClose {}
    }

    fun toggleFavorite(item: Shoe): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users").child(userId)
            .child("Favourite").child(item.id.toString())

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                ref.removeValue()
                trySend("Removed from favourite")
            } else {
                ref.setValue(item)
                trySend("Added to favourite")
            }
        }.addOnFailureListener {
            trySend("Error: ${it.message}")
        }

        awaitClose {}
    }

    fun observeFavorite(shoeId: String): Flow<Boolean> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users").child(userId)
            .child("Favourite").child(shoeId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    fun getFavorites(): Flow<List<Shoe>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@callbackFlow

        val ref = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(userId)
            .child("Favourite")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Shoe>()

                for (child in snapshot.children) {
                    val item = child.getValue(Shoe::class.java)
                    item?.let { list.add(it) }
                }

                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    fun getCartItems(): Flow<List<Shoe>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users")
            .child(userId)
            .child("MyCart")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Shoe>()

                for (child in snapshot.children) {
                    val item = child.getValue(Shoe::class.java)
                    item?.let { list.add(it) }
                }

                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    fun removeFromCart(itemId: String): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users")
            .child(userId)
            .child("MyCart")
            .child(itemId)

        ref.removeValue()
            .addOnSuccessListener {
                trySend("Item removed from cart")
            }
            .addOnFailureListener {
                trySend("Failed: ${it.message}")
            }

        awaitClose {}
    }

    fun decreaseQuantity(itemId: String): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users")
            .child(userId)
            .child("MyCart")
            .child(itemId)

        ref.get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(Shoe::class.java)

            if (item != null) {
                val currentQty = item.quantity ?: 1

                if (currentQty > 1) {
                    ref.child("quantity").setValue(currentQty - 1)
                    trySend("Quantity decreased")
                } else {
                    ref.removeValue()
                    trySend("Item removed from cart")
                }
            }

        }.addOnFailureListener {
            trySend("Error: ${it.message}")
        }

        awaitClose {}
    }

    fun increaseQuantity(item: Shoe): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val ref = db.child("Users")
            .child(userId)
            .child("MyCart")
            .child(item.id.toString())

        ref.get().addOnSuccessListener { snapshot ->
            val existing = snapshot.getValue(Shoe::class.java)

            val updatedQty = (existing?.quantity ?: 1) + 1
            ref.child("quantity").setValue(updatedQty)

            trySend("Quantity increased")
        }

        awaitClose {}
    }
}