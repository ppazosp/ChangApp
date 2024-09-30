package ppazosp.changapp

import android.graphics.Paint.Join
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JoinActivityDialog : DialogFragment() {

    private lateinit var joinButton: Button
    private lateinit var cancelButton: Button

    private var advertOwnerID: Int = -1
    private var advertID: Int = -1

    companion object {
        private const val ARG_ADVERTOWNERID = "ADVERTOWNERID_KEY"
        private const val ARG_ADVERTID = "ADVERTID_KEY"

        fun newInstance(advertOwnerID: Int, advertID: Int): JoinActivityDialog {
            val dialog = JoinActivityDialog()
            val args = Bundle().apply {
                putInt(ARG_ADVERTOWNERID, advertOwnerID)
                putInt(ARG_ADVERTID, advertID)
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            advertOwnerID = it.getInt(ARG_ADVERTOWNERID, -1)
            advertID = it.getInt(ARG_ADVERTID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.dialog_join_activity, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        initializeVars(view)

        setOnClickListeners()

        return view
    }

    private fun initializeVars(view: View)
    {
        joinButton = view.findViewById(R.id.delete_button)
        cancelButton = view.findViewById(R.id.cancel_button)
    }

    private fun setOnClickListeners()
    {
        joinButton.setOnClickListener {

            LoadingScreen.show(requireContext())

            val request = InsertMessage(myUser.id!!, advertOwnerID, advertID, false )

            CoroutineScope(Dispatchers.Main).launch {
                sendResquest(requireContext(), request)

                withContext(Dispatchers.Main){
                    LoadingScreen.hide()
                    dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}
