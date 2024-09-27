package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
import ppazosp.changapp.databinding.ActivityMainBinding


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

    }

    private suspend fun checkLogin()
    {
        val email = emailView?.text?.toString()?.trim() ?: ""
        val password = passwordView?.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "email o contraseña vacíos", Toast.LENGTH_LONG).show()
            return
        }

        try {
            supabase.from("users").select{
                filter {
                    User::email eq email
                    and { User::password eq password }
                }}.decodeSingle<User>()
        }catch (e:Exception){
            Log.e("Exception", e.message.toString())
            Toast.makeText(this, "email o contraseña incorrectos", Toast.LENGTH_LONG).show()
            return
        }
            showMainActivity()
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