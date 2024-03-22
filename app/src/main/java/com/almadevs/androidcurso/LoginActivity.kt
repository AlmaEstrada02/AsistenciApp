package com.almadevs.androidcurso

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Button
import com.almadevs.androidcurso.asistenciaapp.HomeActivity
import androidx.appcompat.widget.AppCompatEditText
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.privacysandbox.tools.core.model.Method
import com.almadevs.androidcurso.asistenciaapp.HomePrivilegeActivity
import com.almadevs.androidcurso.databinding.ActivityLoginBinding
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import com.android.volley.Response
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var ediTextUsuario: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonEntrar: Button

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ediTextUsuario = findViewById(R.id.ediTextUsuario)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonEntrar = findViewById(R.id.buttonEntrar)

        buttonEntrar.setOnClickListener {
            validarUsuario("http://192.168.1.81/asistenciapp_mysql/validar_usuario.php")
        }

        val editTextPassword: AppCompatEditText = findViewById(R.id.editTextPassword)

        editTextPassword.setOnTouchListener { _, event ->
            //Verifica si se hizo click en el drawable
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (editTextPassword.right - editTextPassword.compoundDrawablesRelative[2].bounds.width())
            ) {
                // Realiza la acción de mostrar/ocultar la contraseña
                togglePasswordVisibility(editTextPassword)
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun validarUsuario(url: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                if (!response.isEmpty()) {
                    val jsonObject = JSONObject(response)
                    val tipoUsuario = jsonObject.getInt("tipo_usuario")
                    // Obtener el nombre de usuario del JSON de la respuesta
                    val nombre_usuario = jsonObject.getString("nombre_usuario")
                    val id_usuario = jsonObject.getInt("id_usuario")
                    val intent = if (tipoUsuario == 0) {
                        Intent(applicationContext, HomePrivilegeActivity::class.java)
                    } else {
                        Intent(applicationContext, HomeActivity::class.java)
                    }
                    intent.putExtra("nombre_usuario", nombre_usuario)
                    // Actualizar el id y el nombre de usuario en SharedPreferences
                    actualizarNombreUsuarioEnSharedPreferences(nombre_usuario)
                    startActivity(intent)

                    actualizarIdUsuarioEnSharedPreferences(id_usuario) // Nuevo
                    startActivity(intent)
                } else {
                    snackBarMessage("Verifica tu número de empleado o contraseña e intenta nuevamente")
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@LoginActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros["usuario"] = ediTextUsuario.text.toString()
                parametros["password"] = editTextPassword.text.toString()
                return parametros
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun actualizarNombreUsuarioEnSharedPreferences(nombreUsuario: String) {
        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", MODE_PRIVATE)
        sharedPreferences.edit().putString("nombre_usuario", nombreUsuario).apply()

        Log.d("SharedPreferences", "Nombre de usuario actualizado: $nombreUsuario")
    }

    private fun actualizarIdUsuarioEnSharedPreferences(idUsuario: Int) {
        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("id_usuario", idUsuario).apply()

        Log.d("SharedPreferences", "ID de usuario actualizado: $idUsuario")
    }


    private fun togglePasswordVisibility(editText: EditText) {
        // Cambia dinámicamente el tipo de entrada del EditText entre textPassword y text
        val isPasswordVisible = editText.transformationMethod is PasswordTransformationMethod

        if (isPasswordVisible) {
            // Si la contraseña es visible, ocultarla
            editText.transformationMethod = null
        } else {
            // Si la contraseña está oculta, mostrarla
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        // Mueve el cursor al final del texto después de cambiar la visibilidad
        editText.setSelection(editText.text.length)
    }

    private fun snackBarMessage(message: Any) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "",
            Snackbar.LENGTH_LONG
        )
        val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
        val resources = resources
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            36f,
            resources.displayMetrics
        ).toInt()
        params.setMargins(49, 0, 49, margin)
        snackbar.view.layoutParams = params
        val shapeDrawable = GradientDrawable().apply {
            cornerRadius =
                resources.getDimension(R.dimen.default_radius) // Radio de las esquinas redondeadas
            setColor(
                ContextCompat.getColor(
                    this@LoginActivity,
                    R.color.color_alert_message_login
                )
            )
            setStroke(
                resources.getDimensionPixelSize(R.dimen.snackbar_border_width),
                ContextCompat.getColor(this@LoginActivity, R.color.color_boder_alert_message_login)
            )
        }

        snackbar.view.background = shapeDrawable

        val inflater = LayoutInflater.from(this@LoginActivity)
        val customLayout = inflater.inflate(R.layout.layout_image_snackbar_login, null)
        val textMessageView = customLayout.findViewById<TextView>(R.id.textViewSnackbarLogin)

        when (message) {
            is Int -> textMessageView.setText(message)
            is String -> textMessageView.text = message
        }

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        snackbarLayout.addView(customLayout, 0)
        snackbar.show()
    }

}