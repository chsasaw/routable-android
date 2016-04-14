package cn.campusapp.androuter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

import cn.campusapp.router.Router;
import cn.campusapp.router.route.ActivityRoute;
import cn.campusapp.router.route.CallbackRoute;

public class MainActivity extends Activity {


    Button btn1;

    Button btn2;

    Button btn3;

    Button btn4;

    Button btn5;

    Button btn6;

    Button btn7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityWithVerticalAnim();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityWithHorizontalAnim();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityForResult();
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.open("http://www.baidu.com");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityWithExtraValue();
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openToastView();
            }
        });
    }


    private void openSecondActivity(){
        Router.open("hxstore://second/汤二狗");
    }

    private void openSecondActivityWithVerticalAnim(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("hxstore://second/汤二狗");
        activityRoute
                .setAnimation(this, R.anim.in_from_left, R.anim.out_to_right)
                .open();
    }

    private void openSecondActivityWithHorizontalAnim(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("hxstore://second/汤二狗");
        activityRoute.setAnimation(this, R.anim.in_from_top, R.anim.out_to_bottom)
                .open();
    }

    private void openSecondActivityForResult(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("hxstore://second/汤二狗");
        activityRoute.withOpenMethodStartForResult(this, 200)
                .open();
    }

    private void openSecondActivityWithExtraValue(){
        Date date = new Date();
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("hxstore://third");
        activityRoute
                .withParams("date", date)
                .open();
    }

    private void openToastView() {
        Router.open("hxstore://toast?msg=222222");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            Toast.makeText(this, "Result code "+ resultCode, Toast.LENGTH_SHORT).show();
        }
    }
}
