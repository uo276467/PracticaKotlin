package es.uniovi.eii.practicakotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import coil.load

class FotoAdapter(
    private var listaFotos: List<Foto> = emptyList(),
    //¿A qué te recuerda esto?
    private val onItemSelected: (Foto) -> Unit,


) :
RecyclerView.Adapter<FotoAdapter.FotoViewHolder>() {


    private lateinit var  context : Context


    class FotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView = itemView.findViewById<ImageView>(R.id.ivFoto)
        private val tvTitulo = itemView.findViewById<TextView>(R.id.tvTitulo);
        private val rootView = itemView.rootView;


        fun bindView(foto: Foto, onItemSelected : (Foto) -> Unit ) {
            /* Coil : https://github.com/coil-kt/coil
             * Carga de imágenes apoyada en corrutinas.
            */
            imgView.load(foto.url) {
                crossfade(true)
                crossfade(2000)
            }
            tvTitulo.text = foto.titulo

            //Listener
            rootView.setOnClickListener { onItemSelected(foto) };
        }
    }


    /*
     *  Notificación de cambios.
     */
    fun update(listaFotos : List<Foto>) {
        this.listaFotos = listaFotos

        //Cambiamos la totalidad de la lista. Echa un ojo al warning para otros casos.
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {

        context = parent.context

        return FotoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_fotos, parent, false)
        )
    }

    override fun getItemCount(): Int = listaFotos.size

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int) =
        holder.bindView(listaFotos[position],onItemSelected)

}