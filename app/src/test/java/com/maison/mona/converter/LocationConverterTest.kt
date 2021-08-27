package com.maison.mona.converter

import com.maison.mona.entity.Location
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class LocationConverterTest {
    private val locationTest = Location(lat= 45.495655,lng=-73.556423)
    private val locationJson = JSONObject("{\"lat\": 45.495655,\"lng\": -73.556423}")
    private val locationConverterSpy = spyk(LocationConverter())

    @Test
    fun toLocation() {
        locationConverterSpy.toLocation(locationJson.toString()) shouldBe locationTest
    }

    @Test
    fun toJson() {
        JSONAssert.assertEquals(locationConverterSpy.toJson(locationTest) ,locationJson, JSONCompareMode.LENIENT)
    }
}
