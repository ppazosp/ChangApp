package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ppazosp.changapp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var emailView: TextInputEditText? = null
    private var passwordView: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        emailView = binding.emailInput
        passwordView = binding.passwordInput

        val loginButton = binding.loginButton
        val registerButton = binding.registerButton

        loginButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch { checkLogin() }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private suspend fun checkLogin()
    {

        val email = emailView?.text?.toString()?.trim() ?: ""
        val passwordEntered = passwordView?.text?.toString()?.trim() ?: ""


        if (email.isEmpty() || passwordEntered.isEmpty()) {
            Toast.makeText(this, "email o contraseña vacíos", Toast.LENGTH_LONG).show()
            return
        }

        try {

            val user = supabase.from("users").select {
                filter {
                    User::email eq email
                }
            }.decodeSingle<User>()

            val storedHashedPassword = user.password

            val isPasswordValid = Encripter.validatePassword(passwordEntered, storedHashedPassword)

            if (isPasswordValid) {
                showMainActivity()
            } else {
                Toast.makeText(this, "email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
            Toast.makeText(this, "email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
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