package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import ppazosp.changapp.databinding.ActivityMainBinding

lateinit var myUser: User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initializeVars()

        setUpViewPager()
    }

    private fun initializeVars()
    {
        viewPager = binding.viewpager
        bottomNav = binding.bottomNavBar
    }


    private fun setUpViewPager() {

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        bottomNav.setOnItemSelectedListener { item ->
            when (item) {
                R.id.search -> {
                    viewPager.currentItem = 0
                }
                R.id.profile -> {
                    viewPager.currentItem = 1
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNav.setItemSelected(R.id.search)
                    1 -> bottomNav.setItemSelected(R.id.profile)
                }
            }
        })
    }

    fun showLoginActivity()
    {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}