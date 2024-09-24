package ppazosp.changapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface OnDialogDismissedListener {
    fun onDialogDismissed()
}

class InsertAdvertDialog : DialogFragment() {

    private var listener: OnDialogDismissedListener? = null

    private var selectedPlaceID: Int = -1
    private var selectedSportID: Int = -1

    private lateinit var title_input: TextInputEditText
    private lateinit var description_input: TextInputEditText

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

        val create_button = view.findViewById<Button>(R.id.create_button)
        val cancel_button = view.findViewById<Button>(R.id.cancel_button)

        create_button.setOnClickListener {
            val titleText = title_input.text.toString()
            val descriptionText = description_input.text.toString()

            if (titleText.isEmpty() || descriptionText.isEmpty()) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                insertAdvert(titleText, descriptionText)

                withContext(Dispatchers.Main) {
                    listener?.onDialogDismissed()
                    dismiss()
                }
            }
        }

        cancel_button.setOnClickListener {
            dismiss()
        }

        return view
    }

    private suspend fun insertAdvert(title: String, description: String) {

        val add = Advert(
            user = 1,
            sport = selectedSportID,
            place = selectedPlaceID,
            title = title,
            description = description,
            image = "https://mapvepqvdgagccguault.supabase.co/storage/v1/object/public/images/default.jpg"
        )

        withContext(Dispatchers.IO) {
            supabase.from("adverts").insert(add)
        }
    }
}
