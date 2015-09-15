package com.android.launcher20.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.launcher20.R;


/**
 * Created by Administrator on 2015/8/21.
 */
public class widgetProvider extends AppWidgetProvider{
    private static final String CLICK_NAME_ACTION = "com.terry.action.widget.click";
    public static boolean isChange=true;
    private static RemoteViews rv;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (rv == null) {
            rv = new RemoteViews(context.getPackageName(), R.layout.activity_main);
        }
        if (intent.getAction().equals(CLICK_NAME_ACTION)) {
            if (isChange) {
//                rv.setTextViewText(R.id.TextView01, context.getResources()
//                        .getString(R.string.load));
                rv.setImageViewResource(R.id.ali,R.drawable.ali);

            } else {
//                rv.setTextViewText(R.id.TextView01, context.getResources()
//                        .getString(R.string.change));
                rv.setImageViewResource(R.id.ali,R.drawable.back_top);
            }
            Toast.makeText(context, Boolean.toString(isChange),
                    Toast.LENGTH_LONG).show();
            isChange = !isChange;

        }
        AppWidgetManager appWidgetManger = AppWidgetManager
                .getInstance(context);
        int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(
                context, widgetProvider.class));
        appWidgetManger.updateAppWidget(appIds, rv);
    }

    public static void updateAppWidget(Context context,
                                       AppWidgetManager appWidgeManger, int appWidgetId) {
        rv = new RemoteViews(context.getPackageName(), R.layout.activity_main);
        Intent intentClick = new Intent(CLICK_NAME_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intentClick, 0);
//        rv.setOnClickPendingIntent(R.id.TextView01, pendingIntent);
        rv.setOnClickPendingIntent(R.id.ali,pendingIntent);
        appWidgeManger.updateAppWidget(appWidgetId, rv);
    }
}
