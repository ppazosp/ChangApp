package ppazosp.changapp

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface OnDialogDismissedListener {
    fun onDialogDismissed()
}

class InsertAdvertDialog : DialogFragment() {

    private var listener: OnDialogDismissedListener? = null

    private var types: List<Type> = emptyList()
    private var places: List<Place> = emptyList()

    private var spinnerPlaces: Spinner? = null
    private var spinnerTypes: Spinner? = null

    private var selectedPlaceID: Int = -1
    private var selectedTypeID: Int = -1

    private lateinit var title_input: TextInputEditText
    private lateinit var description_input: TextInputEditText
    private lateinit var image_input: ImageView

    private lateinit var createButton: Button
    private lateinit var cancelButton: Button

    private lateinit var addPhotoFAB: FloatingActionButton

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageView: ImageView? = view?.findViewById(R.id.pic_add)
            imageView?.setImageURI(uri)
        }
    }

    companion object {
        private const val ARG_PLACEID = "PLACE_KEY"
        private const val ARG_TYPEID = "TYPE_KEY"

        fun newInstance(place: Int, sport: Int): InsertAdvertDialog {
            val dialog = InsertAdvertDialog()
            val args = Bundle().apply {
                putInt(ARG_PLACEID, place)
                putInt(ARG_TYPEID, sport)
            }
            dialog.arguments = args
            return dialog
        }
    }

    fun setOnDialogDismissedListener(listener: OnDialogDismissedListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPlaceID = it.getInt(ARG_PLACEID, -1)
            selectedTypeID = it.getInt(ARG_TYPEID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_insert_advert, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        LoadingScreen.show(requireActivity())

        initializeVars(view)

        setUI()

        setOnClickListeners()

        LoadingScreen.hide()

        return view
    }

    private fun initializeVars(view: View)
    {
        title_input = view.findViewById(R.id.last_message)
        description_input = view.findViewById(R.id.description_input)
        image_input = view.findViewById(R.id.pic_add)

        spinnerPlaces = view.findViewById(R.id.spinner_places)
        spinnerTypes = view.findViewById(R.id.spinner_sports)

        createButton = view.findViewById(R.id.delete_button)
        cancelButton = view.findViewById(R.id.cancel_button)

        addPhotoFAB = view.findViewById(R.id.fab_add_photo)
    }

    private fun setUI()
    {
        CoroutineScope(Dispatchers.Main).launch {fetchSpinners()}
    }

    private fun setOnClickListeners()
    {
        addPhotoFAB.setOnClickListener {
            getImage.launch("image/*")
        }

        createButton.setOnClickListener {

            val titleText = title_input.text.toString()
            val descriptionText = description_input.text.toString()

            if (titleText.isEmpty() || descriptionText.isEmpty()) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                withContext(Dispatchers.Main)
                {
                    LoadingScreen.show(requireActivity())
                }

                var imageInput: ByteArray? = null
                var image64: String? = null

                if(hasImageViewChanged(image_input)) {
                    imageInput = Encripter.imageViewToByteArray(image_input)
                    image64 = Encripter.byteArrayToBase64(imageInput)
                }

                insertAdvert(requireContext(), InsertAdvert(myUser.id!!, spinnerTypes!!.selectedItemPosition, spinnerPlaces!!.selectedItemPosition, titleText, descriptionText, image64, getFormattedDate()))

                withContext(Dispatchers.Main) {
                    LoadingScreen.hide()

                    listener?.onDialogDismissed()
                    dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun hasImageViewChanged(imageView: ImageView): Boolean {
        val currentDrawable: Drawable? = imageView.drawable
        val defaultDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.default_image)

        return currentDrawable != defaultDrawable
    }

    private fun updateAdapters() {

        val adapterPlaces = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places.map { it.name })
        adapterPlaces.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaces?.adapter = adapterPlaces
        spinnerPlaces?.setSelection(selectedPlaceID)

        val adapterTypes = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types.map { it.name })
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes?.adapter = adapterTypes
        spinnerTypes?.setSelection(selectedTypeID)
    }

    private suspend fun fetchSpinners()
    {
            types = fetchTypes()
            places = fetchPlaces()

            val defaultType = Type(id = -1, name = "--Seleccionar--")
            val defaultPlace = Place(id = -1, name = "--Seleccionar--")

            types = listOf(defaultType) + types
            places = listOf(defaultPlace) + places

            updateAdapters()
    }
}
