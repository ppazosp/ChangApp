package ppazosp.changapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import ppazosp.changapp.databinding.ActivityMainBinding

val supabase = createSupabaseClient(
    supabaseUrl = "https://mapvepqvdgagccguault.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1hcHZlcHF2ZGdhZ2NjZ3VhdWx0Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcyNzAyNjQ1NSwiZXhwIjoyMDQyNjAyNDU1fQ.WT754nds11_TpqM3by8cAbbFzSjTK-yybYU3SPDalGw"
) {
    install(Postgrest)
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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