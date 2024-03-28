package com.almadevs.androidcurso.asistenciaapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var exitDialog: AlertDialog

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_privilege)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val nombre_usuario = sharedPreferences.getString("nombre_usuario", "")
        val idUsuario = sharedPreferences.getInt("id_usuario", 0)


        if (nombre_usuario.isNullOrEmpty()) {
            val nombre_usuarioIntent = intent.getStringExtra("nombre_usuario")

            // Verifica si el valor recibido del Intent no es nulo
            if (nombre_usuarioIntent != null) {
                // Si no es nulo, guárdalo en SharedPreferences
                sharedPreferences.edit().putString("nombre_usuario", nombre_usuarioIntent).apply()
            }
        } else if (idUsuario == 0) {
            val idUsuarioIntent = intent.getIntExtra("id_usuario", 0)

            // Verifica si el valor recibido del Intent no es cero
            if (idUsuarioIntent != 0) {
                // Guarda el valor en SharedPreferences
                sharedPreferences.edit().putInt("id_usuario", idUsuarioIntent).apply()
            }
        }

        findViewById<TextView>(R.id.nombreEmpleadoPrivilege).text = nombre_usuario
        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDatePrivilege)

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.collectionMenuBottomNavigation)

        //Actualizar estatus
        viewStart = findViewById(R.id.startPtivilege)
        viewPause = findViewById(R.id.pausePrivilege)
        viewReturn = findViewById(R.id.returnPrivilege)
        viewExit = findViewById(R.id.exitPrivilege)

        viewStart.setOnClickListener { actualizarEstadoUsuario(0) }
        viewPause.setOnClickListener { actualizarEstadoUsuario(1) }
        viewReturn.setOnClickListener { actualizarEstadoUsuario(0) }
        viewExit.setOnClickListener { actualizarEstadoUsuario(1) }

        // Obtener una referencia al menú inflado
        val menuInflater = menuInflater
        val menu = bottomNavigationView.menu
        // Obtener las referencias a los elementos del menú
        btnReport = menu.findItem(R.id.btnReport)
        btnListEmp = menu.findItem(R.id.btnListEmp)


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
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarHomePagePrivilege)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_close_sesion, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        exitDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.acceptButtonSesion).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
            exitDialog.dismiss()
        }

        // Configurar el listener de clic para el botón "Cancelar"
        dialogView.findViewById<Button>(R.id.cancelButtonSesion).setOnClickListener {
            exitDialog.dismiss()
        }

        // Crear el callback para manejar el evento onBackPressed
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Mostrar el cuadro de diálogo de confirmación
                exitDialog.show()
            }
        }
        // Registrar el callback con el OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        toolbar.setOnMenuItemClickListener() { menuItem ->
            when (menuItem.itemId) {
                R.id.informacion -> {
                    // Crear y mostrar un diálogo personalizado para la opción "Información"
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
                    val dialogBuilder = AlertDialog.Builder(this)
                        .setView(dialogView)

                    val dialog = dialogBuilder.create()

                    // Configurar el listener de clic para el botón "Aceptar" del diálogo
                    dialogView.findViewById<Button>(R.id.acceptButtonInfo).setOnClickListener {
                        dialog.dismiss()
                    }

                    // Mostrar el diálogo personalizado
                    dialog.show()

                    true

                }

                R.id.cerrar_sesion -> {
                    // Crear un diálogo personalizado para la confirmación de cierre de sesión
                    val dialogView =
                        LayoutInflater.from(this).inflate(R.layout.dialog_close_sesion, null)
                    val dialogBuilder = AlertDialog.Builder(this)
                        .setView(dialogView)

                    val dialog = dialogBuilder.create()

                    // Configurar el listener de clic para el botón "Aceptar" del diálogo
                    dialogView.findViewById<Button>(R.id.acceptButtonSesion).setOnClickListener {
                        // Realizar la acción de cerrar sesión aquí
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                        dialog.dismiss()
                    }

                    // Configurar el listener de clic para el botón "Cancelar" del diálogo
                    dialogView.findViewById<Button>(R.id.cancelButtonSesion).setOnClickListener {
                        dialog.dismiss()
                    }

                    // Mostrar el diálogo personalizado
                    dialog.show()

                    true
                }

                else -> false
            }
        }
}

    private fun obtenerIdUsuario(): Int {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id_usuario", 0)
        Log.d("TAG", "ID de usuario recuperado: $idUsuario")
        return idUsuario
    }

    fun actualizarEstadoUsuario(nuevoEstado: Int) {
        val url = "http://192.168.1.81/asistenciapp_mysql/actualizar_status.php"
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    private fun navigateToReport() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToListEmpl() {
        val intent = Intent(this, ListEmpActivity::class.java)
        startActivity(intent)
    }

        /* fun showPopupMenuPrivilege(imageButton: ImageButton) {
             val popupMenu = PopupMenu(this, imageButton)
             popupMenu.menuInflater.inflate(R.menu.menu_dashboard, popupMenu.menu)

             popupMenu.setOnMenuItemClickListener { menuItem ->
                 when (menuItem.itemId) {
                     R.id.informacion -> {
                         val dialog = Dialog(this)
                         dialog.setContentView(R.layout.custom_dialog_layout)
                         dialog.show()
                         true
                     }

                     R.id.cerrar_sesion -> {
                         // Manejar la acción del elemento "Cerrar Sesión"
                         val dialogView =
                             LayoutInflater.from(this).inflate(R.layout.dialog_close_sesion, null)
                         val dialogBuilder = AlertDialog.Builder(this)
                             .setView(dialogView)

                         val dialog = dialogBuilder.show()

                         dialogView.findViewById<Button>(R.id.acceptButton).setOnClickListener {
                             val intent = Intent(this, LoginActivity::class.java)
                             startActivity(intent)
                             finish()
                             dialog.dismiss()
                         }

                         dialogView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
                             dialog.dismiss()
                         }

                         true
                     }

                     else -> false
                 }
             }
         }*/
}