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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    private lateinit var viewStart: CardView
    private lateinit var viewPause: CardView
    private lateinit var viewReturn: CardView
    private lateinit var viewExit: CardView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var exitDialog: AlertDialog

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        //Recuperar id y nombre de usuario
        val nombre_usuario = sharedPreferences.getString("nombre_usuario", "")
        val idUsuario = sharedPreferences.getInt("id_usuario", 0)

        if (nombre_usuario.isNullOrEmpty()) {
            val intent = intent
            val nombre_usuarioIntent = intent.getStringExtra("nombre_usuario")
            // Guardar el nombre del usuario en SharedPreferences
            sharedPreferences.edit().putString("nombre_usuario", nombre_usuarioIntent).apply()
        } else if (idUsuario == 0) {
            val idUsuarioIntent = intent.getIntExtra("id_usuario", 0)

            // Verifica si el valor recibido del Intent no es cero
            if (idUsuarioIntent != 0) {
                // Guarda el valor en SharedPreferences
                sharedPreferences.edit().putInt("id_usuario", idUsuarioIntent).apply()
            }
        }

        //Actualizar estatus
        viewStart = findViewById(R.id.start)
        viewPause = findViewById(R.id.pause)
        viewReturn = findViewById(R.id.returnn)
        viewExit = findViewById(R.id.exit)

        //Activo=0   Inactivo=1
        viewStart.setOnClickListener { actualizarEstadoUsuario(0) }
        viewPause.setOnClickListener { actualizarEstadoUsuario(1) }
        viewReturn.setOnClickListener { actualizarEstadoUsuario(0) }
        viewExit.setOnClickListener { actualizarEstadoUsuario(1) }

        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDate)
        findViewById<TextView>(R.id.nombreEmpleado).text = nombre_usuario

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)

        // Establecer la fecha actual en el TextView
        textDate.text = fechaActual

        val menuButton = findViewById<ImageButton>(R.id.imageButtonMenu)

        // Establecer clic en el ImageButton para mostrar el menú emergente
        menuButton.setOnClickListener { showPopupMenu(menuButton) }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_close_sesion, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        exitDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.acceptButton).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            exitDialog.dismiss()
        }

        // Configurar el listener de clic para el botón "Cancelar"
        dialogView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
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
    }

    private fun obtenerIdUsuario(): Int {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id_usuario", 0)
        Log.d("TAG", "ID de usuario recuperado: $idUsuario")
        return idUsuario
    }

    fun actualizarEstadoUsuario(nuevoEstado: Int) {
        val url = "http://192.168.130.63/asistenciapp_mysql/actualizar_status.php"
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.informacion-> Toast.makeText(this,"Terminos y condiciones", Toast.LENGTH_SHORT).show()
            R.id.cerrar_sesion-> Toast.makeText(this,"Cerrar Sesión", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
    fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_dashboard, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.informacion -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_info)
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
    }
}