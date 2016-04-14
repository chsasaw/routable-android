package cn.campusapp.router.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.campusapp.router.route.BrowserRoute;
import cn.campusapp.router.route.IRoute;

import static cn.campusapp.router.utils.UrlUtils.getScheme;

/**
 * Created by kris on 16/3/17.
 */
public class BrowserRouter extends BaseRouter {
    private static final Set<String> SCHEMES_CAN_OPEN = new LinkedHashSet<>();

    private Context mBaseContext;

    static {
        SCHEMES_CAN_OPEN.add("https");
        SCHEMES_CAN_OPEN.add("http");
    }

    public void init(Context context){
        mBaseContext = context;
    }

    @Override
    public void open(IRoute route) {
        Uri uri = Uri.parse(route.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mBaseContext.startActivity(intent);
    }

    @Override
    public void open(String url) {
        open(getRoute(url));
    }

    @Override
    public IRoute getRoute(String url) {
        return new BrowserRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(IRoute route) {
        return getCanOpenRoute().equals(route.getClass());
    }

    @Override
    public boolean canOpenTheUrl(String url) {
        return SCHEMES_CAN_OPEN.contains(getScheme(url));
    }

    @Override
    public Class<? extends IRoute> getCanOpenRoute() {
        return BrowserRoute.class;
    }
}
