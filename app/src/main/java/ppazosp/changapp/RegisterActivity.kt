package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ppazosp.changapp.databinding.ActivityRegisterBinding

@Serializable
private data class InsertionUser(
    val email: String,
    val fullname: String,
    val socials: String?,
    val password: String,
)

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val emailView = binding.emailInput
        val fullnameView = binding.fullnameInput
        val socialsView = binding.socialsInput
        val passwordView = binding.passwordInput
        val password2View = binding.passwordInput2

        val registerButton = binding.registerButton

        registerButton.setOnClickListener {
            val email = emailView.text.toString()
            val fullname = fullnameView.text.toString()
            val socials = socialsView.text.toString()
            val password = passwordView.text.toString()
            val password2 = passwordView.text.toString()

            if (email.isEmpty() || fullname.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Error: Hay campos vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password2) {
                Toast.makeText(this, "Error: Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = Encripter.hashPassword(password, Encripter.generateSalt())

            val user = InsertionUser( email, fullname, socials, hashedPassword )
            CoroutineScope(Dispatchers.Main).launch { registerUser(user) }
        }
    }

    private suspend fun registerUser(user: InsertionUser) {
        try {
            supabase.from("users").insert(user)
        } catch (e: Exception) {
            if (e.message?.contains("duplicate key value", ignoreCase = true) == true) {
                Toast.makeText(this, "Error: Este correo ya está registrado", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("Exception", e.message.toString())
                Toast.makeText(this, "Error: No se pudo registrar el usuario", Toast.LENGTH_SHORT).show()
            }
            return
        }

        Toast.makeText(this, "!Has sido registrado con éxito!", Toast.LENGTH_SHORT).show()
        showMainActivity()
    }

    private fun showMainActivity()
    {
        val intent = Intent(
            this@RegisterActivity,
            MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}