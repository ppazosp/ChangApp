package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class ShowAdvertDialog : DialogFragment() {

    private var advertID: Int = -1

    private lateinit var advert: Advert
    private lateinit var user: User
    private lateinit var place: Place
    private lateinit var type: Type

    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var imageView: ImageView
    private lateinit var fullnameView: TextView
    private lateinit var placeView: TextView
    private lateinit var typeView: TextView
    private lateinit var dateView: TextView

    companion object {
        private const val ARG_ADVERTID = "USER_KEY"

        fun newInstance(advertID: Int): ShowAdvertDialog {
            val dialog = ShowAdvertDialog()
            val args = Bundle().apply {
                putInt(ARG_ADVERTID, advertID)
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            advertID = it.getInt(ARG_ADVERTID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_show_advert, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.item_background_dark)

        LoadingScreen.show(requireActivity())

        initializeVars(view)

        return view
    }

    private fun initializeVars(view: View)
    {
        titleView = view.findViewById(R.id.title_view)
        descriptionView = view.findViewById(R.id.description_view)
        imageView = view.findViewById(R.id.advert_image_view)
        fullnameView = view.findViewById(R.id.user_view)
        placeView = view.findViewById(R.id.place_view)
        typeView = view.findViewById(R.id.type_view)
        dateView = view.findViewById(R.id.dateView)

        CoroutineScope(Dispatchers.Main).launch {
            advert = fetchAdvert(advertID)
            user = fetchUser(advert.user)
            place = fetchPlace(advert.place)
            type = fetchType(advert.type)

            withContext(Dispatchers.Main) {
                setUI()

                LoadingScreen.hide()
            }
        }
    }

    private fun setUI()
    {
        titleView.text = advert.title
        descriptionView.text = advert.description
        fullnameView.text = user.fullname
        placeView.text = place.name
        typeView.text = type.name
        dateView.text = advert.date

        if (advert.image != null ) Encripter.setImageFromBase64(imageView, advert.image)
        else imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.default_image))
    }

}
