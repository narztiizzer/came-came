package narz.tiizzer.example;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import narz.tiizzer.camecame.InitialCamera;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitialCamera.init(this);

        boolean isFragment = true;

        if(isFragment) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container, CaptureFragment.newInstance());
            transaction.commit();
        } else {
            finish();
            Intent cameraIntent = new Intent(this , CaptureActivity.class);
            startActivity(cameraIntent);
        }
    }
}
