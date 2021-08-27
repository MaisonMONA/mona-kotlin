package com.maison.mona.converter

import com.maison.mona.entity.BadgeRequiredArgs
import io.mockk.spyk
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class BadgeRequiredArgsConverterTest {
    private val badgeRequiredArgs = BadgeRequiredArgs(2)
    private val jsonBadgeRequired = JSONObject("{\"nb_artworks\": 2}")
    private val badgeRequiredArgsConverterSpy = spyk(BadgeRequiredArgsConverter())

    @Test
    fun toRequiredArgs() {
        assert(badgeRequiredArgsConverterSpy.toRequiredArgs(jsonBadgeRequired.toString()) == badgeRequiredArgs)
    }

    @Test
    fun toJson() {
        JSONAssert.assertEquals(badgeRequiredArgsConverterSpy.toJson(badgeRequiredArgs) ,jsonBadgeRequired, JSONCompareMode.LENIENT)
    }
}