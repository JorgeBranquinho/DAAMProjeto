package almapenada.daam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import almapenada.daam.utility.UserProfile;

public class SignUpActivity extends AppCompatActivity {

    private SignUpActivity self = this;
    private Button image, submit;
    private static final int SELECT_IMAGE = 1;
    private ViewPager viewPager;
    private String filePath = "";
    private View rootView;
    private ImageView imgView;
    private static final int SELECT_PICTURE = 1;
    private EditText putnome;
    private EditText putemail;
    private EditText pass;
    private EditText tlm;
    private EditText reppass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        putnome = (EditText) findViewById(R.id.putnome);
        putemail = (EditText) findViewById(R.id.putemail);
        pass = (EditText) findViewById(R.id.pass);
        tlm = (EditText) findViewById(R.id.tlm);
        reppass = (EditText) findViewById(R.id.reppass);

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
                if ( validateParams() ) {
                    createUser();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Cursor cursor=null;
                Uri selectedImageUri = data.getData();
                filePath = getPath(cursor,selectedImageUri);
                imgView.setImageURI(selectedImageUri);
            }
        }
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
        if ( getName().isEmpty() || getEmail().isEmpty() || getPassword().isEmpty() || getRepPassword().isEmpty() || getTelefone().isEmpty() ) {
            maketoast("Need to fill the required fields");
            return false;
        }
        return true;
    }

    private void maketoast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void createUser() {
        new CreateUserTask().execute();
    }

    public String getName() {
        return putnome.getText().toString();
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

    public String getRepPassword() {
        return reppass.getText().toString();
    }

    public String getTelefone() {
        return tlm.getText().toString();
    }

    public void createProfile(String id, String name, String email, String telefone, String image) {
        new UserProfile(id, name, email, telefone, image);
        System.out.println("Consegui: " + id + name + email + telefone + image);
    }

    public void showSuccessMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Sucesso");
        alertDialogBuilder
                .setMessage("Successfully created user!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        SignUpActivity.this.finish();
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            System.out.println("Vou começar");
            String name =  getName() + "/";
            String email =  getEmail()+ "/";
            String password = getPasswordMd5()+ "/";
            String telefone = getTelefone()+ "/";
            String image = "testeimage"+ "/";
            String host = "https://eventservice-daam.rhcloud.com";
            String method = "/insert/user/";
            String response = "";
            System.out.println(host + method + name + email + password + telefone + image);
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(host + method + name + email + password + telefone + image));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jobj = null;
            UserProfile user = null;
            try {
                jobj = new JSONObject(result);
                System.out.println("Resposta: " + jobj.get("status").toString());

                if (jobj.get("status").toString().compareTo("OK") == 0) {
                    showSuccessMessage();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }



}
