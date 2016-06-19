package almapenada.daam.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import almapenada.daam.R;
import almapenada.daam.utility.User;

public class CreateNewGroup extends Fragment {

    private View v;
    private List<User> friends = new ArrayList<User>();
    private boolean[] friendsarray_going;
    private String[] friendsarray;
    private boolean sync=false;
    private static final int SELECT_IMAGE = 1;
    private static final int SELECT_PICTURE = 1;
    private String filePath;
    private ImageView imgView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_new_group, container, false);

        Button event_people = (Button) v.findViewById(R.id.button2);
        imgView = (ImageView) v.findViewById(R.id.imageView3);
        Button image = (Button) v.findViewById(R.id.chooseImage);

        new DownloadFriendsTask().execute();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });

        event_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sync) {
                    new AlertDialog.Builder(getActivity())
                            .setMultiChoiceItems(friendsarray,
                                    friendsarray_going,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton,
                                                            boolean isChecked) {

                                            if (isChecked) friendsarray_going[whichButton] = true;
                                            else friendsarray_going[whichButton] = false;
                                        }
                                    })
                            .setPositiveButton(R.string.aceitar,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked Yes so do some stuff */
                                        }
                                    })
                            .setNegativeButton(R.string.cancelar,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked No so do some stuff */
                                        }
                                    })
                            .show();
                }
            }
        });


        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
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
            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
        }else{
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
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


    private class DownloadFriendsTask extends AsyncTask<URL, Void, Void> {

        protected Void doInBackground(URL... url) {
            String response = "";
            Bundle b = getActivity().getIntent().getExtras();
            User user = (User) b.getSerializable("User");
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/getAll/friends/byUser/" + user.getIdUser()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                jsonToUser(response);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            friendsarray = new String[friends.size()];
            friendsarray_going = new boolean[ friends.size()];
            int i = 0;
            for (User x : friends) {
                friendsarray[i] = x.getFirstName() + " " + x.getLastName();
                friendsarray_going[i] = false;
                i++;
            }
            sync=true;
        }
    }

    private void jsonToUser(String response) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(response);
            if (jobj.get("status").toString().compareTo("OK") == 0) {
                JSONArray poi = jobj.getJSONArray("teste");
                for (int i = 0; i < poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);
                    User x = new User();
                    x.setIdUser(Integer.parseInt(t_poi.getString("id")));
                    x.setFirstName(t_poi.getString("name").substring(0, t_poi.getString("name").lastIndexOf(" ")));
                    x.setLastName(t_poi.getString("name").substring(t_poi.getString("name").lastIndexOf(" ") + 1));
                    x.setEmail(t_poi.getString("email"));
                    x.setPhone(t_poi.getString("telephone"));
                    if (t_poi.getString("description") != "null")
                        x.setDescricao(t_poi.getString("description"));
                    x.setGender(t_poi.getString("gender"));
                    friends.add(x);
                }
            }
            //dummy_users = (User[]) friends.toArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
