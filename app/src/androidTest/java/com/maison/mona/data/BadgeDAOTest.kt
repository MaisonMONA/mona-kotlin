package androidTest.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.maison.mona.data.BadgeDAO
import com.maison.mona.data.BadgeDatabase
import com.maison.mona.entity.Badge
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
internal class BadgeDAOTest: TestCase() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var badgeDatabase: BadgeDatabase
    private lateinit var badgeDAO: BadgeDAO

    val badge = Badge(1,"Première oeuvre", "First Artwork", "Collectionnez une oeuvre pour obtenir ce badge.", "Collect one artwork to obtain this badge.",
        null, null, null, "general_collection", "1", "", "2021-04-21 07:39:01", "2021-04-21 07:39:01", "public", false, 1)

    @Before
    public override fun setUp() {
        super.setUp()
        badgeDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BadgeDatabase:: class.java
        ).allowMainThreadQueries().build()

        badgeDAO = badgeDatabase.badgesDAO()
    }
    @After
    fun closedb(){
        badgeDatabase.close()
    }

    @Test
    fun insertAll() = runBlockingTest{
       badgeDAO.insertAll(listOf(badge))

        val allBadges = badgeDAO.getAll().getOrAwaitValue()
        assertThat(allBadges).contains(badge)
    }

    @Test
    fun updateCollected() = runBlockingTest{
        badgeDAO.insertAll(listOf(badge))
        badgeDAO.updateCollected(1,true)

        val allBadges = badgeDAO.getAll().getOrAwaitValue()
        assertThat(allBadges[0].isCollected)
    }

    @Test
    fun setGoal() = runBlockingTest{
        badgeDAO.insertAll(listOf(badge))
        badgeDAO.setGoal(1,2)

        val allBadges = badgeDAO.getAll().getOrAwaitValue()
        assertThat(allBadges[0].goal==2)
    }
}