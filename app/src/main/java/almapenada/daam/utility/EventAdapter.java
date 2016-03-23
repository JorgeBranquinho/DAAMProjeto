package almapenada.daam.utility;

        import java.util.ArrayList;

        import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.TextView;
        import android.widget.Toast;

        import almapenada.daam.R;

public class EventAdapter extends BaseAdapter {

    private EventAdapter self;
    private Activity activity;
    private ArrayList<Event> data;
    private static LayoutInflater inflater=null;

    public EventAdapter(Activity a, ArrayList<Event> d) {
        self=this;
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

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(activity.getBaseContext(), "ola", Toast.LENGTH_SHORT).show();
            }
        });

        final int temp_position=position;
        vi.setOnTouchListener(new View.OnTouchListener() {
            private int event_position=temp_position;
            private float x1,x2;
            static final int MIN_DISTANCE = 50;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x1 = event.getX();
                }else{
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (x2 > x1) {
                            Toast.makeText(activity, "Removido evento", Toast.LENGTH_SHORT).show ();
                            Event item = data.get(event_position);
                            data.remove(event_position);
                            self.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(activity, "Removido evento", Toast.LENGTH_SHORT).show ();
                            Event item = data.get(event_position);
                            data.remove(event_position);
                            self.notifyDataSetChanged();
                        }
                    }
                    else {
                        // fez tap no ecra
                    }
                }
                return false;
            }
        });

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
        data.get(position).setId(position);

        return vi;
    }
}