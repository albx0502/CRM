package com.example.crm.data.models

import com.example.crm.R

data class User(
    val name: String,
    val username: String,
    val email: String,
    val profileImage: Int = R.drawable.ic_profile_avatar // Imagen por defecto
)
