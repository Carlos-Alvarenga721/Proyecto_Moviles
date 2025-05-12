package com.example.proyecto_kotlin_dsm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Asegura que el ícono hamburguesa sea visible
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configura el botón hamburguesa y lo enlaza con el DrawerLayout
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)

        // Navegación del menú
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    if (this !is DashboardActivity) {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    true
                }
                R.id.nav_materias -> {
                    if (this !is MateriasActivity) {
                        startActivity(Intent(this, MateriasActivity::class.java))
                    }
                    true
                }
                R.id.nav_evaluaciones -> {
                    if (this !is ActivitiesActivity) {
                        startActivity(Intent(this, ActivitiesActivity::class.java))
                    }
                    true
                }
                R.id.nav_perfil -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState() // Sincroniza el botón hamburguesa
    }

    override fun setContentView(layoutResID: Int) {
        val contentFrame: FrameLayout = findViewById(R.id.contentFrame)
        val view = LayoutInflater.from(this).inflate(layoutResID, contentFrame, false)
        contentFrame.addView(view)
    }
}
