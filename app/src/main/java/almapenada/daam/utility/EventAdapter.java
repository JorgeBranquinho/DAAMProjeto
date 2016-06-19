package almapenada.daam.utility;

        import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
        import android.widget.ListView;
        import android.widget.TextView;
import android.widget.Toast;

        import org.apache.http.HttpResponse;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpDelete;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.methods.HttpPut;
        import org.apache.http.entity.StringEntity;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.URL;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;

        import almapenada.daam.DrawerActivity;
import almapenada.daam.R;

public class EventAdapter extends BaseAdapter {

    private User u;
    private EventsDatabase database;
    private EventAdapter self;
    private Activity activity;
    private ArrayList<Event> data;
    private static LayoutInflater inflater=null;

    public EventAdapter(Activity a, ArrayList<Event> d, User u) {
        self=this;
        activity = a;
        data=d;
        this.u=u;
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
                                            Toast.makeText(activity, "Removido o evento", Toast.LENGTH_SHORT).show();
                                            int id2=e.getId();
                                            database = new EventsDatabase(activity);
                                            database.deleteById(e.getId() + 1);
                                            database.close();//para escrever as mudanças nas DB
                                            database = new EventsDatabase(activity);//reabrir ligacao
                                            data.remove(event_position);
                                            self.notifyDataSetChanged();
                                            avoid_double_click = false;
                                            dialog.dismiss();
                                            database.close();
                                            DeleteEvents de = new DeleteEvents(id2);
                                            de.execute();
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
                    UpdateEvents ue = new UpdateEvents(e.getId(), true);
                    ue.execute();
                } else {
                    going.setText(activity.getString(R.string.not_going_event));
                    UpdateEvents ue = new UpdateEvents(e.getId(), false);
                    ue.execute();
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
                if( e.getLocation_latlng()!=null)
                    values.put(EnumDatabase.FIELD_LOCATION_latlng, e.getLocation_latlng().latitude + " " + e.getLocation_latlng().longitude);
                values.put(EnumDatabase.FIELD_FRIENDS_INVITE, e.isFriendsInvitable());
                values.put(EnumDatabase.FIELD_GOING, going.isChecked());
                values.put(EnumDatabase.FIELD_NEW, e.isNewEvent());
                database.update(e.getId(), values);
                database.close();
            }
        });
        if(!data.get(position).getWeekDay().equals("") || !data.get(position).getDate().equals("")) {
            diaSemana.setText(data.get(position).getWeekDay());
            diaEvento.setText(data.get(position).getDate());
        }else{
            diaSemana.setText(" - ");
            diaEvento.setText("");
        }
        if(!data.get(position).getPrice().equals(""))
            preco.setText("Price: " + data.get(position).getPrice());
        else
            preco.setText(" - ");
        if(!data.get(position).getHours().equals(""))
            horas.setText(data.get(position).getHours());
        else
            horas.setText(" - ");
        if(data.get(position).isNewEvent())
            local.setText("NEW");
        else
            local.setText("");
        data.get(position).setId(position);

        return vi;
    }

    private class DeleteEvents extends AsyncTask<URL, Void, Void> {

        private int id;

        public DeleteEvents(int id) {
            this.id=id;
        }

        protected Void doInBackground(URL... url) {
            String response = "";
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpDelete("https://eventservice-daam.rhcloud.com/delete/event/" + id + "/" + u.getIdUser()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class UpdateEvents extends AsyncTask<URL, Void, Void> {

        private boolean going;
        private int id;

        public UpdateEvents(int id, boolean going) {
            this.id=id;
            this.going=going;
        }

        protected Void doInBackground(URL... url) {
            String response = "";
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                int g=going?1:0;
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpPut("https://eventservice-daam.rhcloud.com/update/event/going/" + u.getIdUser() + "/" + id +"/" + g ));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}