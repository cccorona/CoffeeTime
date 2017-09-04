package mx.com.cesarcorona.coffeetime;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ccabrera on 31/08/17.
 */

public class CooffeTimeApplication extends MultiDexApplication{

        public static String TAG = CooffeTimeApplication.class.getSimpleName();
        public static int MAIN_EXPANTION_VERSION = 1;
        public static int PATCH_EXPANTION_VERSION = 0;



        @Override
        public void onCreate() {
            super.onCreate();
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/comic.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
        }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
