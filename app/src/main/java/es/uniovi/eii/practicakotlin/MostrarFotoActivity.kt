package es.uniovi.eii.practicakotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.io.File
import java.io.FileWriter

class MostrarFotoActivity : AppCompatActivity() {

    //Los companion objects son similares al static que ya conocéis.
    companion object {
        const val URL = "URL";
        const val TITULO = "TITULO";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_foto)

        //No puede ser nulo. De ser así, pasamos a empty.
        val url = intent.getStringExtra(URL).orEmpty()
        val titulo = intent.getStringExtra(TITULO).orEmpty()

        //Simplemente mostramos textos.
        findViewById<TextView>(R.id.tvURL).text = url
        findViewById<TextView>(R.id.tvTitulo).text = titulo
    }

}