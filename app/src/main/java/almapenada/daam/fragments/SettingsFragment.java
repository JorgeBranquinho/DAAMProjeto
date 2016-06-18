package almapenada.daam.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import almapenada.daam.R;
import almapenada.daam.utility.User;

public class SettingsFragment extends Fragment {

    private View rootView;
    private NotificationManager mp;
    private Boolean pushFlag = false;
    private User user;
    private Switch push;
    private Switch infp;
    private Button btn;
    private String title = "Um novo seguidor!";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        Bundle b = getActivity().getIntent().getExtras();
        user = (User) b.getSerializable("User");



         push = (Switch) rootView.findViewById(R.id.switch1);
        infp = (Switch) rootView.findViewById(R.id.switch2);
        btn = (Button) rootView.findViewById(R.id.button);

        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(SettingsFragment.this.push.isChecked()){
                    user.setPushFlag(true);
                }else{
                    user.setPushFlag(false);
                }

                mp = (NotificationManager) getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);

                PendingIntent pending = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, new Intent(), 0);
                Notification notify = new Notification.Builder(getActivity().getApplicationContext()).setContentTitle(title).setContentText("Event Me").setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis()).setContentIntent(pending).build();

                mp.notify(0, notify);
            }
        });


        infp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(SettingsFragment.this.infp.isChecked()){
                    user.setInfpFlag(true);
                }else{
                    user.setInfpFlag(false);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Dados limpos com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
