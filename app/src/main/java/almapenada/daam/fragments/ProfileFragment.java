package almapenada.daam.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.ScalingUtilies;

public class ProfileFragment extends Fragment {

    private ProfileFragment self = this;
    private Button image;
    private static final int SELECT_IMAGE = 1;
    private ViewPager viewPager;
    private String filePath = "";
    private static final int SELECT_PICTURE = 1;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        image = (Button) rootView.findViewById(R.id.image);
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


        //SPINNER
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int index = parentView.getSelectedItemPosition();
                //só para testar
                // Toast.makeText(getActivity(), "You have selected item : " + adapter.getItem(index), Toast.LENGTH_SHORT).show();
                // guardar adapter.getItem(index)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // to do
            }

        });

        ((DrawerActivity) getActivity()).setActionBarTitle("Nome de Utilizador");

        return rootView;
    }




    //Galeria de fotos
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == SELECT_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String wholeID = DocumentsContract.getDocumentId(selectedImage);
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};
                        String sel = MediaStore.Images.Media._ID + "=?";
                        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                        String filePath = "";
                        int columnIndex = cursor.getColumnIndex(column[0]);
                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        image.setBackground(new BitmapDrawable(getContext().getResources(), yourSelectedImage));

                    } else {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//se a API é 15+
                            image.setBackground(new BitmapDrawable(getContext().getResources(), yourSelectedImage));
                        }
                    }
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }*/
        if (resultCode == getActivity().RESULT_OK) {
           filePath = "";
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (data != null) {
                    filePath = getPath(selectedImageUri);
                    filePath = decodeFile(filePath,125,125);
                    Bitmap yourSelectedImage = getImageBitmap(filePath);
                    //Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        //Bitmap fin = Bitmap.createScaledBitmap(yourSelectedImage, 125, 120, false);
                        image.setBackground(new BitmapDrawable(getContext().getResources(), yourSelectedImage));
                    }
                }
            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        Cursor cursor;
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

    private Bitmap getImageBitmap(String filePath) {
        Bitmap bm = null;
            bm = BitmapFactory.decodeFile(filePath);

        return bm;
    }

    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = almapenada.daam.utility.ScalingUtilies.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = almapenada.daam.utility.ScalingUtilies.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);
            } else {
                if (unscaledBitmap != null && !unscaledBitmap.isRecycled()) {
                    unscaledBitmap.recycle();
                }
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
                scaledBitmap.recycle();
            }
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
}

