package cn.campusapp.router.router;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.campusapp.router.exception.InvalidRoutePathException;
import cn.campusapp.router.route.CallbackRoute;
import cn.campusapp.router.route.IRoute;
import cn.campusapp.router.tools.ActivityRouteRuleBuilder;
import cn.campusapp.router.utils.UrlUtils;
import timber.log.Timber;

import static cn.campusapp.router.utils.UrlUtils.getHost;
import static cn.campusapp.router.utils.UrlUtils.getPathSegments;
import static cn.campusapp.router.utils.UrlUtils.getScheme;

/**
 * Created by chsasaw on 16/4/14.
 */
public class CallbackRouter extends BaseRouter {
    private static final String TAG = "CallbackRouter";
    private static String MATCH_SCHEME = "hxstore";

    private static final Set<String> HOSTS_CAN_OPEN = new LinkedHashSet<>();

    static CallbackRouter mSharedCallbackRouter = new CallbackRouter();

    Context mBaseContext;

    Map<String, RouterCallback> mRouteTable = new HashMap<>();

    static {
        CAN_OPEN_ROUTE = CallbackRoute.class;
    }

    public static CallbackRouter getSharedRouter(){
        return mSharedCallbackRouter;
    }

    public void init(Context appContext, ICallbackRouteTableInitializer initializer) {
        mBaseContext = appContext;
        initializer.initRouterTable(mRouteTable);
        for(String pathRule : mRouteTable.keySet()){
            boolean isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule);
            if(!isValid){
                Timber.e(new InvalidRoutePathException(pathRule), "");
                mRouteTable.remove(pathRule);
                HOSTS_CAN_OPEN.remove(getHost(pathRule));
            }else {
                HOSTS_CAN_OPEN.add(getHost(pathRule));
            }
        }
    }


    @Override
    public IRoute getRoute(String url) {
        return new CallbackRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {

        return CAN_OPEN_ROUTE.equals(route.getClass());
    }


    @Override
    public boolean canOpenTheUrl(String url) {
        return TextUtils.equals(getScheme(url), MATCH_SCHEME) && HOSTS_CAN_OPEN.contains(getHost(url));
    }

    public void setMatchScheme(String scheme){
        MATCH_SCHEME = scheme;
    }

    public String getMatchScheme(){
        return MATCH_SCHEME;
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return CAN_OPEN_ROUTE;
    }

    @Override
    public void open(IRoute route) {
        if(route instanceof CallbackRoute){
            CallbackRoute aRoute = (CallbackRoute) route;
            RouterCallback callback = match(aRoute);

            RouterCallback.RouteContext context = new RouterCallback.RouteContext(UrlUtils.getParameters(aRoute.getUrl()), ((CallbackRoute) route).getExtras(), mBaseContext);
            callback.run(context);
        }
    }

    @Override
    public void open(String url) {
        open(getRoute(url));
    }

    /**
     * host 和path匹配称之为路由匹匹配
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(CallbackRoute route) {
        List<String> givenPathSegs = route.getPath();
        OutLoop:
        for(String routeUrl : mRouteTable.keySet()){
            List<String> routePathSegs = getPathSegments(routeUrl);
            if(!TextUtils.equals(getHost(routeUrl), route.getHost())){
                continue;
            }
            if(givenPathSegs.size() != routePathSegs.size()){
                continue;
            }
            for(int i=0;i<routePathSegs.size();i++){
                if(!routePathSegs.get(i).startsWith(":")
                        &&!TextUtils.equals(routePathSegs.get(i), givenPathSegs.get(i))) {
                    continue OutLoop;
                }
            }
            //find the match route
            return routeUrl;
        }

        return null;
    }

    @Nullable
    private RouterCallback match(CallbackRoute route) {
        String matchedRoute = findMatchedRoute(route);
        if(matchedRoute == null){
            return null;
        }
        RouterCallback callback = mRouteTable.get(matchedRoute);

        return callback;
    }
}
