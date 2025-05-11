package com.example.proyecto_kotlin_dsm

import Perfil
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : BaseActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var emailText: TextView
    private lateinit var nameField: EditText
    private lateinit var universityField: EditText
    private lateinit var phoneField: EditText
    private lateinit var saveButton: Button
    private lateinit var selectAvatarButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var uid: String

    private var avatarResName = "avatar1" // Avatar por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar vistas
        profileImage = findViewById(R.id.profileImage)
        emailText = findViewById(R.id.emailText)
        nameField = findViewById(R.id.nameField)
        universityField = findViewById(R.id.universityField)
        phoneField = findViewById(R.id.phoneField)
        saveButton = findViewById(R.id.saveButton)
        selectAvatarButton = findViewById(R.id.selectAvatarButton)

        // Firebase
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        uid = user?.uid ?: return
        emailText.text = user.email
        database = FirebaseDatabase.getInstance().getReference("usuarios").child(uid)

        cargarDatos()

        saveButton.setOnClickListener {
            guardarDatos()
        }

        selectAvatarButton.setOnClickListener {
            mostrarDialogoAvatares()
        }
    }

    private fun cargarDatos() {
        database.child("perfil").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val perfil = snapshot.getValue(Perfil::class.java)
                perfil?.let {
                    nameField.setText(it.nombreCompleto)
                    universityField.setText(it.universidad)
                    phoneField.setText(it.telefono)
                    avatarResName = it.avatarResName ?: "avatar1"
                    mostrarAvatar(avatarResName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarDatos() {
        val perfil = Perfil(
            nombreCompleto = nameField.text.toString(),
            universidad = universityField.text.toString(),
            telefono = phoneField.text.toString(),
            avatarResName = avatarResName
        )

        database.child("perfil").setValue(perfil).addOnSuccessListener {
            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarAvatar(resName: String) {
        val resId = resources.getIdentifier(resName, "drawable", packageName)
        if (resId != 0) {
            profileImage.setImageResource(resId)
        }
    }

    private fun mostrarDialogoAvatares() {
        val avatares = listOf("avatar1", "avatar2", "avatar3")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un avatar")
        builder.setItems(avatares.toTypedArray()) { _, which ->
            avatarResName = avatares[which]
            mostrarAvatar(avatarResName)
        }
        builder.show()
    }
}
