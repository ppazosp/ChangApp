package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ppazosp.changapp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var emailView: TextInputEditText
    private lateinit var passwordView: TextInputEditText

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LoadingScreen.show(this )

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initializeVars()

        setOnClickListeners()

        LoadingScreen.hide()
    }

    private fun initializeVars()
    {
        emailView = binding.emailInput
        passwordView = binding.passwordInput

        loginButton = binding.loginButton
        registerButton = binding.registerButton
    }

    private fun setOnClickListeners()
    {
        loginButton.setOnClickListener {
            LoadingScreen.show(this@LoginActivity)
            val email = emailView.text.toString().trim()
            val password = passwordView.text.toString().trim()
            CoroutineScope(Dispatchers.Main).launch { checkLogin(this@LoginActivity, LoginUser(email, password)) }
            LoadingScreen.hide()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showMainActivity()
    {
        val intent = Intent(
            this@LoginActivity,
            MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}