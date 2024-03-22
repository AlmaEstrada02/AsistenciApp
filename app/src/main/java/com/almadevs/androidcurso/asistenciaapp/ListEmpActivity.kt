package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.almadevs.androidcurso.R
import com.almadevs.androidcurso.databinding.ActivityListEmpBinding
import java.util.Locale


class ListEmpActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityListEmpBinding


    var activityRoute: String = ""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityListEmpBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        val textDate = findViewById<TextView>(R.id.textDateCollectionListEmp)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)

        textDate.text = fechaActual
        setOnclickListeners()
        setOptionButton()

    }


    private fun setOnclickListeners() {

        viewBinding.buttonStillToBePaidActivo.setOnClickListener {
            viewBinding.buttonStillToBePaidActivo.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_active_left_corner, null)
            viewBinding.buttonStillToBePaidActivo.setTextColor(Color.WHITE)
            viewBinding.buttonEmpInactivos.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_disable_right_corner, null)
            viewBinding.buttonEmpInactivos.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.azul_app_asistencia
                )
            )
            viewBinding.viewPagerCollectionMyDeposits.currentItem = 0
        }

        viewBinding.buttonEmpInactivos.setOnClickListener {
            viewBinding.buttonStillToBePaidActivo.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_disable_left_corner, null)
            viewBinding.buttonStillToBePaidActivo.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.azul_app_asistencia
                )
            )
            viewBinding.buttonEmpInactivos.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_active_right_corner, null)
            viewBinding.buttonEmpInactivos.setTextColor(Color.WHITE)
            viewBinding.viewPagerCollectionMyDeposits.currentItem = 1
        }
        viewBinding.imageButtonBackListEmpl.setOnClickListener {
            onBackPressed()
        }

    }

    private fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                activateButton(viewBinding.buttonStillToBePaidActivo, true)
                activateButton(viewBinding.buttonEmpInactivos, false)
            }

            1 -> {
                activateButton(viewBinding.buttonStillToBePaidActivo, false)
                activateButton(viewBinding.buttonEmpInactivos, true)
            }
        }
    }


    private fun activateButton(button: Button, isActive: Boolean) {
        if (isActive) {
            if (button == viewBinding.buttonStillToBePaidActivo) {
                button.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.button_active_left_corner,
                    null
                )
            } else if (button == viewBinding.buttonEmpInactivos) {
                button.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.button_active_right_corner,
                    null
                )
            }
            button.setTextColor(Color.WHITE)
        } else {
            // Fondo y color de texto para el estado inactivo
            if (button == viewBinding.buttonStillToBePaidActivo) {
                button.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.button_disable_left_corner,
                    null
                )
            } else if (button == viewBinding.buttonEmpInactivos) {
                button.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.button_disable_right_corner,
                    null
                )
            }
            button.setTextColor(ContextCompat.getColor(this, R.color.azul_app_asistencia))
        }

    }

    private fun setOptionButton() {
        if (activityRoute == "1") {
            activateButton(viewBinding.buttonEmpInactivos, true)
            activateButton(viewBinding.buttonStillToBePaidActivo, false)
            viewBinding.viewPagerCollectionMyDeposits.currentItem = 1

        } else {
            activateButton(viewBinding.buttonStillToBePaidActivo, true)
            activateButton(viewBinding.buttonEmpInactivos, false)
            viewBinding.viewPagerCollectionMyDeposits.currentItem = 0
        }
    }

}