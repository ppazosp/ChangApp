package ppazosp.changapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

interface OnDialogDismissedListener {
    fun onDialogDismissed()
}

class InsertAdvertDialog : DialogFragment() {

    private var listener: OnDialogDismissedListener? = null

    private var sports: List<Sport> = emptyList()
    private var places: List<Place> = emptyList()

    private var spinnerPlaces: Spinner? = null
    private var spinnerSports: Spinner? = null

    private var selectedPlaceID: Int = -1
    private var selectedSportID: Int = -1

    private lateinit var title_input: TextInputEditText
    private lateinit var description_input: TextInputEditText
    private lateinit var image_input: ImageView

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageView: ImageView? = view?.findViewById(R.id.pic_add)
            imageView?.setImageURI(uri)
        }
    }

    companion object {
        private const val ARG_PLACEID = "PLACE_KEY"
        private const val ARG_SPORTID = "SPORT_KEY"

        fun newInstance(place: Int, sport: Int): InsertAdvertDialog {
            val dialog = InsertAdvertDialog()
            val args = Bundle().apply {
                putInt(ARG_PLACEID, place)
                putInt(ARG_SPORTID, sport)
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
            selectedSportID = it.getInt(ARG_SPORTID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_insert_advert, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        title_input = view.findViewById(R.id.title_input)
        description_input = view.findViewById(R.id.description_input)
        image_input = view.findViewById(R.id.pic_add)

        spinnerPlaces = view.findViewById(R.id.spinner_places)
        spinnerSports = view.findViewById(R.id.spinner_sports)

        fetchSpinners()
        spinnerPlaces!!.setSelection(selectedPlaceID)
        spinnerSports!!.setSelection(selectedSportID)

        val createButton = view.findViewById<Button>(R.id.delete_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        val fabAddPhoto: FloatingActionButton = view.findViewById(R.id.fab_add_photo)
        fabAddPhoto.setOnClickListener {
            getImage.launch("image/*")
        }

        createButton.setOnClickListener {

            val titleText = title_input.text.toString()
            val descriptionText = description_input.text.toString()

            if (titleText.isEmpty() || descriptionText.isEmpty()) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                var imageInput: ByteArray? = null

                if(hasImageViewChanged(image_input)) imageInput = imageViewToByteArray(image_input)


                insertAdvert(titleText, descriptionText, imageInput)

                withContext(Dispatchers.Main) {
                    listener?.onDialogDismissed()
                    dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun hasImageViewChanged(imageView: ImageView): Boolean {
        val currentDrawable: Drawable? = imageView.drawable
        val defaultDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.default_image)

        return currentDrawable != defaultDrawable
    }

    private suspend fun insertAdvert(title: String, description: String, image: ByteArray?) {

        var imageBase64: String? = null

        if (hasImageViewChanged(image_input)) imageBase64 = image?.let { byteArrayToBase64(it) }

        val add = Advert(
            user = 1,
            sport = spinnerSports!!.selectedItemPosition,
            place = spinnerPlaces!!.selectedItemPosition,
            title = title,
            description = description,
            image = imageBase64
        )

        withContext(Dispatchers.IO) {
            supabase.from("adverts").insert(add)
        }
    }

    private fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun imageViewToByteArray(imageView: ImageView, quality: Int = 50): ByteArray {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        return stream.toByteArray()
    }

    private fun updateAdapters() {

        val adapterProvincias = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places.map { it.name })
        adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaces?.adapter = adapterProvincias
        spinnerPlaces?.setSelection(0)

        val adapterSports = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports.map { it.name })
        adapterSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSports?.adapter = adapterSports
        spinnerSports?.setSelection(0)
    }

    private fun fetchSpinners()
    {
        CoroutineScope(Dispatchers.Main).launch {
            sports = fetchSports()
            places = fetchPlaces()

            val defaultSport = Sport(id = -1, name = "--Seleccionar--")
            val defaultPlace = Place(id = -1, name = "--Seleccionar--")

            sports = listOf(defaultSport) + sports
            places = listOf(defaultPlace) + places

            if(isAdded) updateAdapters()
        }
    }

    private suspend fun fetchSports(): List<Sport>
    {
        return withContext(Dispatchers.IO) {
            supabase.from("sports").select().decodeList<Sport>()
        }
    }

    private suspend fun fetchPlaces(): List<Place>
    {
        return withContext(Dispatchers.IO) {
            supabase.from("places").select().decodeList<Place>()
        }
    }
}
