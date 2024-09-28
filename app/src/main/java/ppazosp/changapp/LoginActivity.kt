package ppazosp.changapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ppazosp.changapp.databinding.ActivityLoginBinding
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class LoginActivity : AppCompatActivity() {

    private val SALT_LENGTH: Int = 16
    private val HASH_ITERATIONS: Int = 10000
    private val HASH_LENGTH: Int = 256

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

            val isPasswordValid = validatePassword(passwordEntered, storedHashedPassword)

            if (isPasswordValid) {
                showMainActivity()
            } else {
                Toast.makeText(this, "email o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
            Toast.makeText(this, "email o contraseña incorrectos", Toast.LENGTH_LONG).show()
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

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    private fun hashPassword(password: String, salt: ByteArray?): String {

        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        val hash = factory.generateSecret(spec).encoded

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder()
            .encodeToString(hash)
    }

    private fun validatePassword(enteredPassword: String?, storedPassword: String): Boolean {

        val parts = storedPassword.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val salt = Base64.getDecoder().decode(parts[0])

        val enteredHash = hashPassword(enteredPassword!!, salt)

        return enteredHash == storedPassword
    }
}