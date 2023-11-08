import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.uniovi.eii.practicakotlin.Foto
import es.uniovi.eii.practicakotlin.FotoDao

@Database(entities = [Foto::class], version = 1, exportSchema = false)
abstract class FotosDB : RoomDatabase() {
    abstract fun fotoDao() : FotoDao

    companion object
    {
        @Volatile
        private var INSTANCE : FotosDB? = null

        fun getDB(context : Context) : FotosDB {

            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(context.applicationContext, FotosDB::class.java, "fotosdb").build()
                INSTANCE = instance
                instance
            }
        }
    }
}