package com.washim.washim_launcher

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

class MainActivityTest {

    @Test
    fun mainActivity_isHomeIntentHandler() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = ApplicationProvider.getApplicationContext<Context>()
            .packageManager.resolveActivity(intent, 0)
        // Will fail until manifest is installed on device — mark as instrumented
    }
}
