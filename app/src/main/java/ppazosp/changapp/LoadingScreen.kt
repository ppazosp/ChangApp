package ppazosp.changapp

import android.content.Context
import androidx.fragment.app.FragmentActivity

object LoadingScreen {

    private lateinit var loadingDialog: LoadingDialog

    fun show(context: FragmentActivity) {
        loadingDialog = LoadingDialog()
        loadingDialog.show(context.supportFragmentManager, "Cargando...")
    }

    fun hide() {
        loadingDialog.dismiss()
    }
}
