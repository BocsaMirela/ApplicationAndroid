package com.example.mirela.appAndroid.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val mm = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity,activity as TimePickerDialog.OnTimeSetListener,hour,mm, true)
    }


}