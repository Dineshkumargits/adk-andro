package com.example.adk.adk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup extends Activity {
//    private static String S_URL ="https://adkandroid.000webhostapp.com/signup.php";
    private static String S_URL= "https://adkandroids.000webhostapp.com/signup.php";
    EditText signUpEmail,signUpName,signUpPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    Button signupButton;
    TextView go_to_login;
    CheckBox checkBoxTerms;
    private ProgressDialog pd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
     SessionManager session;

     //Shared Pref
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pd = new ProgressDialog(Signup.this);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        signupButton =(Button)findViewById(R.id.btn_signup);
        signUpEmail = (EditText)findViewById(R.id.input_email);
        signUpName =(EditText)findViewById(R.id.input_name);
        signUpPassword = (EditText)findViewById(R.id.input_password);
        final CheckBox checkbox = (CheckBox)findViewById(R.id.checkBoxTerms);
        go_to_login=findViewById(R.id.go_to_login);

        signUpName.addTextChangedListener(new MyTextWatcher(signUpName));
        signUpEmail.addTextChangedListener(new MyTextWatcher(signUpEmail));
        signUpPassword.addTextChangedListener(new MyTextWatcher(signUpPassword));

        // Alert Dialog Manager
        AlertDialogManager alert = new AlertDialogManager();

        // Session Manager Class
        session=new SessionManager(this);

        // creating an shared Preference file for the information to be stored
// first argument is the name of file and second is the mode, 0 is private mode

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.adk.adk", Context.MODE_PRIVATE);
// get editor to edit in file
        editor = sharedPreferences.edit();

        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this,Login.class);
                startActivity(i);
                finish();
            }
        });



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isChecked()){

                    if (!validateName()) {
                        return;
                    }

                    if (!validateEmail()) {
                        return;
                    }

                    if (!validatePassword()) {
                        return;
                    }
                    signupRequest();

                }else{

                    Toast.makeText(getApplicationContext(),"Please Accept Terms & Services",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private boolean validateName() {
        if (signUpName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(signUpName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = signUpEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(signUpEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (signUpPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(signUpPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }



    private void signupRequest(){
        pd.setMessage("Signing Up . . .");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(Signup.this);
        String response = null;
        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, S_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        //Response

                        if(response.equals("Successfully Signed In")) {

                            String signname=signUpName.getText().toString();
                            String signpass= signUpPassword.getText().toString();
                            String signemail=signUpEmail.getText().toString();


                            session.createUserSignupSession(signname,signemail);
//
//                            // as now we have information in string. Lets stored them with the help of editor
//                            editor.putString("Name",signname);
//                            editor.putString("Email",signemail);
//                            editor.putString("txtPassword",signpass);
//                            editor.commit();

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                            pd.show();

                        }else if (response.equals("email exist")){
                            Toast.makeText(Signup.this, "Already taken this email", Toast.LENGTH_SHORT).show();
                        }else if (response.equals("Error Sign in")){
                            Toast.makeText(Signup.this, "Error Sign In", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", finalResponse);


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("email", signUpEmail.getText().toString());
                params.put("password", signUpPassword.getText().toString());
                params.put("name", signUpName.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }



}