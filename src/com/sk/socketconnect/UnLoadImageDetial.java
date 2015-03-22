package com.sk.socketconnect;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.utils.Constant;
import com.sk.socketconnect.utils.TimeUtil;

public class UnLoadImageDetial extends BaseActivity {

    private EditText unload_img_act_loc,
            unload_img_act_img_description;
    
    private String unload_img_act_loc_x, unload_img_act_loc_y;

    private final String IMAGE_TYPE = "image/*";

    private final int IMAGE_CODE = 101;
    private final int IMAGE_CODE_KITKAT = 102;
    private final int REQUEST_LOCATION_CODE = 103;
    public static final int RESULT_LOCATION_CODE = 104;
    private ImageView unload_img_act_image;

    private Bitmap mGetBitmap;

    private String choose_image_url;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null){
            user_id = getIntent().getStringExtra(Constant.USER_ID);
        } 
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unload_img_act_get_location:
            getLocation();
            break;
        case R.id.unload_img_act_get_img:
            getImageUrl();
            break;
        case R.id.unload_img_act_unload_info:
            String info_des = unload_img_act_img_description.getText().toString().trim();
            if(!TextUtils.isEmpty(info_des) && !TextUtils.isEmpty(unload_img_act_loc_x)
                    && !TextUtils.isEmpty(unload_img_act_loc_y)){
//                {_WARNING_,24,cc817c3a-2cdb-4d6d-a791-5f81a814268d,116.24114990 
//                    39.91754532,2015-03-19 20:34:25,os com on comnooo yes y}
                String randomUUID = UUID.randomUUID().toString();
//                String currentTime = TimeUtil.getChatTime(System.currentTimeMillis());
                String currentTime = TimeUtil.longToString(System.currentTimeMillis(), TimeUtil.FORMAT_DATE_TIME);
                String sendMsg = user_id + "," + randomUUID + "," +unload_img_act_loc_x + " " + unload_img_act_loc_y
                        + "," + currentTime + "," + info_des;
                sendMsg = appendRequest(Constant.UNLOADINFODES, sendMsg);
                sendRequest(sendMsg);
            }else{
                showShortToast("请获取具体信息后上传");
            }
            break;
        case R.id.unload_img_act_unload_pic:
            if (mGetBitmap != null && choose_image_url != null) {
                File mFile = new File(choose_image_url);
                Log.i(this.getClass().getSimpleName(),
                        "start unload pic stream ");
                sendRequest(mFile);
            } else {
                showShortToast("please choose image");
            }
            break;

        default:
            break;
        }
    }

    private void getLocation() {
//        openActivity(JBaiduMapActivity.class);
        Intent intent = new Intent(this, JBaiduMapActivity.class);
        intent.putExtra(Constant.GET_POSITION_INFO, true);
        startActivityForResult(intent, REQUEST_LOCATION_CODE);
    }

    private void getImageUrl() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.addCategory(Intent.CATEGORY_OPENABLE);
        getAlbum.setType(IMAGE_TYPE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            startActivityForResult(getAlbum, IMAGE_CODE_KITKAT);
        } else {
            startActivityForResult(getAlbum, IMAGE_CODE);
        }
    }

    @SuppressLint("NewApi")
    public String getPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
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
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Uri uri, String selection,
            String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = this.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Bitmap bm = null;
            ContentResolver resolver = getContentResolver();
            Uri originalUri = data.getData();
            if (requestCode == IMAGE_CODE) {
                try {
                    mGetBitmap = MediaStore.Images.Media.getBitmap(resolver,
                            originalUri);
                    
                    choose_image_url = selectImagePath(originalUri);
                    unload_img_act_image.setImageBitmap(mGetBitmap);
                    
                } catch (IOException e) {
                    printLog(e.toString());
                }
            }
            if (requestCode == IMAGE_CODE_KITKAT) {
                printLog("ActivityResult resultCode IMAGE_CODE_KITKAT");
                try {
                    // Uri originalUri = data.getData();
                    mGetBitmap = MediaStore.Images.Media.getBitmap(resolver,
                            originalUri);
                    choose_image_url = getPath(originalUri);
                    unload_img_act_image.setImageBitmap(mGetBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(resultCode == RESULT_LOCATION_CODE && requestCode == REQUEST_LOCATION_CODE){
            if(data != null){
                String currentLocationStr = data.getStringExtra(Constant.CURRENTLOCATIONSTR);
                double currentPositionX = data.getDoubleExtra(Constant.CURRENTPOSITONX, 0.0);
                double currentPositionY = data.getDoubleExtra(Constant.CURRENTPOSITONX, 0.0);
                if(unload_img_act_loc != null && currentLocationStr != null){
                    unload_img_act_loc.setText(currentLocationStr);
                    unload_img_act_loc.setTextSize(12);
                }
                if(currentPositionX != 0.0 || currentPositionY != 0.0){
                    unload_img_act_loc_x = currentPositionX + "";
                    unload_img_act_loc_y = currentPositionY + "";
                }
            }
        }
    }

    public String selectImagePath(Uri selectedImage) {
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10, uriStr.length());
            if (path.startsWith("com.sec.android.gallery3d")) {
                printLog("It's auto backup pic path:"
                        + selectedImage.toString());
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        unload_img_act_loc = $(R.id.unload_img_act_location);
        //unload_img_act_loc_y = $(R.id.unload_img_act_location_y);
        unload_img_act_img_description = $(R.id.unload_img_act_img_description);
        unload_img_act_image = $(R.id.unload_img_act_image_pic);
        $(R.id.unload_img_act_get_location).setOnClickListener(this);
        $(R.id.unload_img_act_get_img).setOnClickListener(this);
        $(R.id.unload_img_act_unload_info).setOnClickListener(this);
        $(R.id.unload_img_act_unload_pic).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unload_image_detial;
    }

    @Override
    public void onFailed() {
        Log.i(this.getClass().getSimpleName(), "result === > failed");
        showShortToast("unload fail");
    }

    @Override
    public void onSuccess(String result) {
        printLog("result === > " + result);
        Log.i(this.getClass().getSimpleName(), "result === > " + result);
        // if (Constant.UNLOADIMAGE_START_SUCCESS.equals(result)) {
        // File mFile = new File(choose_image_url);
        // Log.i(this.getClass().getSimpleName(), "start unload pic stream ");
        // sendRequest(mFile);
        // }
        // if (Constant.UNLOADIMAGE_SUCCESS.equals(result)) {
        // sendRequest("{_IMAGE_END_}");
        // }
        if ("_SERVER_CLOSE_".equals(result)) {
            showShortToast(result);
        } else {
            showShortToast(result);
        }
    }

}
