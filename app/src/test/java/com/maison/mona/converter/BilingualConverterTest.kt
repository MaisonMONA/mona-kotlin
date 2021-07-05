package com.maison.mona.converter

import com.maison.mona.entity.Bilingual
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class BilingualConverterTest {

    private val bilingualTest = Bilingual(fr = "allo", en = "hello")
    private val bilingualJson = JSONObject("{\"fr\": \"allo\", \"en\": \"hello\"}")
    private val bilingualConverterSpy = spyk(BilingualConverter())

    @Test
    fun toBilingual() {
        bilingualConverterSpy.toBilingual(bilingualJson.toString())  shouldBe bilingualTest
    }

    @Test
    fun toJson() {
        JSONAssert.assertEquals(bilingualConverterSpy.toJson(bilingualTest) ,bilingualJson, JSONCompareMode.LENIENT)
    }
}