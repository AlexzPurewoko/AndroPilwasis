/*
 * Copyright @2019 by Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 * This prototype is used for testing, and educating about APIs
 * @author APWDevs
 *
 * Licensed under GNU GPLv3
 *
 * This module is provided by "AS IS" and if you want to take
 * a copy or modifying this module, you must include this @author
 * Thanks! Happy Coding!
 */

package id.apwdevs.andropilkasis.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.ServerSettingActivity
import id.apwdevs.andropilkasis.params.PublicConfig
import java.security.MessageDigest

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun encryptMD5(s: String): String {
    val messageDigest = MessageDigest.getInstance("MD5")
    messageDigest.update(s.toByteArray())
    val outDigest = messageDigest.digest()
    val sBuffer = StringBuffer()
    for (o in outDigest) {
        sBuffer.append(Integer.toHexString(0xff and o.toInt()))
    }
    return sBuffer.toString()
}

@SuppressLint("SetTextI18n")
fun showServerConfig(context: Context) {
    AlertDialog.Builder(context).apply {

        val layout = LinearLayout(context).also { l ->
            l.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(16, 16, 16, 16)
            }
            l.orientation = LinearLayout.VERTICAL
            val edit = EditText(context).also { e ->
                e.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                e.hint = "admin password"
            }

            val btn = Button(context).also { b ->
                b.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                b.text = "Submit"
            }
            l.addView(edit)
            l.addView(btn)

            btn.setOnClickListener {
                val edt = edit.text.toString()
                if (edt.isEmpty())
                    edit.error = "Password kosong!"
                else {
                    val md5 = encryptMD5(edt)
                    if (md5 == PublicConfig.PASSWORD_CONFIG_SERVER) {
                        context.startActivity(
                            Intent(context, ServerSettingActivity::class.java)
                        )
                    } else
                        edit.error = "Password Salah!"
                }
            }
        }
        setView(layout)
        setTitle("Password Confirmation")
        setCancelable(false)
        setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
            restartApplication(context)
        }
    }.show()

}

fun showAlert(context: Context, config: (AlertDialog.Builder) -> Unit) {
    AlertDialog.Builder(context).apply {
        setCancelable(false)
        setIcon(R.drawable.ic_warning_black_24dp)
        setTitle("Error!")
        config(this)
    }.show()
}

fun showLoadingDialog(context: Context): AlertDialog {
    return AlertDialog.Builder(context).apply {
        setCancelable(false)
        setView(R.layout.loading_layout)
    }.create()
}

fun restartApplication(context: Context) {
    context.startActivity(
        Intent(
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        ).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

