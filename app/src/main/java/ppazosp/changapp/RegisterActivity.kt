package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import kotlinx.serialization.Serializable
import ppazosp.changapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var emailView: TextInputEditText
    private lateinit var fullnameView: TextInputEditText
    private lateinit var socialsView: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private lateinit var password2View: TextInputEditText

    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initializeVars()

        setOnClickListeners()
    }

    private fun initializeVars()
    {
        emailView = binding.emailInput
        fullnameView = binding.fullnameInput
        socialsView = binding.socialsInput
        passwordView = binding.passwordInput
        password2View = binding.passwordInput2

        registerButton = binding.registerButton
    }

    private fun setOnClickListeners()
    {
        registerButton.setOnClickListener {

            LoadingScreen.show(this@RegisterActivity)

            val email = emailView.text.toString()
            val fullname = fullnameView.text.toString()
            val socials = socialsView.text.toString()
            val password = passwordView.text.toString()
            val password2 = password2View.text.toString()

            if (email.isEmpty() || fullname.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                ErrorHandler.showError(this, "Hay campos vacíos")
                return@setOnClickListener
            }

            if (password != password2) {
                ErrorHandler.showError(this, "Las contraseñas no coinciden")
                return@setOnClickListener
            }

            val hashedPassword = Encripter.hashPassword(password, Encripter.generateSalt())

            val user = InsertionUser( email, fullname, socials, hashedPassword )
            CoroutineScope(Dispatchers.Main).launch {

                if (registerUser(this@RegisterActivity, user)) showMainActivity()

                withContext(Dispatchers.Main) { LoadingScreen.hide() }
            }
        }
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