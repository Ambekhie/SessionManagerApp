package competition.sessionmanagerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Enumeration;

public class Audience extends Fragment {

    private ImageButton join;
    private EditText code;
    private String macAddress;
    private String ip;
    private String info = "";

    private class ClientSide {

        private Socket socket;

        public int connect(String givenIp) {
            try {
                if (givenIp == null) {
                    return 0;
                }
                socket = new Socket();
                socket.connect(new InetSocketAddress(givenIp,4444),2500);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
                Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
                for (NetworkInterface netInterface : Collections.list(netInterfaces)) {
                    if (netInterface.getDisplayName().equals("wlan0")) {
                        byte[] mac = netInterface.getHardwareAddress();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        macAddress = sb.toString();
                    }
                }
                output.writeUTF(macAddress);
                info = input.readUTF();
                output.writeUTF("{\n\t\"Vote\" : {\n\t\"Interesting\" : " + 0
                        + ",\n\t\"Presentation Skills\" : " + 0
                        + ",\n\t\"Understanding\" : " + 0
                        + ",\n\t\"Comfortable\" : " + 0
                        + ",\n\t\"Amazing\" : " + 0
                        + ",\n\t\"My Knowledge\" : " + 0 + "\n\t}\n}");
                socket.close() ;
            } catch(SocketTimeoutException e){
                return 2 ;
            }catch(Exception e){
                return 0 ;
            }
            ip = givenIp;
            return 1 ;
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

    public Audience() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Audiance.
     */
    // TODO: Rename and change types and number of parameters
    public static Audience newInstance(String param1, String param2) {
        Audience fragment = new Audience();
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
        final View view = inflater.inflate(R.layout.fragment_audiance, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        code = (EditText) view.findViewById(R.id.codeAudiance);
        join = (ImageButton) view.findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int flag = (new ClientSide().connect(codeToIp(code.getText().toString()))) ;
            if (1 == flag) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                String[] infos = info.split("#,#");
                AudienceView chart;
                if (infos.length < 4){
                    chart = new AudienceView(macAddress, ip, infos[0], "", "", "");
                } else {
                    chart = new AudienceView(macAddress, ip, infos[0], infos[1], infos[2], infos[3]);
                }
                ft.replace(R.id.splashLayout, chart);
                ft.commit();
            } else if(0 == flag){
                Toast.makeText(v.getContext(), "WRONG CODE", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(v.getContext(), "NO SESSION ON CURRENT NETWORK", Toast.LENGTH_SHORT).show();
            }
            }
        });
        return view;
    }

    private String codeToIp(String code) {
        String ip = "";
        try {
            for (int i = 0; i < 8; i += 2) {
                ip += hexToDecimal(code.substring(i, i + 2)) + ".";
            }
        } catch (Exception e) {
            return null;
        }
        return ip.substring(0, ip.length() - 1);
    }

    private String hexToDecimal(String string) {
        String digits = "0123456789ABCDEF";
        string = string.toUpperCase();
        int value = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int d = digits.indexOf(c);
            if (d == -1) {
                throw new NullPointerException();
            }
            value = 16 * value + d;
        }
        return Integer.toString(value);
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
