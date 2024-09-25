package ppazosp.changapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteAdvertDialog : DialogFragment() {

    private var listener: OnDialogDismissedListener? = null

    private var user: Int = -1
    private var place: Int = -1
    private var sport: Int = -1

    companion object {
        private const val ARG_USER = "USER_KEY"
        private const val ARG_PLACE = "PLACE_KEY"
        private const val ARG_SPORT = "SPORT_KEY"

        fun newInstance(user: Int, place: Int, sport: Int): DeleteAdvertDialog {
            val dialog = DeleteAdvertDialog()
            val args = Bundle().apply {
                putInt(ARG_USER, user)
                putInt(ARG_PLACE, place)
                putInt(ARG_SPORT, sport)
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
            user = it.getInt(ARG_USER, -1)
            place = it.getInt(ARG_PLACE, -1)
            sport = it.getInt(ARG_SPORT, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_delete_advert, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val deleteButton = view.findViewById<Button>(R.id.delete_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)


        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                deleteAdvert()

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

    private suspend fun deleteAdvert() {
        supabase.from("adverts").delete {
            filter {
                Advert::user eq user
                and { Advert::place eq place
                    and { Advert::sport eq sport
                    }
                }
            }
        }
    }

}
