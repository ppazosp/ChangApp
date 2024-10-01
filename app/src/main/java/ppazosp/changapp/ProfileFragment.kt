package ppazosp.changapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(), OnDialogDismissedListener {

    private lateinit var emailView: TextView
    private lateinit var fullnameView: EditText
    private lateinit var socialsView: EditText
    private lateinit var passwordView: EditText

    private lateinit var saveButton: Button

    private lateinit var deleteFAB: FloatingActionButton
    private lateinit var logoutFAB: FloatingActionButton

    private lateinit var userAdvertsLayout: FrameLayout
    private lateinit var userAdvertsContainer: LinearLayout

    override fun onDialogDismissed() {
        CoroutineScope(Dispatchers.Main).launch { retrieveUserAdverts(true) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        LoadingScreen.show(requireActivity())

        initializeVars(view)

        setUI()

        setOnClickListeners()

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

        userAdvertsLayout = view.findViewById(R.id.user_adverts_layout)
        userAdvertsContainer = view.findViewById(R.id.linear_layout)

    }

    private fun setUI()
    {
        emailView.text = myUser.email
        fullnameView.setText(myUser.fullname)
        socialsView.setText(myUser.socials)
        passwordView.setText(myUser.password)

        CoroutineScope(Dispatchers.Main).launch{ retrieveUserAdverts(false) }

    }

    private fun setOnClickListeners()
    {
        saveButton.setOnClickListener {

            if(hasFullnameChanged() || hasSocialsChanged() || hasPasswordChanged())
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main)
                    {
                        LoadingScreen.show(requireActivity())
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

    private suspend fun retrieveUserAdverts(refresh: Boolean)
    {
        if (refresh)
        {
            withContext(Dispatchers.Main)
            {
                LoadingScreen.show(requireActivity())
            }
        }

        userAdvertsContainer.removeAllViews()

        val adverts: List<Advert> = fetchUserAdverts(requireActivity(), myUser.id!!)

        if (adverts.isEmpty()) {
            userAdvertsLayout.visibility = View.GONE
            withContext(Dispatchers.Main)
            {
                LoadingScreen.hide()
            }
            return
        }

        for(advert in adverts)
        {
            val place = fetchPlace(advert.place)
            val type = fetchType(advert.type)

            val resultView = LayoutInflater.from(context).inflate(R.layout.result_item, userAdvertsContainer, false)

            val userView = resultView.findViewById<TextView>(R.id.userID)

            val titleView = resultView.findViewById<TextView>(R.id.title_view)
            val typePlaceView = resultView.findViewById<TextView>(R.id.type_place_view)
            val picView = resultView.findViewById<ImageView>(R.id.pic)
            val dateView = resultView.findViewById<TextView>(R.id.dateView)

            val frameLayout = resultView.findViewById<FrameLayout>(R.id.linearLayout)

            userView.text = advert.user.toString()

            titleView.text = advert.title
            val typePlace = "${place.name} - ${type.name}"
            typePlaceView.text = typePlace
            if (advert.image != null ) Encripter.setImageFromBase64(picView, advert.image)
            dateView.text = advert.date

            frameLayout.setOnClickListener {
                val dialogDelete = DeleteAdvertDialog.newInstance(myUser.id!!, advert.place, advert.type)
                dialogDelete.setOnDialogDismissedListener(this)
                dialogDelete.show(childFragmentManager, "Eliminar anuncio")
            }

            userAdvertsContainer.addView(resultView)

            animateResultItem(resultView)
        }

        withContext(Dispatchers.Main)
        {
            LoadingScreen.hide()
        }
    }

    private fun animateResultItem(resultItem: View) {

        resultItem.scaleX = 0f

        resultItem.visibility = View.VISIBLE

        resultItem.animate().scaleX(1f).setDuration(500).start()
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