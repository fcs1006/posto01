package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Auto Posto 01 app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Auto Posto 01", appName)
    }

    @Test
    fun `test fuel discount and loyalty calculation`() {
        val volumeLiters = 35.0
        val regularPrice = 6.19
        val clubPrice = 5.89
        val discountPerLiter = 0.30

        val subtotal = volumeLiters * regularPrice
        val discount = volumeLiters * discountPerLiter
        val totalWithDiscount = subtotal - discount

        assertEquals(216.65, subtotal, 0.01)
        assertEquals(10.50, discount, 0.01)
        assertEquals(206.15, totalWithDiscount, 0.01)

        val pointsEarned = (volumeLiters * 2).toInt()
        assertEquals(70, pointsEarned)

        val cashbackEarned = totalWithDiscount * 0.01
        assertTrue(cashbackEarned > 2.0)
    }

    @Test
    fun `test ethanol vs gasoline parity rule`() {
        val ethanol = 3.69
        val gasoline = 5.89
        val ratio = ethanol / gasoline
        // 3.69 / 5.89 is ~0.626 (< 0.70)
        assertTrue("Etanol should be advantageous under 70%", ratio <= 0.70)
    }
}
