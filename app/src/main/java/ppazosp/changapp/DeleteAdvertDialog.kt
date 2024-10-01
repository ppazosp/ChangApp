package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteAdvertDialog : DialogFragment() {

    private var listener: OnDialogDismissedListener? = null

    private var user: Int = -1
    private var place: Int = -1
    private var sport: Int = -1

    private lateinit var deleteButton: Button
    private lateinit var cancelButton: Button

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

        initializeVars(view)

        setOnClickListeners()

        return view
    }

    private fun initializeVars(view: View)
    {
        deleteButton = view.findViewById(R.id.delete_button)
        cancelButton = view.findViewById(R.id.cancel_button)
    }

    private fun setOnClickListeners()
    {
        deleteButton.setOnClickListener {

            LoadingScreen.show(requireActivity())

            CoroutineScope(Dispatchers.Main).launch { deleteAdvert(requireContext(), user, place, sport) }

            LoadingScreen.hide()

            listener?.onDialogDismissed()
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}
