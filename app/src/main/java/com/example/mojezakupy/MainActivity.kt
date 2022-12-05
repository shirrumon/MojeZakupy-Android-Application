package com.example.mojezakupy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.fragments.ListFragment
import com.example.mojezakupy.fragments.ArchiveListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val listFragment = ListFragment()
    private val archiveListFragment = ArchiveListFragment()
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
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}