package com.almadevs.androidcurso.asistenciaapp

import EmployeeAdapter
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadevs.androidcurso.R
import com.almadevs.androidcurso.databinding.ActivityListEmpBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.util.Locale


class ListEmpActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityListEmpBinding
    private var activeEmployeesButtonSelected = true
    private var inactiveEmployeesButtonSelected = false


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

    }

    private fun setOnclickListeners() {
        viewBinding.buttonStillToBePaidActivo.setOnClickListener {
            activeEmployeesButtonSelected = true
            inactiveEmployeesButtonSelected = false
            updateButtonStyles()
            fetchEmployeesList()
        }

        viewBinding.buttonEmpInactivos.setOnClickListener {
            activeEmployeesButtonSelected = false
            inactiveEmployeesButtonSelected = true
            updateButtonStyles()
            fetchEmployeesList()
        }

        viewBinding.imageButtonBackListEmpl.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchEmployeesList() {
        // URL del servicio según el botón seleccionado
        val url = if (activeEmployeesButtonSelected) {
            "http://192.168.1.81/asistenciapp_mysql/consultar_activos.php"
        } else {
            "http://192.168.1.81/asistenciapp_mysql/consultar_inactivos.php"
        }

        // Crear una solicitud GET a la URL
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                // Analizar la respuesta JSON utilizando Gson
                val gson = Gson()
                val employeesList = gson.fromJson(response.toString(), Array<Employee>::class.java)

                // Mostrar la lista de empleados (aquí debes implementar tu lógica)
                displayEmployeesList(employeesList)
                val totalEmployees = employeesList.size
                Log.d("TotalEmployees", "$totalEmployees")
                // Actualizar el TextView correspondiente según el tipo de empleados
                if (activeEmployeesButtonSelected) {
                    viewBinding.textTotalActivos.text = "$totalEmployees"
                } else {
                    viewBinding.textTotalInactivos.text = "$totalEmployees"
                }

            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al obtener la lista de empleados: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }

    // Función para mostrar la lista de empleados
    private fun displayEmployeesList(employeesList: Array<Employee>) {
        val adapter = EmployeeAdapter(employeesList)
        viewBinding.recyclerViewEmployees.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerViewEmployees.adapter = adapter
    }

    private fun updateButtonStyles() {
        if (activeEmployeesButtonSelected) {
            activateButton(viewBinding.buttonStillToBePaidActivo, true)
            activateButton(viewBinding.buttonEmpInactivos, false)
        } else if (inactiveEmployeesButtonSelected) {
            activateButton(viewBinding.buttonStillToBePaidActivo, false)
            activateButton(viewBinding.buttonEmpInactivos, true)
        }
    }

    private fun activateButton(button: Button, isActive: Boolean) {
        if (isActive) {
            button.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_active_left_corner,
                null
            )
            button.setTextColor(Color.WHITE)
        } else {
            button.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_disable_right_corner,
                null
            )
            button.setTextColor(ContextCompat.getColor(this, R.color.azul_app_asistencia))
        }
    }

}