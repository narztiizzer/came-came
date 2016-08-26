package narz.tiizzer.camecame.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by nattapongr on 1/4/16 AD.
 */
public interface iBitmapNotification {
    void onGetThumbnail(Bitmap bm);
    void onResizeSuccess(Uri resizeFileUri, String message);
    void onResizeFail(String message);
}
