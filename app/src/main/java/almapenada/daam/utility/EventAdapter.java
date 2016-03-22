package almapenada.daam.utility;

        import java.util.ArrayList;
        import java.util.HashMap;

        import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.ImageView;
        import android.widget.TextView;

        import almapenada.daam.R;

public class EventAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Event> data;
    private static LayoutInflater inflater=null;

    public EventAdapter(Activity a, ArrayList<Event> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if(data==null) return 0;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.event_tab, null);

        TextView title = (TextView)vi.findViewById(R.id.nomeEvento);
        CheckBox going = (CheckBox)vi.findViewById(R.id.goingOpt);
        TextView diaSemana = (TextView)vi.findViewById(R.id.diaSemana);
        TextView diaEvento = (TextView)vi.findViewById(R.id.diaEvento);
        TextView preco = (TextView)vi.findViewById(R.id.preco);
        TextView horas = (TextView)vi.findViewById(R.id.horas);
        TextView local = (TextView)vi.findViewById(R.id.local);

        title.setText(data.get(position).getEventName());
        going.setSelected(data.get(position).isGoing());
        diaSemana.setText(data.get(position).getWeekDay());
        diaEvento.setText(data.get(position).getDate());
        preco.setText("Price: " + data.get(position).getPrice());
        horas.setText(data.get(position).getHours());
        local.setText(data.get(position).getLocation());

        return vi;
    }
}