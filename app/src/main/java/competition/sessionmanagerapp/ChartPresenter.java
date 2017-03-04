package competition.sessionmanagerapp;

import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChartPresenter extends Fragment {

    private BarChart barChart;
    private int index, oldIndex;
    private Comments commentsView;
    private ArrayList<BarEntry> entries;
    private BarEntry barEntry;
    private ArrayList<String> labels;
    private ArrayList<String> comments;
    private float[] yData = {0, 0, 0, 0, 0, 0};
    private ImageButton refresh;
    private String sessionName, sessionCode, contactName, contactEmail, contactLinkedIn;
    private TextView name, numOfComments;
    private TextView code;
    private Map<String, int[]> audience;
    private TextView number;
    private String[] bars = {"Interesting", "Presentation skills", "Understanding", "Comfortable", "Amazing", "My Knowledge"};

    private class ServerSide extends Thread {

        private ServerSocket server;
        private Socket socket;
        private InetSocketAddress port = new InetSocketAddress(4444);

        @Override
        public void run() {
            // U have to set the two arrays (comments, subjects)
            try {
                server = new ServerSocket();
                server.setReuseAddress(true);
                server.bind(port);
            } catch (IOException e) { }
            while (true) {
                try {
                    socket = server.accept();
                    ServeClient client = this.new ServeClient(socket);
                    Thread thread = new Thread(client);
                    thread.start();
                } catch (IOException e) { }
            }
        }

        class ServeClient implements Runnable {

            private Socket socket;
            private String clientID;

            public ServeClient(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                try {
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    clientID = input.readUTF();
                    if (!audience.containsKey(clientID)) {
                        audience.put(clientID, new int[6]);
                    }
                    output.writeUTF(sessionName + "#,#" + contactName + "#,#" + contactEmail + "#,#" + contactLinkedIn);
                    String data = input.readUTF();
                    parse(data);
                    commentsView.setComments(comments);
                    input.close();
                    output.close();
                } catch (IOException e) { }

            }

            private void parse(String data) {
                String[] tokens = data.split("\n");
                if (tokens[1].replaceAll("[\\s\\t\":{]", "").equals("Comment")) {
                    String parameter = "";
                    String string = tokens[2].replaceAll(",", "");
                    String[] parameters = string.split(":");
                    parameter = parameters[1];
                    comments.add(parameter);
                    /*
                    for (int i = 2; i < 4; i++) {
                        String string = tokens[i].replaceAll(",", "");
                        String[] parameters = string.split(":");
                        parameter[i - 2] = parameters[1];
                    }
                    comment.add("Subject : " + parameter[0] + "\nComment : " + parameter[1]);
                    */
                } else if (tokens[1].replaceAll("[\\s\\t\":{]", "").equals("Vote")) {
                    for (int i = 2; i < 8; i++) {
                        String string = tokens[i].replaceAll(",", "").replaceAll(" ", "");
                        String[] parameters = string.split(":");
                        audience.get(clientID)[i - 2] = Integer.parseInt(parameters[1]);
                    }
                }
            }

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

    public ChartPresenter() {
        // Required empty public constructor
    }

    public ChartPresenter (String sName, String code, Comments cView, String n, String e, String l) {
        commentsView = cView;
        sessionName = sName;
        sessionCode = code;
        comments = new ArrayList<String>();
        contactName = n;
        contactEmail = e;
        contactLinkedIn = l;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartPresenter.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartPresenter newInstance(String param1, String param2) {
        ChartPresenter fragment = new ChartPresenter();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chart_presenter, container, false);
        ((MainActivity)getActivity()).setFlag(true);
        numOfComments = (TextView) view.findViewById(R.id.numberOfComments);
        barChart = (BarChart) view.findViewById(R.id.chartPresenter);
        refresh = (ImageButton) view.findViewById(R.id.refresh);
        name = (TextView) view.findViewById(R.id.sName);
        code = (TextView) view.findViewById(R.id.codeInPresenter);
        addData();
        name.setText(sessionName);
        code.setText(sessionCode);
        audience = Collections.synchronizedMap(new HashMap<String, int[]>());
        comments = new ArrayList<String>();
        number = (TextView) view.findViewById(R.id.audNum);
        barChart.setDescription("");
        barChart.setTouchEnabled(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisLeft().setAxisMaxValue(100);
        barChart.getAxisLeft().setAxisMinValue(0);
        barChart.setDoubleTapToZoomEnabled(false);
        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);
        Thread server = new ServerSide();
        server.start();
        barChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                oldIndex = index;
                RectF rect = null;
                for (index = 0; index < entries.size(); index++) {
                    rect = barChart.getBarBounds(entries.get(index));
                    if (rect.left <= event.getX() && rect.right >= event.getX()) {
                        break;
                    }
                }
                if (index < 6 && index != oldIndex) {
                    Toast.makeText(v.getContext(), bars[index], Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                number.setText(String.valueOf(audience.size()));
                addData();
//                numOfComments.setText(Integer.toString(subjects.size()));
                if (comments.size() > 0){
                    numOfComments.setBackgroundColor(getResources().getColor(R.color.red));
                }
                numOfComments.setText(" " + Integer.toString(comments.size()) + " ");
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setData() {
        int[] data = new int[6];
        Iterator it = audience.entrySet().iterator();
        while (it.hasNext()) {
            int[] temp = ((Map.Entry<String, int[]>) it.next()).getValue();
            for (int i = 0; i < 6; i++) {
                data[i] += temp[i];
            }
        }
        if (audience.size() != 0) {
            for (int i = 0; i < 6; i++) {
                yData[i] = data[i] / (audience.size());
            }
        }
    }

    private void addData() {
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
}
