package com.example.mojezakupy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.fragments.pages.ListFragment
import com.example.mojezakupy.fragments.ArchiveListFragment
import com.example.mojezakupy.fragments.InfographicsFragment
import com.example.mojezakupy.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val archiveListFragment = ArchiveListFragment()
    private val infoGraphicsFragment = InfographicsFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var appDatabase: AppDatabase

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
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}