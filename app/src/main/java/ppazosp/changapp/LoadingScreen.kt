package ppazosp.changapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager

object LoadingScreen {

    private lateinit var loadingDialog: AlertDialog

    fun show(context: Context) {
        val builder = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        builder.setView(dialogView)
        builder.setCancelable(false)

        loadingDialog = builder.create()
        loadingDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        loadingDialog.show()
        loadingDialog.window?.setLayout(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun hide() {
        loadingDialog.dismiss()
    }
}
