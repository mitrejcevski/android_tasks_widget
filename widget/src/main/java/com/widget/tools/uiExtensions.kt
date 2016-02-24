package com.widget.tools

import android.app.Activity
import android.widget.Toast

fun Activity.toast(resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(this.getString(resource), duration)
}

fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
