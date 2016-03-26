package almapenada.daam.utility;

        import java.util.ArrayList;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
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
                Toast.makeText(activity.getBaseContext(), "imagina que mudavas de pagina", Toast.LENGTH_SHORT).show();
            }
        });

        final int temp_position=position;
        final String titulo_evento=data.get(position).getEventName();
        vi.setOnTouchListener(new View.OnTouchListener() {
            private int event_position=temp_position;
            private float x1,x2;
            static final int MIN_DISTANCE = 50;
            private boolean avoid_double_click=false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x1 = event.getX();
                }else {
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE && !avoid_double_click) {
                        avoid_double_click=true;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(activity.getString(R.string.msg_apagar_evento) + " " + titulo_evento + "?")
                                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        avoid_double_click=false;
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(activity, "Removido o evento", Toast.LENGTH_SHORT).show();
                                        Event item = data.get(event_position);
                                        data.remove(event_position);
                                        self.notifyDataSetChanged();
                                        avoid_double_click=false;
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                        /*if (x2 > x1) {}//saber se é esq ou direita*/
                    }
                    else {
                        //Toast.makeText(activity.getBaseContext(), "imagina que mudavas de pagina", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        TextView title = (TextView)vi.findViewById(R.id.nomeEvento);
        final CheckBox going = (CheckBox)vi.findViewById(R.id.goingOpt);
        TextView diaSemana = (TextView)vi.findViewById(R.id.diaSemana);
        TextView diaEvento = (TextView)vi.findViewById(R.id.diaEvento);
        TextView preco = (TextView)vi.findViewById(R.id.preco);
        TextView horas = (TextView)vi.findViewById(R.id.horas);
        TextView local = (TextView)vi.findViewById(R.id.local);

        title.setText(data.get(position).getEventName());
        going.setSelected(data.get(position).isGoing());
        going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(going.isChecked()) {
                    going.setText(activity.getString(R.string.going_event));
                }else {
                    going.setText(activity.getString(R.string.not_going_event));
                }
            }
        });
        diaSemana.setText(data.get(position).getWeekDay());
        diaEvento.setText(data.get(position).getDate());
        preco.setText("Price: " + data.get(position).getPrice());
        horas.setText(data.get(position).getHours());
        local.setText(data.get(position).getLocation());
        data.get(position).setId(position);

        return vi;
    }
}