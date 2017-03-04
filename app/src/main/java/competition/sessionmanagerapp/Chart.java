package competition.sessionmanagerapp;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Chart extends Fragment implements GestureDetector.OnGestureListener{

    private String temp, sessionName = "", ip, macAddress;
    private BarChart barChart;
    private ArrayList<BarEntry> entries;
    private BarEntry barEntry;
    private ArrayList<String> labels;
    private float[] yData = {0, 0, 0, 0, 0, 0};
    private int index, oldIndex;
    private Button submit, comment, send;
    private TextView sName;
    private EditText commentField;
    private String[] bars = {"Interesting", "Presentation skills", "Understanding", "Comfortable", "Amazing", "My Knowledge"};

    private class ClientSide {

        private Socket socket;

        public boolean connect(String ip, boolean comment) {
            try {
                socket = new Socket(ip, 4444);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(macAddress);
                input.readUTF();
                if (!comment) {
                    output.writeUTF("{\n\t\"Vote\" : {\n\t\"Interesting\" : " + (int) yData[0]
                            + ",\n\t\"Presentation Skills\" : " + (int) yData[1]
                            + ",\n\t\"Understanding\" : " + (int) yData[2]
                            + ",\n\t\"Comfortable\" : " + (int) yData[3]
                            + ",\n\t\"Amazing\" : " + (int) yData[4]
                            + ",\n\t\"My Knowledge\" : " + (int) yData[5] + "\n\t}\n}");
                } else {
                    output.writeUTF("{\n\t\"Comment\" : {\n\t\"Text\" : " + temp + "\n\t}\n}");
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }

    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Chart() {
        // Required empty public constructor
    }

    public Chart(String macAddress, String ip, String sessionName) {
        this.macAddress = macAddress;
        this.ip = ip;
        this.sessionName = sessionName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chart.
     */
    // TODO: Rename and change types and number of parameters
    public static Chart newInstance(String param1, String param2) {
        Chart fragment = new Chart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chart, container, false);
       /* comments = new ArrayList<String[]>();*/
        ((MainActivity)getActivity()).setFlag(true);
        barChart = (BarChart) view.findViewById(R.id.barChart);
        sName = (TextView) view.findViewById(R.id.sessionName);
        sName.setText(sessionName);
        submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(new ClientSide()).connect(ip, false)){
                    Toast.makeText(view.getContext(), "CONNECTION FAILED", Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().getBackStackEntryCount() > 0){
                        boolean done = getFragmentManager().popBackStackImmediate();
                    }
                }
            }
        });
        temp = new String();
        comment = (Button) view.findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Dialog commentDialog = new Dialog(v.getContext());
                commentDialog.setCancelable(true);
                commentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                commentDialog.setContentView(R.layout.comment_dialog);
                send = (Button) commentDialog.findViewById(R.id.sendComment);
                commentField = (EditText) commentDialog.findViewById(R.id.commentField);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp = commentField.getText().toString();
                        if (!temp.equals("")) {
                            (new ClientSide()).connect(ip, true);
                        }
                        commentDialog.cancel();
                    }
                });
                commentDialog.show();
            }
        });
        addData();
        barChart.setDescription("");
        barChart.setTouchEnabled(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMaxValue(100);
        barChart.getAxisLeft().setAxisMinValue(0);
        barChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                oldIndex = index;
                RectF rect = null;
                for (index = 0; index < entries.size(); index++) {
                    rect = barChart.getBarBounds(entries.get(index));
                    if (rect.left <= event.getX() && rect.right >= event.getX()) {
                        doAction(event, rect.top);
                        break;
                    }
                }
                if (index < 6 && index != oldIndex) {
                    Toast.makeText(v.getContext(), bars[index], Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);
        return view;
    }

    private void doAction(MotionEvent e, float y){
        this.onScroll(e, e, y, y);
    }

    private void addData(){
        entries = new ArrayList<>();
        barEntry = new BarEntry(yData[0], 0);
        entries.add(barEntry);
        barEntry = new BarEntry(yData[1], 1);
        entries.add(barEntry);
        barEntry = new BarEntry(yData[2], 2);
        entries.add(barEntry);
        barEntry = new BarEntry(yData[3], 3);
        entries.add(barEntry);
        barEntry = new BarEntry(yData[4], 4);
        entries.add(barEntry);
        barEntry = new BarEntry(yData[5], 5);
        entries.add(barEntry);
        BarDataSet dataSet = new BarDataSet(entries, "Session Manager");
        labels = new ArrayList<String>();
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        dataSet.setColors(colors);
        BarData data = new BarData(labels, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        barChart.setData(data);
        barChart.animateXY(0, 0);
        barChart.invalidate();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e2.getY() > distanceY && yData[index] > 0){
            yData[index]--;
        } else if (e2.getY() < distanceY && yData[index] < 100){
            yData[index]++;
        }
        addData();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
