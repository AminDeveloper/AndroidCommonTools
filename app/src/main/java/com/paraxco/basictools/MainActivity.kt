package com.paraxco.basictools

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.paraxco.basictools.Commontools.Observers.ObserverList
import com.paraxco.basictools.Commontools.Observers.TestObserver
import com.paraxco.commontools.BroadCastReceiver.NetworkChangeReceiver
import com.paraxco.commontools.Observers.RetryHelper
import com.paraxco.commontools.utils.RetryHelper.DefaultNetworkErrorDialog
import com.paraxco.commontools.utils.SmartLogger
import com.paraxco.commontools.utils.Utils
import com.paraxco.commontools.utils.doAsync
import com.paraxco.commontools.utils.uiThread
import kotlinx.android.synthetic.main.main_activity.*

/**
 *
 */

class MainActivity : Activity(), TestObserver.ObserverTest {
    var retryHelper: RetryHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkChangeReceiver.PingBeforeInform = true
        NetworkChangeReceiver.pingMechanism = { host, timeout -> Utils.isConnectedToThisServer(host,timeout) }

        DefaultNetworkErrorDialog.startShowingDefaultNetworkErrorDialog(this)
        SmartLogger.initLogger(applicationContext)
        setContentView(R.layout.main_activity)
        testMethod()
        showNotificationTest.setOnClickListener({
            startNotificationBadgeTest()
        })

        ObserverTest.setOnClickListener({
            ObserverList.getTestObserver().informObservers(listOf("abc", "cde"))
        })
        ObserverList.getTestObserver().addObserver(this)

        RetryHelperTest.setOnClickListener {
            retryHelper = RetryHelper.getInstanceAndCall(this, {
                SmartLogger.logDebug("doing ...")
                Thread.sleep(500)
                retry()
            })
        }

    }

    private fun testMethod() {

        testmethod2()
    }

    private fun testmethod2() {
        SmartLogger.logDebug("hello ")
        SmartLogger.logError("Error! ")

    }

    private fun retry() {
        doAsync {
            uiThread {
                retryHelper!!.retry()

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        ObserverList.getTestObserver().removeObserver(this)
        DefaultNetworkErrorDialog.stopShowingDefaultNetworkErrorDialog(this)


    }

    override fun observeChanges(list: MutableList<String>?) {
        Toast.makeText(this, list!!.size.toString(), Toast.LENGTH_LONG).show()
    }


    private fun startNotificationBadgeTest() {
        val myIntent = Intent(this, NotificationBadgeTest::class.java)
//        myIntent.putExtra("key", value) //Optional parameters
        this.startActivity(myIntent)
    }


//    private fun startPage(NextActivity: Class<*>) {
//        val myIntent = Intent(this, NextActivity::class.java)
////        myIntent.putExtra("key", value) //Optional parameters
//        this.startActivity(myIntent)
//    }
}