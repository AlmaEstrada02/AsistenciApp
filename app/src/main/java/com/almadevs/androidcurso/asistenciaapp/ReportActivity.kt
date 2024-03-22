package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.almadevs.androidcurso.R

class ReportActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val buttonFinalizar = findViewById<Button>(R.id.btnFinalizar)
        buttonFinalizar.setOnClickListener{ navigateToHomePriveleger() }

        val btnreturn = findViewById<ImageView>(R.id.imageButtonBackListEmpl)
        btnreturn.setOnClickListener{ regresarInicio() }
    }
    var isLoading: Boolean = false

    private fun navigateToHomePriveleger(){
        val intent = Intent(this, HomePrivilegeActivity::class.java)
        startActivity(intent)
    }
    fun regresarInicio() {
        //regresar a la pagina home privilege
        if (!isLoading) {
            this.finish()
        } else {
            Toast.makeText(
                baseContext,
                "Cargando datos, espere un momento por favor.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}