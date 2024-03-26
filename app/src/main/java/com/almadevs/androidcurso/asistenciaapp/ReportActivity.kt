package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almadevs.androidcurso.R
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class ReportActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    private val employeeList = mutableListOf<Employee>()
    private lateinit var employeeRecyclerView: RecyclerView
    private lateinit var employeeAdapter: EmployeePuntoEncuentro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val buttonFinalizar = findViewById<Button>(R.id.btnFinalizar)
        buttonFinalizar.setOnClickListener{ navigateToHomePriveleger() }

        val btnreturn = findViewById<ImageView>(R.id.imageButtonBackListEmpl)
        btnreturn.setOnClickListener{ regresarInicio() }

        // Inicializar RecyclerView y su adaptador
        employeeRecyclerView = findViewById(R.id.recyclerViewListaReport)
        employeeRecyclerView.layoutManager = LinearLayoutManager(this)
        employeeAdapter = EmployeePuntoEncuentro(employeeList)
        employeeRecyclerView.adapter = employeeAdapter

        // Obtener la lista de empleados
        fetchEmployeeList()

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

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchEmployeeList() {
        // URL del servicio para obtener la lista de empleados
        val url = "http://192.168.130.63/asistenciapp_mysql/consultar_activos.php"

        // Crear una solicitud GET a la URL
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Analizar la respuesta JSON utilizando Gson
                val gson = Gson()

                // Iterar sobre el JSONArray y agregar cada empleado a la lista
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val employee = gson.fromJson(jsonObject.toString(), Employee::class.java)
                    employeeList.add(employee)
                }

                // Notificar al adaptador que los datos han cambiado
                employeeAdapter.notifyDataSetChanged()
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al obtener la lista de empleados: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }
}