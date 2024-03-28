package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class ReportActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    private val employeeList = mutableListOf<Employee>()
    private lateinit var employeeRecyclerView: RecyclerView
    private lateinit var employeeEncuentro: EmployeePuntoEncuentro

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
        employeeEncuentro = EmployeePuntoEncuentro(employeeList)
        employeeRecyclerView.adapter = employeeEncuentro

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

    private fun cambioPuntoModal(employee: Employee) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_punto_encuentro)
        dialog.show()

        val buttonChangeStatus = dialog.findViewById<Button>(R.id.accepPuntoEcuentro)
        buttonChangeStatus.setOnClickListener {
            // Determinar el nuevo estado del empleado
            val newStatusPunto = if (employee.punto_encuentro == 0) 1 else 0
            // Aquí realizas la solicitud al servicio PHP para cambiar el estado del empleado
            val url = "http://192.168.1.81/asistenciapp_mysql/modificar_punto_encuentro_admin.php"
            val request = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // Procesa la respuesta del servicio
                    try {
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getBoolean("success")
                        val message = jsonResponse.getString("message")
                        if (success) {
                            // Estado cambiado exitosamente
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            // Actualizar el estado del empleado localmente
                            employee.punto_encuentro = newStatusPunto
                            // Notificar al adaptador del RecyclerView para que actualice la vista
                            employeeEncuentro.notifyDataSetChanged()
                        } else {
                            // Error al cambiar el estado
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    dialog.dismiss()
                },
                Response.ErrorListener { error ->
                    // Error de la solicitud
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id_usuario"] = employee.id_usuario.toString()
                    // Envía el nuevo estado del empleado al servicio
                    params["punto_encuentro"] = newStatusPunto.toString()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchEmployeeList() {
        // URL del servicio para obtener la lista de empleados
        val url = "http://192.168.1.81/asistenciapp_mysql/consultar_activos.php"

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

                employeeEncuentro.setOnItemClickListener { employee ->
                   // cambioPuntoModal(employee)
                }
                // Notificar al adaptador que los datos han cambiado
                employeeEncuentro.notifyDataSetChanged()
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al obtener la lista de empleados: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }
}