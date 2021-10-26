package kr.khs.oneboard.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kr.khs.oneboard.R

object DialogUtil {
    private var loadingDialog: AppCompatDialog? = null

    fun createDialog(
        context: Context,
        message: String,
        positiveText: String,
        negativeText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(positiveText) { dialog, _ ->
                positiveAction.invoke()
            }
            .setNegativeButton(negativeText) { dialog, _ ->
                negativeAction.invoke()
                dialog.dismiss()
            }
            .show()
    }

    fun onLoadingDialog(activity: Activity?) {
        if (activity == null || activity.isFinishing)
            return

        loadingDialog?.let {
            if (it.isShowing)
                return
        }

        loadingDialog = AppCompatDialog(activity).apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_loading)
        }

        loadingDialog?.show()

        val loadingImage = loadingDialog?.findViewById<ImageView>(R.id.loadingImage)
        val loadingAni = loadingImage!!.background as AnimationDrawable
        loadingImage.post { loadingAni.start() }
    }

    fun offLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                loadingDialog?.dismiss()
            }
        }
    }
}