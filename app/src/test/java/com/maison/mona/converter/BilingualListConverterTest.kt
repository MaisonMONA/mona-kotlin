package com.maison.mona.converter

import com.maison.mona.entity.Bilingual
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.json.JSONArray
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class BilingualListConverterTest {
    private val bilingualTest = createBillingual(fr = "allo", en = "hello")
    private val bilingualTest2 = createBillingual(fr = "aurevoir", en = "bye")
    private val bilingualJson = JSONArray("[{\"fr\": \"allo\", \"en\": \"hello\"}, {\"fr\": \"aurevoir\", \"en\": \"bye\"}]")
    private val emptyJson = JSONArray("[{}]")
    private val bilingualListTest = listOf(bilingualTest,bilingualTest2)
    private val bilingualListTestEmpty = listOf<Bilingual>()
    private val bilingualListConverterSpy = spyk(BilingualListConverter())

    private fun createBillingual(
        fr: String = "",
        en: String = "",
    ) = Bilingual(
        fr = fr,
        en = en,
    )
    @Nested
    inner class toBilingualList {
        @Test
        fun toBilingualList() {
            val bilingualListConverterTest =
                bilingualListConverterSpy.toBilingualList(bilingualJson.toString())
            bilingualListConverterTest.shouldContainExactly(
                Bilingual(fr = "allo", en = "hello"),
                Bilingual(fr = "aurevoir", en = "bye")
            )
        }
        //TODO fix this
        @Test
        fun toBilingualList_Empty() {
            val bilingualListConverterTest =
                bilingualListConverterSpy.toBilingualList(emptyJson.toString())
            bilingualListConverterTest.shouldContainExactly(
                Bilingual(fr = "", en = "")
            )
        }
    }
    @Nested
    inner class toJson{
        @Test
        fun toJson() {
            JSONAssert.assertEquals(bilingualListConverterSpy.toJson(bilingualListTest) ,bilingualJson, JSONCompareMode.LENIENT)
        }

        @Test
        fun toJson_Empty() {
            bilingualListConverterSpy.toJson(bilingualListTestEmpty) shouldBe "[]"
        }
    }
}