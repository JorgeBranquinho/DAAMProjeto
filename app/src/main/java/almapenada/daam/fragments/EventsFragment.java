package almapenada.daam.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import almapenada.daam.R;


public class EventsFragment extends Fragment {

    LinearLayout lista_eventos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (ViewGroup)inflater.inflate(R.layout.fragment_events, container, false);
        lista_eventos = (LinearLayout) v.findViewById(R.id.lista_de_eventos);

        ImageView imageView = new ImageView(getContext());
        //imageView.setImageResource(R.layout.event_tab);
        lista_eventos.addView(imageView);
        return v;
    }

}
