package com.example.droidfakecamera

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.droidfakecamera.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.viewPager.adapter = MainPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_sources)
                else -> getString(R.string.tab_module)
            }
        }.attach()

        onBackPressedDispatcher.addCallback(this) {
            if (binding.viewPager.currentItem == 0) {
                finish()
            } else {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
            }
        }
    }
}
