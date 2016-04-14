package cn.campusapp.router.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.campusapp.router.BuildConfig;
import cn.campusapp.router.exception.InvalidRoutePathException;
import cn.campusapp.router.exception.InvalidValueTypeException;
import cn.campusapp.router.exception.RouteNotFoundException;
import cn.campusapp.router.route.ActivityRoute;
import cn.campusapp.router.route.IRoute;
import cn.campusapp.router.tools.ActivityRouteRuleBuilder;
import cn.campusapp.router.utils.UrlUtils;
import timber.log.Timber;

import static cn.campusapp.router.utils.UrlUtils.getHost;
import static cn.campusapp.router.utils.UrlUtils.getPathSegments;
import static cn.campusapp.router.utils.UrlUtils.getScheme;

/**
 * Created by kris on 16/3/10.
 */
public class ActivityRouter extends BaseRouter {
    private static final String TAG = "ActivityRouter";
    private static String MATCH_SCHEME = "hxstore";
    private static final Set<String> HOSTS_CAN_OPEN = new LinkedHashSet<>();

    static ActivityRouter mSharedActivityRouter = new ActivityRouter();
    Context mBaseContext;
    Map<String, Class<? extends Activity>> mRouteTable = new HashMap<>();

    static {
        CAN_OPEN_ROUTE = ActivityRoute.class;
    }

    public static ActivityRouter getSharedRouter(){
        return mSharedActivityRouter;
    }

    public void init(Context appContext, IActivityRouteTableInitializer initializer) {
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
        return new ActivityRoute.Builder(this)
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
        if(route instanceof ActivityRoute){
            ActivityRoute aRoute = (ActivityRoute) route;
            switch (aRoute.getOpenType()){
                case ActivityRoute.START:
                    open(aRoute, aRoute.getActivity());
                    break;
                case ActivityRoute.FOR_RESULT_ACTIVITY:
                    openForResult(aRoute, aRoute.getActivity(), aRoute.getRequestCode());
                    break;
                case ActivityRoute.FOR_RESULT_SUPPORT_FRAGMENT:
                    openForResult(aRoute, aRoute.getSupportFragment(), aRoute.getRequestCode());
                    break;
                case ActivityRoute.FOR_RESULT_FRAGMENT:
                    openForResult(aRoute, aRoute.getFragment(), aRoute.getRequestCode());
                    break;
                default:
                    Timber.e("Error Open Type");
            }
        }

    }

    @Override
    public void open(String url) {
        open(getRoute(url));
    }


    protected void open(ActivityRoute route, Context context) {
        try {
            Intent intent = match(route);
            if(intent == null){
                Timber.e(new RouteNotFoundException(route.getUrl()), "");
                return;
            }

            if(context == null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mBaseContext.startActivity(intent);
            } else {
                context.startActivity(intent);
            }

            if(route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null){
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
        } catch (Exception e){
            Timber.e(e, "");
        }
    }

    protected void openForResult(ActivityRoute route, Activity activity, int requestCode) {

        try {
            Intent intent = match(route);
            if(route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null){
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e){
            Timber.e(e, "");
        }
    }

    protected void openForResult(ActivityRoute route, Fragment fragment, int requestCode) {

        try {
            Intent intent = match(route);
            if(route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null){
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e){
            Timber.e(e, "");
        }
    }

    protected void openForResult(ActivityRoute route, android.app.Fragment fragment, int requestCode) {

        try {
            Intent intent = match(route);
            if(route.getInAnimation() != -1 && route.getOutAnimation() != -1 && route.getActivity() != null){
                route.getActivity().overridePendingTransition(route.getInAnimation(), route.getOutAnimation());
            }
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e){
            Timber.e(e, "");
        }
    }




    /**
     * host 和path匹配称之为路由匹匹配
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private String findMatchedRoute(ActivityRoute route) {
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

    private Intent setOptionParams(String url, Intent intent){
        Map<String, String> queryParams = UrlUtils.getParameters(url);
        for(String key: queryParams.keySet()){
            intent.putExtra(key, queryParams.get(key));
        }

        return intent;
    }

    private Intent setExtras(Bundle bundle, Intent intent){
        intent.putExtras(bundle);
        return intent;
    }

    @Nullable
    private Intent match(ActivityRoute route) {
        String matchedRoute = findMatchedRoute(route);
        if(matchedRoute == null){
            return null;
        }
        Class<? extends Activity> matchedActivity = mRouteTable.get(matchedRoute);
        Intent intent = new Intent(mBaseContext, matchedActivity);
        intent = setOptionParams(route.getUrl(), intent);
        intent = setExtras(route.getExtras(), intent);
        return intent;
    }
}
