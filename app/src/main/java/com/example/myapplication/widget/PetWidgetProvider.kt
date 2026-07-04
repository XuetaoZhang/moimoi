package com.example.myapplication.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.myapplication.R
import com.example.myapplication.utils.WidgetHelper

class PetWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.pet_widget)

            // 加载保存的头像
            val bitmap = WidgetHelper.getAvatarBitmap(context)
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.widget_avatar, bitmap)
            }

            // 显示陪伴天数
            val days = WidgetHelper.getDaysCount(context)
            views.setTextViewText(R.id.widget_days, "${days}天")

            // 显示心情
            val mood = WidgetHelper.getMoodText()
            views.setTextViewText(R.id.widget_mood, mood)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
