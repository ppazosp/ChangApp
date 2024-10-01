package ppazosp.changapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.BinderThread
import androidx.fragment.app.DialogFragment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteUserDialog : DialogFragment() {

    private lateinit var deleteButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_delete_user, container, false)
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
            CoroutineScope(Dispatchers.Main).launch {
                if (deleteUser(requireContext())){
                    withContext(Dispatchers.Main)
                    {
                        LoadingScreen.hide()
                        (activity as MainActivity).showLoginActivity()
                        dismiss()
                    }
                }
                else dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}
