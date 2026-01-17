package com.zzk.childrenbank.statistic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeriodSelectFragment#newInstance} factory method to create an instance of this fragment.
 * 用于控制起止日期范围的面板，包括五个按钮选项：近一周、近一月、近三月、全部、自定义。
 * 选择自定义时，会再出现一个起止时间的面板。
 */
public class PeriodSelectFragment extends Fragment {
    private Date startDate;
    private Date endDate;
    Child child;

    private ToggleButton[] toggleButtons;

    private DropdownList dropdownListYearMonth;
    private DropdownList dropdownListAccount;

    private LinearLayout dateArea;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private TextView textViewTo;
    private Button buttonApply;

    private OnPeriodChangeListener changeListener;

    public PeriodSelectFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PeriodSelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    //public static PeriodSelectFragment newInstance(String param1, String param2) {
    public static PeriodSelectFragment newInstance() {
        PeriodSelectFragment fragment = new PeriodSelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_period_select, container, false);
        toggleButtons = new ToggleButton[4];
        toggleButtons[0] = fragment.findViewById(R.id.toggleButtonAll);
        toggleButtons[1] = fragment.findViewById(R.id.toggleButtonThisYear);
        toggleButtons[2] = fragment.findViewById(R.id.toggleButtonRecent2Year);
        toggleButtons[3] = fragment.findViewById(R.id.toggleButtonDefined);

        dropdownListYearMonth = fragment.findViewById(R.id.dropdownlist_yearmonth);
        dropdownListAccount = fragment.findViewById(R.id.dropdownlist_periodselect_account);

        dateArea = fragment.findViewById(R.id.dateArea);
        textViewStartDate = fragment.findViewById(R.id.textViewStartDate);
        textViewEndDate = fragment.findViewById(R.id.textViewEndDate);
        textViewTo = fragment.findViewById(R.id.textViewTo);
        buttonApply = fragment.findViewById(R.id.buttonApply);

        dropdownListYearMonth.addItem("按年统计");
        dropdownListYearMonth.addItem("按月统计");
        dropdownListYearMonth.updateData();
        dropdownListAccount.addItem("全部账户");
        dropdownListAccount.updateData();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;
                while(index<toggleButtons.length && toggleButtons[index]!=view) index++;
                setChecked(index);
                if(index<4) {
                    if(changeListener!=null) changeListener.onPeriodChanged(getStartDate(), getEndDate(), getAccountIndex());
                }
            }
        };
        for(ToggleButton button:toggleButtons) button.setOnClickListener(clickListener);

        dropdownListAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(changeListener!=null) changeListener.onPeriodChanged(getStartDate(), getEndDate(), getAccountIndex());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        dropdownListYearMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(changeListener!=null) changeListener.onYearMonthChanged(getYearMonth());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        setChecked(0);


        endDate = new Date();
        startDate = new Date(endDate.getTime() - 1l * 365 * 24 * 60 * 60 * 1000);   //  1l使得采用long型运算，否则采用int型，会有溢出
        setStartDate(startDate);
        setEndDate(endDate);


        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(startDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = GregorianCalendar.getInstance();
                        //cal.setTime(startDate);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        Date datetime = cal.getTime();
                        setStartDate(datetime);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(endDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = GregorianCalendar.getInstance();
                        //cal.setTime(endDate);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        Date datetime = cal.getTime();
                        setEndDate(datetime);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changeListener!=null) changeListener.onPeriodChanged(getStartDate(), getEndDate(), getAccountIndex());
            }
        });

        return fragment;
    }

    public void setSelfDefineDateTextColor(int color){
        textViewStartDate.setTextColor(color);
        textViewEndDate.setTextColor(color);
        textViewTo.setTextColor(color);
    }

    void setChecked(int index) {
        for(ToggleButton button:toggleButtons) button.setChecked(false);
        toggleButtons[index].setChecked(true);
        if( index==3) {
            dateArea.setVisibility(View.VISIBLE);
        } else {
            dateArea.setVisibility(View.GONE);
        }
    }

    void setStartDate(Date date) {
        startDate.setTime(date.getTime());
        textViewStartDate.setText(Common.DateTimeToString(startDate, true,true,false));
    }

    void setEndDate(Date date) {
        endDate.setTime(date.getTime());
        textViewEndDate.setText(Common.DateTimeToString(endDate, true, true, false));
    }

    int getCheckedIndex(){
        for(int i=0; i<toggleButtons.length; i++) if(toggleButtons[i].isChecked()) return i;
        return -1;
    }

    /**
     * 获取当前设置的开始日期
     * @return
     */
    public Date getStartDate(){
        int index = getCheckedIndex();
        if(index==3) {      //  自定义
            return startDate;
        } else if(index==0) {   //  全部
            return null;
        } else {
            int year = Common.getYear(new Date());
            if(index==2) year--;
            return new Date(Common.getBeginTimeOfYear(year));
        }
    }

    /**
     * 获取当前设置的结束日期
     * @return
     */
    public Date getEndDate(){
        int index = getCheckedIndex();
        if(index==3) {
            return endDate;
        } else if(index==0) {
            return null;
        } else {
            return new Date();
        }
    }

    public void setOnPeriodChangeListener(OnPeriodChangeListener listener){
        this.changeListener = listener;
    }

    public void fillAccountList(Child child) {
        dropdownListAccount.clearItem();
        dropdownListAccount.addItem("全部账户");
        Iterator<Account> iter = child.accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            dropdownListAccount.addItem(account.name);
        }
        dropdownListAccount.updateData();
    }

    public int getAccountIndex() {
        return dropdownListAccount.getSelectedItemPosition();
    }

    public int getYearMonth(){
        return dropdownListYearMonth.getSelectedItemPosition();
    }

    /**
     * 用于监听用户设置的起止日期发生变化时的监听器
     */
    public static interface OnPeriodChangeListener{
        /**
         * 当用户修改起止日期时会调用监听器的onPeriodChanged方法。
         * @param start
         * @param end
         */
        void onPeriodChanged(Date start, Date end, int accountIndex);
        void onYearMonthChanged(int yearMonth);
    }
}