package com.paraxco.basictools

import android.content.Intent
import android.os.Bundle
import com.paraxco.commontools.Activities.BaseActivity
import com.paraxco.commontools.utils.NotificationHelper
import com.paraxco.commontools.utils.SmartLogger
import com.paraxco.commontools.utils.Utils
import kotlinx.android.synthetic.main.notification_badge_test.*

/**
 *
 */
class NotificationBadgeTest : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_badge_test)
        button.setOnClickListener { showNotification() }
        checkIntent(intent)
        NotificationHelper.refreshnotificationCount(this)

    }

    override fun onResume() {
        super.onResume()

    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        SmartLogger.logDebug()
    }


    private fun showNotification() {
        Utils.createNotification(this, NotificationBadgeTest::class.java, R.drawable.ic_launcher_foreground, "title", "hello", "type")
    }


}