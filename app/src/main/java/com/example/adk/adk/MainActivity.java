package com.example.adk.adk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.example.adk.adk.SessionManager.KEY_EMAIL;
import static com.example.adk.adk.SessionManager.KEY_NAME;
import static com.example.adk.adk.SessionManager.KEY_NAME1;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;

    private TextView h_name,h_email;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;
    private static final String PREFER_NAME = "com.example.adk.adk";

    SharedPreferences sharedPreferences;

    private static final String URL= "https://adkandroids.000webhostapp.com/U_name.php";

    public String Uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tab = findViewById(R.id.tab);

        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Wmain.class);
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout swipeview = findViewById(R.id.swipe_view);

        swipeview.setColorScheme(android.R.color.holo_blue_dark,android.R.color.holo_blue_light,android.R.color.holo_green_light,android.R.color.holo_green_dark);
        swipeview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                swipeview.setRefreshing(true);

                Log.d("Swipe", "Refreshing Number");
                ( new Handler()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        swipeview.setRefreshing(false);
                    }
                }, 3000);
            }
        });



        if (!isOnline()){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        // Session Manager Class
        session = new SessionManager(getApplicationContext());

        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);

        getName();

        SessionManager sessionManager=new SessionManager(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.adk.adk", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(KEY_NAME1,"").toString();

        String email = sharedPreferences.getString(KEY_EMAIL,"").toString();

        sessionManager.checkLogin();

        myDrawer = findViewById(R.id.mydrawer);
        myToggle=new ActionBarDrawerToggle(this,myDrawer, R.string.open, R.string.close);

        NavigationView navigationView = findViewById(R.id.my_nav);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        h_name = headerView.findViewById(R.id.header_name);
        h_name.setText(name);
        h_email= headerView.findViewById(R.id.header_email);
        h_email.setText(email);

        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getName() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Uname = response;

                        session.createUsername(Uname);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
//                        pd.hide();
//                        Log.d("ErrorResponse", finalResponse);
//                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", sharedPreferences.getString(KEY_EMAIL,"").toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (myToggle.onOptionsItemSelected(item)){


            return true;

      }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:

                SessionManager sessionManager=new SessionManager(this);
                sessionManager.logoutUser();
                finish();
                break;

        }
        return true;
    }


    //Press back to exit
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //Network State
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
