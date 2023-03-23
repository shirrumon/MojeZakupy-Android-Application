package com.mainApp.mojezakupy

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.mainApp.mojezakupy.database.AppDatabase
import com.mainApp.mojezakupy.fragments.pages.ListFragment
import com.mainApp.mojezakupy.fragments.pages.ArchiveListFragment
import com.mainApp.mojezakupy.fragments.InfographicsFragment
import com.mainApp.mojezakupy.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val archiveListFragment = ArchiveListFragment()
    private val infoGraphicsFragment = InfographicsFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.mainApp.mojezakupy.R.layout.activity_main)
        appDatabase = AppDatabase.getDatabase(this)

        replaceFragment(listFragment)

        findViewById<BottomNavigationView>(com.mainApp.mojezakupy.R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.mainApp.mojezakupy.R.id.ic_dashboard -> replaceFragment(listFragment)
                com.mainApp.mojezakupy.R.id.ic_list_archive -> replaceFragment(archiveListFragment)
                com.mainApp.mojezakupy.R.id.ic_list_infographics -> replaceFragment(infoGraphicsFragment)
//                R.id.ic_setting_page -> replaceFragment(settingsFragment)
            }
            true
        }

//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA),
//                101
//            )
//        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.replace(com.mainApp.mojezakupy.R.id.fragment_container, fragment)
        transaction.commit()
    }
}