package ppazosp.changapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ppazosp.changapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpViewPager()
    }


    private fun setUpViewPager() {

        val viewPager = binding.viewpager
        val bottomNav = binding.bottomNavBar

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        bottomNav.setOnItemSelectedListener { item ->
            when (item) {
                R.id.search -> {
                    viewPager.currentItem = 0
                }
                R.id.chats -> {
                    viewPager.currentItem = 1
                }
                R.id.profile -> {
                    viewPager.currentItem = 2
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNav.setItemSelected(R.id.search)
                    1 -> bottomNav.setItemSelected(R.id.chats)
                    2 -> bottomNav.setItemSelected(R.id.profile)
                }
            }
        })
    }
}