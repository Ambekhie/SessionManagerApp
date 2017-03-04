package competition.sessionmanagerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class Session extends Fragment {

    private ImageButton start;
    private TextView code;
    private EditText sessionName;
    private String name = "", email = "", linkedin = "";

    public void setContactDetails (String n, String e, String l){
        name = n;
        email = e;
        linkedin = l;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Session() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Session.
     */
    // TODO: Rename and change types and number of parameters
    public static Session newInstance(String param1, String param2) {
        Session fragment = new Session();
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

    private String code(View view) throws SocketException {
        String ip = " NO CONNECTION";
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface net : Collections.list(nets)) {
            if (net.getDisplayName().equals("wlan0")) {
                Enumeration<InetAddress> inters = net.getInetAddresses();
                for (InetAddress inter : Collections.list(inters)) {
                    if (inter.toString().length() < 17) {
                        ip = inter.toString();
                    }
                }

            }
        }
        return ip;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presenter, container, false);
        start = (ImageButton) view.findViewById(R.id.start);
        code = (TextView) view.findViewById(R.id.code);
        String ip = "";
        try {
            ip = code(view).substring(1);
            if (!ip.equals("NO CONNECTION")) {
                String[] tokens = ip.split("\\.");
                ip = "";
                for (int i = 0; i < 4; i++) {
                    ip = ip + (Integer.toHexString(Integer.parseInt(tokens[i])).length() == 1?
                            "0"+Integer.toHexString(Integer.parseInt(tokens[i])):Integer.toHexString(Integer.parseInt(tokens[i])));
                }
            }
            code.setText(ip);
        } catch (SocketException e) { }
        final String ipCopy = ip;
        sessionName = (EditText) view.findViewById(R.id.sessionName);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ipCopy.equals("NO CONNECTION")) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    PresenterSession chart = new PresenterSession(sessionName.getText().toString(), ipCopy, name, email, linkedin);
                    ft.replace(R.id.splashLayout, chart);
                    ft.commit();
                } else {
                    Toast.makeText(v.getContext(), "NO CONNECTION FOUND", Toast.LENGTH_SHORT).show();
                }

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
