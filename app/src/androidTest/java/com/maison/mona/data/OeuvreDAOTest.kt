package androidTest.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.maison.mona.data.OeuvreDAO
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.entity.Artist
import com.maison.mona.entity.Bilingual
import com.maison.mona.entity.Location
import com.maison.mona.entity.Oeuvre
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import util.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
internal class OeuvreDAOTest: TestCase() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var oeuvreDatabase: OeuvreDatabase
    private lateinit var oeuvreDAO: OeuvreDAO

    private val oeuvre = Oeuvre(1,2, "First Artwork", "Collectionnez une oeuvre pour obtenir ce badge.", Bilingual("allo","bye"),
        null, null, null, listOf(Bilingual("allo","bye")), listOf(Artist(2, "shrimp", "sh",true)), "",
        Location(1.2,13.12), "2021-04-21 07:39:01", "public", "place", null, null, null,null,null)
    private val oeuvreArtwork = Oeuvre(1,2, "First Artwork", "Collectionnez une oeuvre pour obtenir ce badge.", Bilingual("allo","bye"),
        null, null, null, listOf(Bilingual("allo","bye")), listOf(Artist(2, "shrimp", "sh",true)), "",
        Location(1.2,13.12), "2021-04-21 07:39:01", "public", "artwork", null, null, null,null,null)
    @Before
    public override fun setUp() {
        super.setUp()
        oeuvreDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            OeuvreDatabase:: class.java
        ).allowMainThreadQueries().build()

        oeuvreDAO = oeuvreDatabase.oeuvreDAO()
    }
    @After
    fun closedb(){
        oeuvreDatabase.close()
    }

    @Test
    fun insertAll() = runBlockingTest{
        oeuvreDAO.insertAll(listOf(oeuvre))

        val allOeuvre = oeuvreDAO.getAll().getOrAwaitValue()
        assertThat(allOeuvre).contains(oeuvre)
    }

    @Test
    fun insert() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)

        val allOeuvre = oeuvreDAO.getAll().getOrAwaitValue()
        assertThat(allOeuvre).contains(oeuvre)
    }

    @Test
    fun updateRating() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)
        oeuvreDAO.updateRating(1,1.2f,"wow",2,"0000:00:00")

        val allOeuvre = oeuvreDAO.getAll().getOrAwaitValue()
        assertEquals(allOeuvre[0].rating,1.2f)
        assertEquals(allOeuvre[0].comment,"wow")
        assertEquals(allOeuvre[0].state,2)
        assertEquals(allOeuvre[0].date_photo,"0000:00:00")
    }

    @Test
    fun updatePath() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)
        oeuvreDAO.updatePath(1,"some path")

        val allOeuvre = oeuvreDAO.getAll().getOrAwaitValue()
        assertEquals(allOeuvre[0].photo_path,"some path")
    }

    @Test
    fun updateTarget() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)
        oeuvreDAO.updateTarget(1,4)

        val allOeuvre = oeuvreDAO.getAll().getOrAwaitValue()
        assertEquals(allOeuvre[0].state,4)
    }

    @Test
    fun getOeuvre() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)
        assertEquals(oeuvreDAO.getOeuvre(1),oeuvre)
    }

    @Test
    fun getType() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)
        oeuvreDAO.insert(oeuvreArtwork)

        val allOeuvre = oeuvreDAO.getType("place").getOrAwaitValue()

        assertThat(allOeuvre).doesNotContain(oeuvreArtwork)
        assertThat(allOeuvre).contains(oeuvre)
    }

    @Test
    fun getNotCollected() = runBlockingTest{
        oeuvreDAO.insert(oeuvre)

        val allOeuvre = oeuvreDAO.getNotCollected("place").getOrAwaitValue()

        assertThat(allOeuvre).doesNotContain(oeuvreArtwork)
        assertThat(allOeuvre).contains(oeuvre)
    }

    @Test
    fun getCollected() = runBlockingTest{
        val oeuvreUpdated = Oeuvre(1,2, "First Artwork", "Collectionnez une oeuvre pour obtenir ce badge.", Bilingual("allo","bye"),
            null, null, null, listOf(Bilingual("allo","bye")), listOf(Artist(2, "shrimp", "sh",true)), "",
            Location(1.2,13.12), "2021-04-21 07:39:01", "public", "place", 2, 1.2f, "wow",null,"0000:00:00")
        oeuvreDAO.insert(oeuvre)
        oeuvreDAO.insert(oeuvreArtwork)
        oeuvreDAO.updateRating(1,1.2f,"wow",2,"0000:00:00")
        val allOeuvre = oeuvreDAO.getCollected(2).getOrAwaitValue()

        assertThat(allOeuvre).doesNotContain(oeuvreArtwork)
        assertThat(allOeuvre).contains(oeuvreUpdated)
    }
}