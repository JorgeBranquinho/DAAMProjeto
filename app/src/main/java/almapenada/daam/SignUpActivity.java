package almapenada.daam;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends AppCompatActivity {

    private SignUpActivity self = this;
    private Button image, submit;
    private static final int SELECT_IMAGE = 1;
    private ViewPager viewPager;
    private String filePath = "";
    private View rootView;
    private ImageView imgView;
    private static final int SELECT_PICTURE = 1;
    private EditText putfirstname;
    private EditText putlastname;
    private EditText putemail;
    private EditText pass;
    private EditText tlm;
    private View mProgressView;
    private View signup_form;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        putfirstname = (EditText) findViewById(R.id.putfirstname);
        putlastname = (EditText) findViewById(R.id.putlastname);
        putemail = (EditText) findViewById(R.id.putemail);
        pass = (EditText) findViewById(R.id.pass);
        tlm = (EditText) findViewById(R.id.tlm);
        mProgressView = findViewById(R.id.signup_progress);
        signup_form = findViewById(R.id.signup_form);

        //rootView = inflater.inflate(R.layout.activity_sign_up, container, false);
        imgView = (ImageView) findViewById(R.id.imageView3);
        image = (Button) findViewById(R.id.chooseImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "BLABLABLA", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( validateParams()) {
                    showProgress(true);
                    createUser();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Cursor cursor = null;
                Uri selectedImageUri = data.getData();
                filePath = getPath(cursor,selectedImageUri);
                Bitmap yourSelectedImage = getImageBitmap(filePath);
                //imgView.setImageURI(selectedImageUri);
                imgView.setImageBitmap(yourSelectedImage);
            }
        }
    }

    private Bitmap getImageBitmap(String filePath) {
        Bitmap bm = null;
        bm = BitmapFactory.decodeFile(filePath);
        return bm;
    }

    public String getPath(Cursor cursor, Uri uri) {
        // just some safety built in

        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
        }else{
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        //Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        }
        //Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
        // this is our fallback here
        return uri.getPath();
    }

    private boolean validateParams() {
        View focusView = null;
        String error = "";

        if (!(isNetworkAvailable())){
            showDialogMessage("É necessário connecção à Internet!", false);
            return false;
        }

        if (  putfirstname.getText().toString().isEmpty()) {
            error = "Nome tem que ser preenchido" + "\n";
            focusView = putfirstname;
        }

        if ((!(getEmail().contains("@"))) || (getEmail().isEmpty())) {
            error += "Email tem que ser bem preenchido" + "\n";
            focusView = putemail;
        }

        if ((!(getPassword().length() >= 5) ) || (getPassword().isEmpty())) {
            error += "Password tem que ser bem preenchida (5 caracteres)" + "\n";
            focusView = pass;
        }

        if (focusView!=null){
            showDialogMessage(error, false);
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    public void createUser() {
        new CreateUserTask(getName(), getEmail(), getPasswordMd5(), getTelefone(), getImage()).execute();
    }

    public String getName() {
        String n = putfirstname.getText().toString() + " " + putlastname.getText().toString();
        return n;
    }

    public String getEmail() {
        return putemail.getText().toString();
    }

    public String getPassword() {
        return pass.getText().toString();
    }

    public String getPasswordMd5() {
        String p = md5(getPassword()) + md5("event");
        return p;
    }

    public String getTelefone() {
        return tlm.getText().toString();
    }

    public String getImage() {
        return "teste";
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showDialogMessage(String msg, final boolean tofinish) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Sign up");
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if ( tofinish ) {
                            SignUpActivity.this.finish();
                        }
                        //
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    };

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

    class CreateUserTask extends AsyncTask<Void, String, String> {

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
                Boolean validEmail = true;
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
                showDialogMessage("Erro na ligação a base de dados", false);
                //e.printStackTrace();
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
                        showDialogMessage("Utilizador criado com sucesso!", true);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();

                }
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
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

            signup_form.setVisibility(show ? View.GONE : View.VISIBLE);
            signup_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signup_form.setVisibility(show ? View.GONE : View.VISIBLE);
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
            signup_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
