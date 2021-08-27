package com.maison.mona.converter

import com.maison.mona.entity.BadgeOptArgs
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.json.JSONObject
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class BadgeOptArgsConverterTest {
    private val badgeOptArgs = BadgeOptArgs("bidon")
    private val badgeOptArgsNull = BadgeOptArgs(null)

    private val jsonBadgeOpt = JSONObject("{\"tag\":\"bidon\"}")
    private val jsonBadgeOptNull = JSONObject("{\"tag\":null}")

    private val badgeOptArgsConverterSpy = spyk(BadgeOptArgsConverter())
    @Nested
    inner class toOptArgs{
        @Test
        fun toOptArgs_notNull() {
            assert(badgeOptArgsConverterSpy.toOptArgs(jsonBadgeOpt.toString()) == badgeOptArgs)
        }
        @Test
        fun toOptArgs_null() {
            assert(badgeOptArgsConverterSpy.toOptArgs(jsonBadgeOptNull.toString()) == badgeOptArgsNull)
        }
    }

    @Nested
    inner class toJson{
        @Test
        fun toJson_notNull() {
            JSONAssert.assertEquals(badgeOptArgsConverterSpy.toJson(badgeOptArgs) ,jsonBadgeOpt, JSONCompareMode.LENIENT)
        }
        @Test
        fun toJson_null() {
            JSONAssert.assertEquals(badgeOptArgsConverterSpy.toJson(badgeOptArgsNull) ,jsonBadgeOptNull, JSONCompareMode.LENIENT)
        }
    }
}