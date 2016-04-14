package cn.campusapp.androuter;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import java.util.Map;

import cn.campusapp.router.Router;
import cn.campusapp.router.router.IActivityRouteTableInitializer;
import cn.campusapp.router.router.ICallbackRouteTableInitializer;
import cn.campusapp.router.router.RouterCallback;
import timber.log.Timber;

/**
 * Created by kris on 16/3/11.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.initActivityRouter(getApplicationContext(), new IActivityRouteTableInitializer() {
            @Override
            public void initRouterTable(Map<String, Class<? extends Activity>> router) {
                router.put("hxstore://second/:{name}", SecondActivity.class);
                router.put("hxstore://third", ThirdActivity.class);
            }
        });
        Router.initBrowserRouter(getApplicationContext());
        Router.initCallbackRouter(getApplicationContext(), new ICallbackRouteTableInitializer() {
            @Override
            public void initRouterTable(Map<String, RouterCallback> router) {
                router.put("hxstore://toast", new RouterCallback() {
                    @Override
                    public void run(RouteContext context) {
                        Toast.makeText(context.getContext(), context.getParams().get("msg"), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        Timber.plant(new Timber.DebugTree());
    }
}
