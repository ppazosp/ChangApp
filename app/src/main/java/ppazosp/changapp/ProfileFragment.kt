package ppazosp.changapp

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

class ProfileFragment : Fragment() {

    private var emailView: TextView? = null
    private var fullnameView: EditText? = null
    private var socialsView: EditText? = null
    private var passwordView: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val saveButton = view.findViewById<Button>(R.id.save_button)

        val deleteFAB = view.findViewById<FloatingActionButton>(R.id.fab_delete)
        val logoutFAB = view.findViewById<FloatingActionButton>(R.id.fab_logout)

        emailView = view.findViewById(R.id.email_view)
        fullnameView = view.findViewById(R.id.fullname_view)
        socialsView = view.findViewById(R.id.socials_view)
        passwordView = view.findViewById(R.id.password_view)

        emailView?.text = myUser.email
        fullnameView?.setText(myUser.fullname)
        socialsView?.setText(myUser.socials)
        passwordView?.setText(myUser.password)

        saveButton.setOnClickListener {

            if(hasFullnameChanged() || hasSocialsChanged() || hasPasswordChanged())
                CoroutineScope(Dispatchers.Main).launch { updateUser() }

        }

        deleteFAB.setOnClickListener{
            //delete user and open login window
        }

        logoutFAB.setOnClickListener{
            //open login window
        }



        return view
    }



    private fun hasFullnameChanged(): Boolean
    {
        return !myUser.fullname.equals(fullnameView!!.text)
    }

    private fun hasSocialsChanged(): Boolean
    {
        return !myUser.socials.equals(socialsView!!.text)
    }

    private fun hasPasswordChanged(): Boolean
    {
        return !myUser.password.equals(passwordView!!.text)
    }

    private suspend fun updateUser()
    {
        supabase.from("users").update(
            {
                User::fullname setTo fullnameView!!.text.toString()
                User::socials setTo socialsView!!.text.toString()
                User::password setTo passwordView!!.text.toString()
            }
        ) {
            filter {
                User::id eq myUser.id
            }
        }
    }
}