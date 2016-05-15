package almapenada.daam.utility;

        import java.util.ArrayList;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.ContentValues;
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

        import almapenada.daam.DrawerActivity;
        import almapenada.daam.R;

public class EventAdapter extends BaseAdapter {

    private EventsDatabase database;
    private EventAdapter self;
    private Activity activity;
    private ArrayList<Event> data;
    private static LayoutInflater inflater=null;

    public EventAdapter(Activity a, ArrayList<Event> d) {
        self=this;
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //database = new EventsDatabase(activity);
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

        final int temp_position=position;

        vi.setOnClickListener(new View.OnClickListener() {
            private Event e=data.get(temp_position);
            @Override
            public void onClick(View arg0) {
                ((DrawerActivity)activity).viewEventDetails(e);
            }
        });

        final String titulo_evento=data.get(position).getEventName();
        vi.setOnTouchListener(new View.OnTouchListener() {
            private int event_position=temp_position;
            private float x1,x2;
            static final int MIN_DISTANCE = 50;
            private boolean avoid_double_click=false;
            private Event e=data.get(temp_position);
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x1 = event.getX();
                }else {
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE && !avoid_double_click) {
                        if (x2 > x1) {//saber se é esq ou direita*/
                            avoid_double_click = true;
                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(activity.getString(R.string.msg_apagar_evento) + " " + titulo_evento + "?")
                                    .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            avoid_double_click = false;
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.aceitar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            database = new EventsDatabase(activity);
                                            boolean res=database.deleteById(e.getId() + 1);
                                            if(!res)    System.out.println("[error] nao apagou evento " + e.getId());
                                            database.close();//para escrever as mudanças nas DB
                                            data.remove(event_position);
                                            self.notifyDataSetChanged();
                                            avoid_double_click = false;
                                            dialog.dismiss();
                                            if(res) Toast.makeText(activity, "Removido o evento", Toast.LENGTH_SHORT).show();
                                            else Toast.makeText(activity, "Ocorreu um erro a remover o evento", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            builder.show();
                        }
                    }
                    else {
                        //user fez tap
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
        going.setChecked(data.get(position).isGoing());
        if(data.get(position).isGoing())going.setText(activity.getString(R.string.going_event));else going.setText(activity.getString(R.string.not_going_event));
        going.setOnClickListener(new View.OnClickListener() {
            private Event e = data.get(temp_position);

            @Override
            public void onClick(View v) {

                if (going.isChecked()) {
                    going.setText(activity.getString(R.string.going_event));
                } else {
                    going.setText(activity.getString(R.string.not_going_event));
                }
                database = new EventsDatabase(activity);
                ContentValues values = new ContentValues();
                values.put(EnumDatabase.FIELD_NAME, e.getEventName());
                values.put(EnumDatabase.FIELD_isPUBLIC, e.isPublic());
                values.put(EnumDatabase.FIELD_WEEKDAY, e.getWeekDay());
                values.put(EnumDatabase.FIELD_DATE, e.getDate());
                values.put(EnumDatabase.FIELD_isENDDATE, e.isEndDate());
                values.put(EnumDatabase.FIELD_ENDDATE, e.getEnddate());
                values.put(EnumDatabase.FIELD_isPRICE, e.isPrice());
                values.put(EnumDatabase.FIELD_PRICE, e.getPrice());
                values.put(EnumDatabase.FIELD_HOURS, e.getHours());
                values.put(EnumDatabase.FIELD_isLOCATION, e.isLocation());
                values.put(EnumDatabase.FIELD_LOCATION_latlng, e.getLocation_latlng().latitude + " " + e.getLocation_latlng().longitude);
                values.put(EnumDatabase.FIELD_FRIENDS_INVITE, e.isFriendsInvitable());
                values.put(EnumDatabase.FIELD_GOING, going.isChecked());
                values.put(EnumDatabase.FIELD_NEW, e.isNewEvent());
                values.put(EnumDatabase.FIELD_DESCRIPTION, e.getDescription());
                values.put(EnumDatabase.FIELD_FILEPATH, e.getFilepath());
                database.update(e.getId(), values);
                database.close();
            }
        });
        if(!data.get(position).getWeekDay().equals("") || !data.get(position).getDate().equals("")) {
            diaSemana.setText(data.get(position).getWeekDay());
            diaEvento.setText(data.get(position).getDate());
        }else{
            diaSemana.setText("");
            diaEvento.setText("");
        }
        if(data.get(position).isPrice() && !data.get(position).getPrice().equals(""))
            preco.setText("Price: " + data.get(position).getPrice() + "€");
        else
            preco.setText(" - ");
        if(!data.get(position).getHours().equals(""))
            horas.setText(data.get(position).getHours());
        else
            horas.setText("");
        if(data.get(position).isNewEvent())
            local.setText("NEW");
        else
            local.setText("");
        data.get(position).setId(position);

        return vi;
    }
}