package com.almadevs.androidcurso.asistenciaapp
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.almadevs.androidcurso.R
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    private var isStartSelected:Boolean = true
    private var isPausaSelected:Boolean = false

    private lateinit var viewStart:CardView
    private lateinit var viewPausa:CardView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Obtener la referencia al TextView
        val textDate = findViewById<TextView>(R.id.textDate)
        val nombre_usuario = intent.getStringExtra("nombre_usuario")
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