package com.eiffelyk.mywiget.wiget.clock

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.RemoteViews
import com.eiffelyk.mywiget.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ClockAppWidgetConfigureActivity]
 */
class ClockAppWidget : AppWidgetProvider() {
    private var mTimer = Timer()
    private var mAppWidgetManager: AppWidgetManager? = null
    private var mContext: Context? = null

    //将0-9的液晶数字图片定义为数组
    private val digits = intArrayOf(
        R.mipmap.shuzi0,
        R.mipmap.shuzi1,
        R.mipmap.shuzi2,
        R.mipmap.shuzi3,
        R.mipmap.shuzi4,
        R.mipmap.shuzi5,
        R.mipmap.shuzi6,
        R.mipmap.shuzi7,
        R.mipmap.shuzi8,
        R.mipmap.shuzi9
    )

    //将显示小时、分钟、秒钟的ImageView定义为数组
    private val digitViews = intArrayOf(
        R.id.img01,
        R.id.img02,
        R.id.img04,
        R.id.img05,
        R.id.img07,
        R.id.img08
    )

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        mAppWidgetManager = appWidgetManager
        mContext = context
        //定义计时器
        mTimer = Timer()
        //启动周期性调度
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                //发送空消息，通知界面更新
                handler.sendEmptyMessage(0x123)
            }
        }, 0, 1000)
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        @SuppressLint("SimpleDateFormat")
        override fun handleMessage(msg: Message) {
            if (msg.what == 0x123) {
                val views = RemoteViews(mContext!!.packageName, R.layout.clock_app_widget)
                //定义SimpleDateFormat对象
                val df = SimpleDateFormat("HHmmss")
                //将当前时间格式化为HHmmss的形式
                val timeStr = df.format(Date())
                for (i in timeStr.indices) {
                    //将第i个数字字符zh转换为对应的数字
                    val num = timeStr[i].code - 48
                    //将第i个图片设为对应的液晶数字图片
                    views.setImageViewResource(digitViews[i], digits[num])
                }
                //将APPWidgetProvider子类实例包装成ComponentName对象
                val componentName = ComponentName(mContext!!, ClockAppWidget::class.java)
                //调用APPWidgetManager将RemoteViews添加到ComponentName中
                mAppWidgetManager!!.updateAppWidget(componentName, views)
            }
            super.handleMessage(msg)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.clock_app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}