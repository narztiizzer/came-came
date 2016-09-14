package narz.tiizzer.example;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import narz.tiizzer.camecame.InitialCamera;
import narz.tiizzer.camecame.camera.CameraPreview;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitialCamera.init(this);

        Button mockupButton = (Button) findViewById(R.id.mockupButton);
        Button fragmentButton = (Button) findViewById(R.id.fragmentButton);
        Button activityButton = (Button) findViewById(R.id.activityButton);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.containers, MockupFragment.newInstance());
        transaction.commit();

        mockupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.containers, MockupFragment.newInstance());
                transaction.commit();
            }
        });

        fragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, CaptureFragment.newInstance());
                transaction.addToBackStack(null);
                transaction.commit();
                Log.d("TAG" , "");
            }
        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent cameraIntent = new Intent(MainActivity.this , CaptureActivity.class);
                startActivity(cameraIntent);
            }
        });
    }
}
