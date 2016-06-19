package almapenada.daam.utility;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import almapenada.daam.R;

public class DatePickerFragment2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final Activity activity;
    private final boolean tipo;
    private int type = -1;
    private String date;
    private String date_end;
    private String hour_init;
    private String hour_end;

    public DatePickerFragment2(int i, Activity activity, boolean tipo) {
        this.type = i;
        this.activity=activity;
        this.tipo=tipo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(final DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//"yyyy-MM-dd");
        final String formattedDate = sdf.format(c.getTime());

        Bundle b = activity.getIntent().getExtras();
        if (tipo) date=formattedDate;//date=formattedDate;//date_picker.setText(formattedDate);
        else date_end=formattedDate;//date_end=formattedDate;


        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String datetime = formattedDate + " " + selectedHour + ":" + selectedMinute;
                Bundle b = activity.getIntent().getExtras();
                if (tipo) hour_init=datetime;
                else hour_end=datetime;
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getHour_init() {
        return hour_init;
    }

    public void setHour_init(String hour_init) {
        this.hour_init = hour_init;
    }

    public String getHour_end() {
        return hour_end;
    }

    public void setHour_end(String hour_end) {
        this.hour_end = hour_end;
    }
}
