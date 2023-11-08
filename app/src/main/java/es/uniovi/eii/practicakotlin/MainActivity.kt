package es.uniovi.eii.practicakotlin

import FotosDB
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity() : AppCompatActivity() {

    //Pon el ratón sobre lateinit. Lee.
    //¿Qué implica?

    private lateinit var recyclerView : RecyclerView;
    private lateinit var fotoAdapter : FotoAdapter
    private  var fotosDb : FotosDB = FotosDB.getDB(this)



    /**
     * onCreate.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Nota: Nuestro recycler está oculto por defecto.
        //Porque mostramos una barra de carga hasta que se lean los datos.
        recyclerView = findViewById(R.id.recyclerFotos)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)

        //Uso de expresiones lambda.
        fotoAdapter = FotoAdapter { it -> mostrarFoto(it)}

        recyclerView.adapter = fotoAdapter



        Log.d("corrutinas", "Primero - Llamada a carga de ficheros")

        //0 -> Secuencial . 1 -> Corrutinas. Carga un único CSV; 2 -> Corrutinas. Carga ambos CSVs.
        cargarFicheros(2)

        Log.d("corrutinas", "Tercero - Posterior a la llamada cargarFicheros")


    }


    private fun cargarFicheros(tipo : Int) {
        //Os debería recordar al switch: https://kotlinlang.org/docs/control-flow.html#if-expression
        when(tipo){
            0 -> cargaSecuencial()
            1 -> cargaUnico()
            2 -> cargaMultiple()
        }
    }

    private fun cargarFotos(){
        var fotos = loadAnimalsFromCsv("animales.csv")
        val progressBar = findViewById<ProgressBar>(R.id.progressBarCarga)
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    private fun cargaSecuencial() {
        fotoAdapter.update(loadAnimalsFromCsv("animales.csv"))
        val progressBar = findViewById<ProgressBar>(R.id.progressBarCarga)
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }


    /*
     * Las corrutinas se apoyan en hilos (o un pool) para realizar una ejecución concurrente.
     * El desarrollo del código es muy similar al código síncrono.
     * Los hilos se bloquean, las corrutinas se supenden. Es decir, el hilo sigue libre.
     * ¿Recuerdas lo que era una continuación?
     *
     * Tienen, además, un ámbito. Básicamente indicamos tu tiempo de vida.
     *  - GlobalScope : Mientras la app esté funcionando.
     *  - lifecycleScope: Mientras la Activity o el Fragment estén activos.
     *  - viewModelScope: Hasta que el ViewModel se destruya -irrelevante por ahora-.
     *  - Tenéis otras como RunBlocking o CoroutineScope.
     *
     * Por otra parte tenemos los dispatchers o despachadores.
     * Ayudan a decidir en qué hilo/s debe ejecutarse una corrutina.
     *
     *  - Dispatchers.Main: Cuando es necesario interactuar con la UI.
     *  - Dispatchers.IO: Operaciones tipo BD, peticiones red, lectura/escritura ficheros.
     *  - Dispatchers.Default: Uso intensivo de la CPU.
     *
     *
     * Por último, lanzaremos la corrutina -generalmente- con launch o async.
     * Y lo veremos con un par de ejemplos.
     *
     * El resumen es:
     *
     * ámbitoDeLaCorrutina.launch/async(Dispatcher) { código }
     *
     */


    private fun cargaUnico() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            val listaFotos = loadAnimalsFromCsv("animales.csv")
            repintar(listaFotos)
            Log.d("corrutinas", "Segundo - Acabó la corrutina para cargaUnico()")
        }

    }

    private suspend fun repintar(listaFotos: List<Foto>){
        withContext(Dispatchers.Main)
        {
            fotoAdapter.update(listaFotos)
            val progressBar = findViewById<ProgressBar>(R.id.progressBarCarga)
            progressBar.visibility = View.INVISIBLE
            recyclerView.visibility = View.VISIBLE
            Toast.makeText(this@MainActivity, "Fotos cargadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargaMultiple()  {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            val tarea1 = async { loadAnimalsFromCsv("animales.csv") } //El dispatcher es el del ámbito del padre (IO)
            val tarea2 = async { loadAnimalsFromCsv("animales2.csv") }
            val listaAnimales1 = tarea1.await()
            val listaAnimales2 = tarea2.await()
            val listaFotos = listaAnimales1 + listaAnimales2
            withContext(Dispatchers.Main)
            {
                fotoAdapter.update(listaFotos)
                val progressBar = findViewById<ProgressBar>(R.id.progressBarCarga)
                progressBar.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity, "Fotos cargadas", Toast.LENGTH_SHORT).show()
            }
            Log.d("corrutinas", "Segundo - Acabó la corrutina para cargaMultiple()")
        }
    }


    private fun loadAnimalsFromCsv(nombreFichero : String): List<Foto> {
        //Si piensas que aquí las excepciones no existen... Lo siento.
        //Queda de tu mano investigar cómo se gestionan aquí.

        val isr = InputStreamReader(assets.open(nombreFichero))
        val reader = BufferedReader(isr)
        //Cabecera
        reader.readLine()
        //Para cada línea
        return reader.lineSequence()
            //Filtramos líneas en blanco. it es la línea actual.
            .filter { it.isNotBlank() }
            //Transformamos la línea (string) en objeto Foto.
            .map {
                val (url, titulo) = it.split(';', limit = 2) //limit -> Máximo de trozos.
                Foto(url, titulo)
            }.toList()
    }

    /**
     * Lo utilizaremos como listener mediante expresiones lambda.
     */
    private fun mostrarFoto(foto : Foto) {
        val intent = Intent(this, MostrarFotoActivity::class.java)
        intent.putExtra(MostrarFotoActivity.URL, foto.url)
        intent.putExtra(MostrarFotoActivity.TITULO, foto.titulo)
        startActivity(intent)
    }

}