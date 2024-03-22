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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class HomePrivilegeActivity : AppCompatActivity() {
    private var isStartSelected:Boolean = true
    private var isPausaSelected:Boolean = false
    private lateinit var viewStart: CardView
    private lateinit var viewPausa: CardView
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

        if (nombre_usuario.isNullOrEmpty()) {
            val intent = intent
            val nombre_usuarioIntent = intent.getStringExtra("nombre_usuario")
            // Guardar el nombre del usuario en SharedPreferences
            sharedPreferences.edit().putString("nombre_usuario", nombre_usuarioIntent).apply()
        }

        //val nombre_usuario = intent.getStringExtra("nombre_usuario")
        findViewById<TextView>(R.id.nombreEmpleadoPrivilege).text = nombre_usuario
        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDatePrivilege)
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(calendar.time)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.collectionMenuBottomNavigation)
        //Obtener referencia del menu mas
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarHomePage)

        //Terminos y condiciones desde el menu
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
        val buttonAceptar = dialogView.findViewById<Button>(R.id.buttonAceptar)

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
        initComponents()
        initListeners()
        initUI()

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

    private fun navigateToReport() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToListEmpl(){
        val intent = Intent( this, ListEmpActivity::class.java)
        startActivity(intent)
    }

    private fun initComponents(){
        viewStart = findViewById(R.id.startPtivilege)
        viewPausa = findViewById(R.id.pausaPrivilege)
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

}