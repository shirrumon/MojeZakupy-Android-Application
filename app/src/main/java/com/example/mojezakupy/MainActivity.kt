package com.example.mojezakupy

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.NumberFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.fragments.pages.ListFragment
import com.example.mojezakupy.fragments.pages.ArchiveListFragment
import com.example.mojezakupy.fragments.InfographicsFragment
import com.example.mojezakupy.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val archiveListFragment = ArchiveListFragment()
    private val infoGraphicsFragment = InfographicsFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var appDatabase: AppDatabase

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appDatabase = AppDatabase.getDatabase(this)

        replaceFragment(listFragment)

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_dashboard -> replaceFragment(listFragment)
                R.id.ic_list_archive -> replaceFragment(archiveListFragment)
                R.id.ic_list_infographics -> replaceFragment(infoGraphicsFragment)
                R.id.ic_setting_page -> replaceFragment(settingsFragment)
            }
            true
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    companion object {
        @SuppressLint("ConstantLocale")
        @RequiresApi(Build.VERSION_CODES.N)
        val currencyLocalSymbol = NumberFormat.getCurrencyInstance(Locale.getDefault()).currency?.symbol.toString()
    }
}