package narz.tiizzer.camecame.helper;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import narz.tiizzer.camecame.R;
import narz.tiizzer.camecame.interfaces.iBitmapNotification;

/**
 * Created by nattapongp on 7/8/2558.
 */
public class ImageHelper {

    public static final int REQUEST_IMAGE_CAPTURE = 999;
    public static final int REQUEST_IMAGE_PICKER = 998;
    public static final int ESTIMATE_IMAGE_FILE_SIZE = 900000;

    public static File createOutputMediaFile(Context mContext) {
        // TODO Auto-generated method stub
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), mContext.getResources().getString(R.string.app_name) + "Photo");
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + mContext.getResources().getString(R.string.app_name) + "_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    public static Uri createResizeImageFileFromBitmap(Context inContext, Bitmap inImage, String imageName) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), inContext.getResources().getString(R.string.app_name) + "Photo");
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) { return null; }
        }

        File file = new File(mediaStorageDir.getPath() , imageName.split("\\.")[0] + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(file);
    }

    public synchronized static File resizeImageFile(Context context, File curFile) {
        Uri uriFile = Uri.fromFile(curFile);
        ContentResolver mContentResolver = context.getContentResolver();

        if(!curFile.exists())
            return null;

        String fileName = curFile.getName();
        if(curFile.length() > ESTIMATE_IMAGE_FILE_SIZE) {
            InputStream in = null;
            try {

                in = mContentResolver.openInputStream(uriFile);

                // Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, o);
                in.close();

                int scale = 1;
                while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > ESTIMATE_IMAGE_FILE_SIZE) {
                    scale++;
                }

                Bitmap b = null;
                in = mContentResolver.openInputStream(uriFile);
                if (scale > 1) {
                    scale--;
                    // scale to max possible inSampleSize that still yields an image
                    // larger than target
                    o = new BitmapFactory.Options();
                    o.inSampleSize = scale;
                    b = BitmapFactory.decodeStream(in, null, o);

                    // resize to desired dimensions
                    int height = b.getHeight();
                    int width = b.getWidth();
                    double y = Math.sqrt(ESTIMATE_IMAGE_FILE_SIZE
                            / (((double) width) / height));
                    double x = (y / height) * width;

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
//                    b.recycle();
                    b = scaledBitmap;
                    uriFile = createResizeImageFileFromBitmap(context, b, fileName);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                uriFile = null;
            } catch (IOException e) {
                e.printStackTrace();
                uriFile = null;
            }
        }

        String newFilePath = getPath(context , uriFile);
        return new File(newFilePath);
    }

    public static Uri convertContentUriToFileUri(Context cntx , Uri contentUri) {
        String filePath = ImageHelper.getPath(cntx, contentUri);
        File imgFile = new File(filePath);
        return Uri.fromFile(imgFile);
    }

    public static String getFileNameFromPath(String filePath) {
        String[] arrFromPath = filePath.split("/");
        return arrFromPath[arrFromPath.length - 1];
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection , String[] selectionArgs) {
        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void captureLayoutImage(Context context , View captureView) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getResources().getString(R.string.app_name) + "Photo");
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) { return; }
        }

        try {
            // image naming and path  to include sd card  appending name you choose for file

            // create bitmap screen capture
            captureView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(captureView.getDrawingCache());
            captureView.setDrawingCacheEnabled(false);
            File imageFile = new File(mediaStorageDir.getPath() , dateFormat.format(new Date()) + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            MediaScannerConnection.scanFile(context ,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> URI = " + uri);
                        }
                    });
            Toast.makeText(context , "Capture image successful" , Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Bitmap writeTextToBitmap(Bitmap source , String text , int x , int y){
        Bitmap bitmapSource = source.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapSource);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(50);
        canvas.drawText(text , x , y , paint);
        Bitmap last = bitmapSource;
        return last;
    }

    public static void createBitmapScaleThumbnail(final iBitmapNotification callBack, final Uri imageFileUri , final int sizeX, final int sizeY) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String path = imageFileUri.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                callBack.onGetThumbnail(Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false));
            }
        });
    }

    public static void createBitmapThumbnail(final iBitmapNotification callBack, final Uri imageFileUri ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String path = imageFileUri.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                callBack.onGetThumbnail(bitmap);
            }
        });
    }

    public static void resizeImageFromFile(final iBitmapNotification callBack , final Context context , final File file) {
        synchronized (ImageHelper.class) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Uri resizeUri = Uri.fromFile(ImageHelper.resizeImageFile(context, file));
                    if(resizeUri != null) {
                        callBack.onResizeSuccess(resizeUri , "Resize success.");
                    } else {
                        callBack.onResizeFail("Resize fail." );
                    }
                }
            });
        }
    }

    public static Bitmap cropCenter(Bitmap srcBmp) {
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
