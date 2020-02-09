import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.mona.Entity.Oeuvre

@Database(entities = [Oeuvre::class], version = 1,exportSchema = false)
@TypeConverters(OeuvreConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oeuvreDao(): OeuvreDAO
}









