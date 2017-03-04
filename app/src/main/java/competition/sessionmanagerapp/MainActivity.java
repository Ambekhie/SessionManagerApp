package competition.sessionmanagerapp;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends FragmentActivity implements OnFragmentInteractionListener{

    private Button yes, no;
    private boolean flag = false;
    private ImageView splash;

    public void setFlag (boolean f){
        flag = f;
    }

    @Override
    public void onBackPressed() {
        if (flag){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            yes = (Button) dialog.findViewById(R.id.yes);
            no = (Button) dialog.findViewById(R.id.no);
            yes.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    finish();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash = (ImageView) findViewById(R.id.splashView);
        splash.setBackgroundResource(R.drawable.splashview);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.setVisibility(View.GONE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Choose choose = new Choose();
                ft.replace(R.id.splashLayout, choose);
                ft.commit();
            }
        }, 4000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}