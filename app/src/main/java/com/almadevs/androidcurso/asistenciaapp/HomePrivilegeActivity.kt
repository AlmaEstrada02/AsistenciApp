package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.almadevs.androidcurso.LoginActivity
import com.almadevs.androidcurso.R
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.Locale

class HomePrivilegeActivity : AppCompatActivity() {

    private lateinit var viewStart: CardView
    private lateinit var viewPause: CardView
    private lateinit var viewReturn: CardView
    private lateinit var viewExit: CardView


    private lateinit var btnReport: MenuItem
    private lateinit var btnListEmp: MenuItem
    private lateinit var sharedPreferences: SharedPreferences


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_privilege)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val nombre_usuario = sharedPreferences.getString("nombre_usuario", "")
        val id_usuario = sharedPreferences.getString("usuario", "")


        if (nombre_usuario.isNullOrEmpty()) {
            val intent = intent
            val nombre_usuarioIntent = intent.getStringExtra("nombre_usuario")
            // Guardar el nombre del usuario en SharedPreferences
            sharedPreferences.edit().putString("nombre_usuario", nombre_usuarioIntent).apply()
        } else if (id_usuario.isNullOrEmpty()) {
            val intent = intent
            val id_usuarioIntent = intent.getStringExtra("id_usuario")
            // Guardar el nombre del usuario en SharedPreferences
            sharedPreferences.edit().putString("id_usuario", id_usuarioIntent).apply()
        }

        findViewById<TextView>(R.id.nombreEmpleadoPrivilege).text = nombre_usuario
        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDatePrivilege)
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.collectionMenuBottomNavigation)
        //Obtener referencia del menu mas
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarHomePagePrivilege)

        //Terminos y condiciones desde el menu
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
        val buttonAceptar = dialogView.findViewById<Button>(R.id.buttonAceptar)

        //Actualizar estatus
        viewStart = findViewById(R.id.startPtivilege)
        viewPause = findViewById(R.id.pausePrivilege)
        viewReturn = findViewById(R.id.returnPrivilege)
        viewExit = findViewById(R.id.exitPrivilege)

        viewStart.setOnClickListener { actualizarEstadoUsuario(1) }
        viewPause.setOnClickListener { actualizarEstadoUsuario(2) }
        viewReturn.setOnClickListener { actualizarEstadoUsuario(3) }
        viewExit.setOnClickListener { actualizarEstadoUsuario(4) }

        // Obtener una referencia al menú inflado
        val menuInflater = menuInflater
        val menu = bottomNavigationView.menu
        // Obtener las referencias a los elementos del menú
        btnReport = menu.findItem(R.id.btnReport)
        btnListEmp = menu.findItem(R.id.btnListEmp)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        buttonAceptar.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Establecer la fecha actual en el TextView
        textDate.text = fechaActual

        btnReport.setOnMenuItemClickListener {
            navigateToReport()
            true
        }

        btnListEmp.setOnMenuItemClickListener {
            navigateToListEmpl()
            true
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.informacion -> {
                    // Manejar la acción del elemento "Información"
                    dialog.show()
                    true
                }
                R.id.cerrar_sesion -> {
                    // Manejar la acción del elemento "Cerrar Sesión"
                   AlertDialog.Builder(this)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Estás seguro de que deseas cerrar la sesión?")
                        .setPositiveButton("Aceptar") { dialog, which ->
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                    true
                }
                else -> false
            }
        }


    }
    private fun obtenerIdUsuario(): Int {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_usuario", 0) // 0 es el valor predeterminado si no se encuentra el ID del usuario
    }

    fun actualizarEstadoUsuario(nuevoEstado: Int) {
        val url = "http://192.168.1.81/asistenciapp_mysql/actualizar_estado_usuario.php"
        val idUsuario = obtenerIdUsuario() // Implementa esta función para obtener el ID del usuario

        // Crear la solicitud POST usando Volley
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Manejar la respuesta del servidor
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")
                if (success) {
                    // Actualización exitosa, mostrar mensaje de éxito
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    // Error en la actualización, mostrar mensaje de error
                    Snackbar.make(viewStart, message, Snackbar.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Error de conexión, mostrar mensaje de error
                Snackbar.make(viewStart, error.toString(), Snackbar.LENGTH_SHORT).show()
            }) {
            // Sobrescribir el método getParams para enviar los parámetros POST
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id_usuario"] = idUsuario.toString()
                params["status_usuario"] = nuevoEstado.toString()
                return params
            }
        }

        // Agregar la solicitud a la cola de solicitudes de Volley
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    private fun navigateToReport() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToListEmpl(){
        val intent = Intent( this, ListEmpActivity::class.java)
        startActivity(intent)
    }

}