package de.fra_uas.fb2.mobiledevices.abeautifulmind

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

class CustomToast() {

    // static function
    companion object {
        fun makeText(context: Context, text: String?) {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast_layout, null)

            val toastText = layout.findViewById<TextView>(R.id.toastText)
            toastText.text = text

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }
    }
}