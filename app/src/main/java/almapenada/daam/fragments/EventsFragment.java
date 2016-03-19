package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import almapenada.daam.R;


public class EventsFragment extends Fragment {

    LinearLayout lista_eventos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (ViewGroup)inflater.inflate(R.layout.fragment_events, container, false);
        lista_eventos = (LinearLayout) v.findViewById(R.id.lista_de_eventos);

        if(getNumberOfEvents()>0) {
            ViewGroup new_event = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.event_tab, null);
            TextView o = (TextView) new_event.findViewById(R.id.nomeEvento);
            o.setText("Vinho verde");
            ViewGroup new_event2 = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.event_tab, null);
            lista_eventos.addView(new_event);
            lista_eventos.addView(new_event2);
        }
        return v;
    }


    public int getNumberOfEvents() {
        //ir a DB, buscar numero de eventos a mostrar e retornar
        return 2;
    }
}
