package ppazosp.changapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import ppazosp.changapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SearchFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId)
            {
                R.id.search -> replaceFragment(SearchFragment())
                R.id.chats -> replaceFragment(ChatsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {

                }
            }
            true

        }
    }

    private fun replaceFragment( fragment : Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}