package kr.khs.oneboard.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {
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
}