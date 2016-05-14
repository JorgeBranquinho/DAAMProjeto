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

import almapenada.daam.R;

public class ProfileFragment extends Fragment {

    private ProfileFragment self = this;
    private Button image;
    private static final int SELECT_IMAGE = 1;
    private ViewPager viewPager;
    private String filePath = "";

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
                Toast.makeText(getActivity(), "You have selected item : " + adapter.getItem(index), Toast.LENGTH_SHORT).show();
                // guardar adapter.getItem(index)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // to do
            }

        });

        return rootView;
    }


    //Galeria de fotos
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
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
        }
    }
}

