package narz.tiizzer.camecame;

import android.content.Context;

/**
 * Created by narztiizzer on 8/25/2016 AD.
 */
public class InitialCamera {

    public static final int TOUCH_FOCUS_STATE = 99;
    public static final int TOUCH_FOCUS_FAIL = 0;
    public static final int TOUCH_FOCUS_SUCCESS = 1;

    public static final String FRONT_CAMERA = "FRONT_CAMERA";
    public static final String REAR_CAMERA = "REAR_CAMERA";

    private Context cameraContext;

    private static InitialCamera ourInstance;

    public InitialCamera(Context cntx){
        cameraContext = cntx;
    }

    public static InitialCamera init(Context context) {
        if(ourInstance == null);
            ourInstance = new InitialCamera(context);
        return ourInstance;
    }

    public static InitialCamera getInstance(){
        return ourInstance;
    }


}
