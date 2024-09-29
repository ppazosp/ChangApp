package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var emailView: TextView
    private lateinit var fullnameView: EditText
    private lateinit var socialsView: EditText
    private lateinit var passwordView: EditText

    private lateinit var saveButton: Button

    private lateinit var deleteFAB: FloatingActionButton
    private lateinit var logoutFAB: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        LoadingScreen.show(requireContext())

        initializeVars(view)

        setUI()

        setOnClickListeners()

        LoadingScreen.hide()

        return view
    }

    private fun initializeVars(view: View)
    {
        saveButton = view.findViewById(R.id.save_button)

        deleteFAB = view.findViewById(R.id.fab_delete)
        logoutFAB = view.findViewById(R.id.fab_logout)

        emailView = view.findViewById(R.id.email_view)
        fullnameView = view.findViewById(R.id.fullname_view)
        socialsView = view.findViewById(R.id.socials_view)
        passwordView = view.findViewById(R.id.password_view)

    }

    private fun setUI()
    {
        emailView.text = myUser.email
        fullnameView.setText(myUser.fullname)
        socialsView.setText(myUser.socials)
        passwordView.setText(myUser.password)
    }

    private fun setOnClickListeners()
    {
        saveButton.setOnClickListener {

            if(hasFullnameChanged() || hasSocialsChanged() || hasPasswordChanged())
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main)
                    {
                        LoadingScreen.show(requireContext())
                    }
                    updateUser()
                    withContext(Dispatchers.Main)
                    {
                        LoadingScreen.hide()
                    }
                }
            else ErrorHandler.showError(requireContext(), "No hay cambios que guardar")

        }

        deleteFAB.setOnClickListener{
            val dialogDelete = DeleteUserDialog()
            dialogDelete.show(childFragmentManager, "Eliminar usuario")
        }

        logoutFAB.setOnClickListener{
            (activity as MainActivity).showLoginActivity()
        }
    }


    private fun hasFullnameChanged(): Boolean
    {
        return myUser.fullname != fullnameView.text.toString()
    }

    private fun hasSocialsChanged(): Boolean
    {
        return myUser.socials!! != socialsView.text.toString()
    }

    private fun hasPasswordChanged(): Boolean
    {
        return myUser.password != passwordView.text.toString()
    }

    private suspend fun updateUser()
    {

        supabase.from("users").update(
            {
                if (hasFullnameChanged()) User::fullname setTo fullnameView.text.toString()
                if (hasSocialsChanged()) User::socials setTo socialsView.text.toString()
                if (hasPasswordChanged()) User::password setTo Encripter.hashPassword(passwordView.text.toString(), Encripter.generateSalt())
            }
        ) {
            filter {
                User::id eq myUser.id
            }
        }
    }
}