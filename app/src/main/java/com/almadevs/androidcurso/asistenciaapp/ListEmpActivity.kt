package com.almadevs.androidcurso.asistenciaapp

import android.app.Dialog
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadevs.androidcurso.R
import com.almadevs.androidcurso.databinding.ActivityListEmpBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.util.Locale



class ListEmpActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityListEmpBinding
    private var activeEmployeesButtonSelected = true
    private var inactiveEmployeesButtonSelected = false

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
        fetchEmployeesLists()
        setOnclickListeners()
    }

    private fun setOnclickListeners() {
        viewBinding.buttonStillToBePaidActivo.setOnClickListener {
            activeEmployeesButtonSelected = true
            inactiveEmployeesButtonSelected = false
            updateButtonStyles()
            fetchEmployeesList(activeEmployeesButtonSelected)
        }

        viewBinding.buttonEmpInactivos.setOnClickListener {
            activeEmployeesButtonSelected = false
            inactiveEmployeesButtonSelected = true
            updateButtonStyles()
            fetchEmployeesList(activeEmployeesButtonSelected)
        }

        viewBinding.imageButtonBackListEmpl.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchEmployeesList(activeEmployeesButtonSelected: Boolean) {
        // URL del servicio según el botón seleccionado
        val url = if (this.activeEmployeesButtonSelected) {
            "http://192.168.130.63/asistenciapp_mysql/consultar_activos.php"
        } else {
            "http://192.168.130.63/asistenciapp_mysql/consultar_inactivos.php"
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
                if (this.activeEmployeesButtonSelected) {
                    viewBinding.textTotalActivos.text = "$totalEmployees"
                } else {
                    viewBinding.textTotalInactivos.text = "$totalEmployees"
                }
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(
                    this,
                    "Error al obtener la lista de empleados: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }

    private fun fetchEmployeesLists() {
        // URL de las consultas de empleados activos e inactivos
        val urlActivos = "http://192.168.130.63/asistenciapp_mysql/consultar_activos.php"
        val urlInactivos = "http://192.168.130.63/asistenciapp_mysql/consultar_inactivos.php"

        // Variables para almacenar el total de empleados activos e inactivos
        var totalActivos = 0
        var totalInactivos = 0

        // Crear una solicitud GET para empleados inactivos
        val requestInactivos = JsonArrayRequest(Request.Method.GET, urlInactivos, null,
            { response ->
                // Analizar la respuesta JSON utilizando Gson
                val gson = Gson()
                val employeesList = gson.fromJson(response.toString(), Array<Employee>::class.java)

                // Asignar el tamaño de la lista de empleados inactivos a totalInactivos
                totalInactivos = employeesList.size

                // Mostrar la lista de empleados inactivos (aquí debes implementar tu lógica)
                displayEmployeesList(employeesList)

                // Actualizar el TextView correspondiente para empleados inactivos
                viewBinding.textTotalInactivos.text = "$totalInactivos"
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(
                    this,
                    "Error al obtener la lista de empleados inactivos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Crear una solicitud GET para empleados activos
        val requestActivos = JsonArrayRequest(Request.Method.GET, urlActivos, null,
            { response ->
                // Analizar la respuesta JSON utilizando Gson
                val gson = Gson()
                val employeesList = gson.fromJson(response.toString(), Array<Employee>::class.java)

                // Asignar el tamaño de la lista de empleados activos a totalActivos
                totalActivos = employeesList.size

                // Mostrar la lista de empleados activos (aquí debes implementar tu lógica)
                displayEmployeesList(employeesList)

                // Actualizar el TextView correspondiente para empleados activos
                viewBinding.textTotalActivos.text = "$totalActivos"
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(
                    this,
                    "Error al obtener la lista de empleados activos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })


        // Agregar las solicitudes a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).apply {
            add(requestInactivos)
            add(requestActivos)
        }
    }

    // Función para mostrar la lista de empleados
    private fun displayEmployeesList(employeesList: Array<Employee>) {
        val adapter = EmployeeAdapter(employeesList)
        viewBinding.recyclerViewEmployees.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerViewEmployees.adapter = adapter

        adapter.setOnItemClickListener { employee ->
            Log.d(
                "EmployeeList",
                "Empleado seleccionado - ID: ${employee.id_usuario}, Nombre: ${employee.nombre_completo}"
            )
            showEmployeeDetailsModal(employee)
        }
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

    private fun showEmployeeDetailsModal(employee: Employee) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cambio_status)
        dialog.show()

        val buttonChangeStatus = dialog.findViewById<Button>(R.id.acceptButtonStatus)
        buttonChangeStatus.setOnClickListener {
            // Determinar el nuevo estado del empleado
            val newStatus = if (employee.status_usuario == 0) 1 else 0
            // Aquí realizas la solicitud al servicio PHP para cambiar el estado del empleado
            val url = "http://192.168.130.63/asistenciapp_mysql/cambio_status_admin.php"
            val request = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // Procesa la respuesta del servicio
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    if (success) {
                        // Estado cambiado exitosamente
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        // Actualizar el estado del empleado localmente
                        employee.status_usuario = newStatus
                        // Puedes realizar acciones adicionales aquí, como actualizar la lista de empleados
                    } else {
                        // Error al cambiar el estado
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                    fetchEmployeesLists()
                },
                Response.ErrorListener { error ->
                    // Error de la solicitud
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id_usuario"] = employee.id_usuario.toString()
                    // Envía el nuevo estado del empleado al servicio
                    params["status_usuario"] = newStatus.toString()
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)
        }
        val buttonCancel = dialog.findViewById<Button>(R.id.cancelButtonStatus)
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun activateButton(button: Button, isActive: Boolean) {
        if (isActive) {
            when (button) {
                viewBinding.buttonStillToBePaidActivo -> {
                    button.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_active_left_corner,
                        null
                    )
                    button.setTextColor(Color.WHITE)
                }

                viewBinding.buttonEmpInactivos -> {
                    button.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_active_right_corner,
                        null
                    )
                    button.setTextColor(Color.WHITE)
                }
                else -> {
                }
            }
        } else {
            when (button) {
                viewBinding.buttonStillToBePaidActivo -> {
                    button.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_disable_left_corner,
                        null
                    )
                    button.setTextColor(ContextCompat.getColor(this, R.color.azul_app_asistencia))
                }

                viewBinding.buttonEmpInactivos -> {
                    button.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_disable_right_corner,
                        null
                    )
                    button.setTextColor(ContextCompat.getColor(this, R.color.azul_app_asistencia))
                }

                else -> {

                }
            }
        }
    }
}
