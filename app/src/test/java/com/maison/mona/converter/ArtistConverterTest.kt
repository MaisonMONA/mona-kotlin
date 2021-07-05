package com.maison.mona.converter

import com.maison.mona.entity.Artist
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.json.JSONArray
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class ArtistConverterTest {
    private val artistTest1 = createArtist(1,"bidon", "b",true)
    private val artistTest2 = createArtist(2, "test", "t", false)
    private val artistTestList = listOf(artistTest1,artistTest2)
    private val jsonArtistList = JSONArray("[{\"id\": 1, \"name\":\"bidon\", \"alias\":\"b\", \"collective\": true},{\"id\": 2, \"name\":\"test\", \"alias\":\"t\", \"collective\": false}]")
    private val artistConverterSpy = spyk(ArtistConverter())

    private fun createArtist(
        id: Int = 0,
        name: String = "",
        alias: String = "",
        collective: Boolean
    ) = Artist(
        id = id,
        name = name,
        alias = alias,
        collective = collective
    )
    @Test
    fun toArtists() {
        val artistListConverterTest = artistConverterSpy.toArtists(jsonArtistList.toString())
        artistListConverterTest.shouldContainExactly(
            Artist(1,"bidon", "b",true),
            Artist(2, "test", "t", false)
        )
    }

    @Test
    fun toJson() {
        JSONAssert.assertEquals(artistConverterSpy.toJson(artistTestList) ,jsonArtistList, JSONCompareMode.LENIENT)
    }
}