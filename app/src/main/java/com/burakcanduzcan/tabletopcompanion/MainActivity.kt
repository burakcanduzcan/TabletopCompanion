package com.burakcanduzcan.tabletopcompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.burakcanduzcan.tabletopcompanion.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            binding.drawerLayout.open()
            Timber.i("Navigation icon clicked")
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    // Handle more item (inside overflow menu) press
                    Timber.i("Settings icon clicked")
                    true
                }
                else -> false
            }
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            binding.drawerLayout.close()
            true
        }
    }

    fun openDrawerLayout() {
        binding.drawerLayout.open()
    }
}