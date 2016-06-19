package almapenada.daam;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import almapenada.daam.utility.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /*usado para o login no facebook*/
    User user = null;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "teste@daam.com:daam"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private boolean logface=false;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String email = "emailKey";
    public static final String gender = "genderKey";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());//facebook

        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //mEmailView.setText("teste@daam.com");

        mPasswordView = (EditText) findViewById(R.id.password);
        //mPasswordView.setText("daam");
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logface = true;

            }
        });


        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));

       // if(logface ){


            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
            if (isNetworkAvailable()) {
                //callbackManager = CallbackManager.Factory.create();

                if (AccessToken.getCurrentAccessToken() != null) {
                    Profile p = Profile.getCurrentProfile();
                    user = new User();
                    user.setFirstName(p.getFirstName());
                    user.setLastName(p.getLastName());
                    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    user.setEmail(sharedpreferences.getString(email, ""));
                    user.setGender(sharedpreferences.getString(gender, ""));
                    try {
                        Uri uriImage = Uri.parse(p.getProfilePictureUri(200, 200).toString());
                        user.setPictureURL(new URL(String.valueOf(uriImage)));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    callDrawerActivity();
                }
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Bundle params = new Bundle();
                        params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                        new GraphRequest(loginResult.getAccessToken(), "me", params, HttpMethod.GET, new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        try {
                                            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            user = new User();
                                            if(user.getIdUser()==0)user.setIdUser(1);
                                            user.setFacebookID(data.getString("id").toString());
                                            String fullname = data.getString("name").toString();
                                            user.setFirstName(fullname.substring(0, fullname.lastIndexOf(" ")));
                                            user.setLastName(fullname.substring(fullname.lastIndexOf(" ") + 1));
                                            user.setGender(data.getString("gender").toString());
                                            editor.putString(gender, data.getString("gender").toString());
                                            editor.putString(email, data.getString("email"));
                                            user.setEmail(data.getString("email"));
                                            user.setPictureURL(new URL(data.getJSONObject("picture").getJSONObject("data").getString("url")));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        new getFBUser(user.getEmail()).execute();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getBaseContext(), "Login attempt canceled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getBaseContext(), "Login attempt failed - check network connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        //}


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!isNetworkAvailable()) return;
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //accessTokenTracker.stopTracking();
    }
//fim das cenas do facebook



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showDialogMessage(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Event Me");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        LoginActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    };
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String host = "https://eventservice-daam.rhcloud.com";
            String method = "/login/";
            String response = "";
            String pass = md5(mPassword) + md5("event");
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(host + method + mEmail + "/" + pass ));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(response);
                    System.out.println("Resposta: " + jobj.get("status").toString());
                    if (jobj.get("status").toString().compareTo("OK") == 0) {
                        JSONArray array = jobj.getJSONArray("userLogin");
                        System.out.println("Array: " + array );
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject t_user = array.getJSONObject(i);
                            user = new User();
                            user.setIdUser(Integer.parseInt(t_user.getString("id")));
                            user.setFirstName(t_user.getString("name"));
                            user.setLastName("");
                            user.setEmail(t_user.getString("email"));
                            user.setTelefone(t_user.getString("telephone"));
                            if ( t_user.getString("description") != null ) {
                                user.setDescricao(t_user.getString("description"));
                            } else {
                                user.setDescricao("");
                            }
                            if (t_user.getString("gender") != null ) {
                                user.setGender(t_user.getString("gender"));
                            } else {
                                user.setGender("");
                            }
                            user.setUserFromFB(false);

                            return true;
                        }
                        return false;
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    showDialogMessage("Erro na resposta JSON" );
                }
            } catch (Exception e) {
                e.printStackTrace();
                showDialogMessage("Erro na ligação ao servidor");
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                callDrawerActivity();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class getFBUser extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private String host = "https://eventservice-daam.rhcloud.com";
        private String method = "/check/email/";
        private String response = "";

        getFBUser(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(host + method + mEmail));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(response);
                    System.out.println("Resposta: " + jobj.get("status").toString());
                    if (jobj.get("status").toString().compareTo("OK") == 0) {
                        JSONArray array = jobj.getJSONArray("result");
                        System.out.println("Array: " + array);
                        if ( array.length() > 0 ) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject t_user = array.getJSONObject(i);
                                user = new User();
                                user.setIdUser(Integer.parseInt(t_user.getString("id")));
                                user.setTelefone(t_user.getString("telephone"));
                                if (t_user.getString("description") != null) {
                                    user.setDescricao(t_user.getString("description"));
                                } else {
                                    user.setDescricao("");
                                }
                                if (t_user.getString("gender") != null) {
                                    user.setGender(t_user.getString("gender"));
                                } else {
                                    user.setGender("");
                                }

                                return true;
                            }
                        } else {
                            criarUtilizador();
                            return true;
                        }
                        return false;
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    showDialogMessage("Erro na resposta JSON");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showDialogMessage("Erro na ligação ao servidor");
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                callDrawerActivity();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }

    public boolean criarUtilizador() {
        String name = user.getFirstName() + " " + user.getLastName();
        String email = user.getEmail();
        String password = "";
        String telefone = "";
        String image = "";
        String host = "https://eventservice-daam.rhcloud.com";
        String methodInsert = "/insert/user/";
        try {
            String response = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(host + methodInsert);

            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("email", email);
            json.put("password", password);
            json.put("telephone", telefone);
            json.put("image", image);

            String message = json.toString();

            post.setEntity(new StringEntity(message, "UTF8"));
            post.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(post);
            if ( httpResponse != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(response);
                    System.out.println("Resposta: " + jobj.get("status").toString());

                    if (jobj.get("status").toString().compareTo("OK") == 0) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            showDialogMessage("Erro na ligação a base de dados");
            e.printStackTrace();
        }
        return false;
    }

    public class CreateUserTask  extends AsyncTask<Void, String, String> {

        private String name;
        private String email;
        private String password;
        private String telefone;
        private String image;
        private String host = "https://eventservice-daam.rhcloud.com";
        private String methodInsert = "/insert/user/";
        private String methodCheckEmail = "/check/email/";

        public CreateUserTask(String name, String email, String password, String telefone, String image ) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.telefone = telefone;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String response = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(host + methodInsert);

                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("email", email);
                json.put("password", password);
                json.put("telephone", telefone);
                json.put("image", image);

                String message = json.toString();

                post.setEntity(new StringEntity(message, "UTF8"));
                post.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(post);
                if ( httpResponse != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                    response = reader.readLine();
                }

            } catch (Exception e) {
                showDialogMessage("Erro na ligação a base de dados");
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (result != "" ) {
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(result);
                    System.out.println("Resposta: " + jobj.get("status").toString());

                    if (jobj.get("status").toString().compareTo("OK") == 0) {
                        showDialogMessage("Utilizador criado com sucesso!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


    private void callDrawerActivity(){
        Intent intent =new Intent(LoginActivity.this,DrawerActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("User", user);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

}

