package cn.campusapp.router;

import android.content.Context;

import cn.campusapp.router.route.CallbackRoute;
import cn.campusapp.router.route.IRoute;
import cn.campusapp.router.router.ActivityRouter;
import cn.campusapp.router.router.BrowserRouter;
import cn.campusapp.router.router.CallbackRouter;
import cn.campusapp.router.router.IActivityRouteTableInitializer;
import cn.campusapp.router.router.ICallbackRouteTableInitializer;
import cn.campusapp.router.router.IRouter;

/**
 * Created by kris on 16/3/17.
 * shell to the user
 */
public class Router {


    public static synchronized void addRouter(IRouter router){
        RouterManager.getSingleton().addRouter(router);
    }

    public static synchronized void initBrowserRouter(Context context){
       RouterManager.getSingleton().initBrowserRouter(context);
    }

    public static synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer){
        RouterManager.getSingleton().initActivityRouter(context, initializer);
    }


    public static synchronized void initCallbackRouter(Context context, String scheme, ICallbackRouteTableInitializer initializer){
        RouterManager.getSingleton().initCallbackRouter(context, initializer, scheme);
    }

    public static synchronized void initCallbackRouter(Context context, ICallbackRouteTableInitializer initializer){
        RouterManager.getSingleton().initCallbackRouter(context, initializer);
    }


    public static synchronized void initActivityRouter(Context context, String scheme, IActivityRouteTableInitializer initializer){
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }

    public static void open(String url){
        RouterManager.getSingleton().open(url);
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     * @param url
     * @return
     */
    public static IRoute getRoute(String url){
        return RouterManager.getSingleton().getRoute(url);
    }


    public static void openRoute(IRoute route){
        RouterManager.getSingleton().openRoute(route);
    }

    public static void setActivityRouter(ActivityRouter router){
        RouterManager.getSingleton().setActivityRouter(router);
    }

    public static void setBrowserRouter(BrowserRouter router){
        RouterManager.getSingleton().setBrowserRouter(router);
    }

    public static void setCallbackRouter(CallbackRouter router){
        RouterManager.getSingleton().setCallbackRouter(router);
    }

}
