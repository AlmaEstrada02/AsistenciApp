package com.almadevs.androidcurso.asistenciaapp
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.almadevs.androidcurso.LoginActivity
import com.almadevs.androidcurso.R
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    private var isStartSelected:Boolean = true
    private var isPausaSelected:Boolean = false

    private lateinit var viewStart:CardView
    private lateinit var viewPausa:CardView
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val nombre_usuario = sharedPreferences.getString("nombre_usuario", "")
        if (nombre_usuario.isNullOrEmpty()) {
            val intent = intent
            val nombre_usuarioIntent = intent.getStringExtra("nombre_usuario")
            // Guardar el nombre del usuario en SharedPreferences
            sharedPreferences.edit().putString("nombre_usuario", nombre_usuarioIntent).apply()
        }

        //modal menu cierre de sesion
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarHomePage)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
        val builder = AlertDialog.Builder(this)
        val buttonAceptar = dialogView.findViewById<Button>(R.id.buttonAceptar)


        builder.setView(dialogView)
        val dialog = builder.create()

        buttonAceptar.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDate)
        findViewById<TextView>(R.id.nombreEmpleado).text = nombre_usuario

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)

        // Establecer la fecha actual en el TextView
        textDate.text = fechaActual
        initComponents()
        initListeners()
        initUI()

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


    private fun initComponents(){
        viewStart = findViewById(R.id.start)
        viewPausa = findViewById(R.id.pausa)
    }

    private fun initListeners(){
        viewStart.setOnClickListener {
            changeGender()
            setGenderColor() }
        viewPausa.setOnClickListener {
            changeGender()
            setGenderColor()}
    }

    private fun changeGender(){
        isStartSelected = !isStartSelected
        isPausaSelected = !isPausaSelected
    }

    private fun setGenderColor(){
        viewStart.setCardBackgroundColor(getBackgroundColor(isStartSelected))
        viewPausa.setCardBackgroundColor(getBackgroundColor(isPausaSelected))
    }

    private fun getBackgroundColor(isSelectedComponet:Boolean):Int {
        val colorReference = if(isSelectedComponet){
            R.color.title_text
        }else{
            R.color.colorPrimaryCoppelAzul2
        }
        return ContextCompat.getColor(this, colorReference)
    }

    private fun initUI(){
        setGenderColor()
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


}