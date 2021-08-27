package com.maison.mona.converter

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.json.JSONArray
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class DimensionConverterTest {
    private val dimensionTest = listOf("1.631.83","1.42","1.14","m","10.2","cm")
    private val dimensionTestEmpty = listOf<Any>()
    private val dimensionJson = JSONArray("[\"1.631.83\",\"1.42\",\"1.14\",\"m\",\"10.2\",\"cm\"]")
    private val emptyJson = JSONArray("[]")
    private val dimensionConverterSpy = spyk(DimensionConverter())
    //Object [("dimension", ["1.631.83", "1.42", "1.14", "m", "10.2", "cm"])]
    @Nested
    inner class ToDimension{
        @Test
        fun toDimension() {
            val toDimensionTest = dimensionConverterSpy.toDimension(dimensionJson.toString())
            toDimensionTest.shouldContainExactly(
                "1.631.83","1.42","1.14","m","10.2","cm"
            )
        }

        @Test
        fun toDimension_Empty() {
            val toDimensionTest = dimensionConverterSpy.toDimension(emptyJson.toString())
            toDimensionTest shouldBe listOf<Any?>()
        }
    }

    @Nested
    inner class ToJson {
        @Test
        fun toJson() {
            JSONAssert.assertEquals(dimensionConverterSpy.toJson(dimensionTest), dimensionJson, JSONCompareMode.LENIENT)
        }
        @Test

        fun toJson_Empty() {
            dimensionConverterSpy.toJson(dimensionTestEmpty) shouldBe "[]"
        }
    }
}