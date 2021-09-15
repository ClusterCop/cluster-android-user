package com.cluster.taxiuser.ui.fragment.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.base.BaseFragment;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.base.BaseActivity.RIDE_REQUEST;

public class ScheduleFragment extends BaseFragment implements ScheduleIView {

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;

    private String selectedScheduledTime;
    private String selectedScheduledHour;

    private SchedulePresenter<ScheduleFragment> presenter = new SchedulePresenter<>();

    public ScheduleFragment() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(BaseActivity.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedMinute < 10 || selectedHour < 10) {
                    if (selectedMinute < 10) selectedScheduledTime = "0" + selectedMinute;
                    else selectedScheduledTime = String.valueOf(selectedMinute);

                    if (selectedHour < 10) selectedScheduledHour = "0" + selectedHour;
                    else selectedScheduledHour = String.valueOf(selectedHour);

                    time.setText(selectedScheduledHour + ":" + selectedScheduledTime);
                } else {
                    time.setText(selectedHour + ":" + selectedMinute);
                }
            }
        };
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.date, R.id.time, R.id.schedule_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date:
                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.schedule_request:
                sendRequest();
                break;
        }
    }

    private void sendRequest() {
        if (date.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(activity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("schedule_date", date.getText().toString());
        map.put("schedule_time", time.getText().toString());
        showLoading();
        presenter.sendRequest(map);
    }

    @Override
    public void onSuccess(Object object) {
        hideLoading();
        Intent intent = new Intent("INTENT_FILTER");
        activity().sendBroadcast(intent);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
