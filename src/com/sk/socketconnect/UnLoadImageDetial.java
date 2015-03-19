package com.sk.socketconnect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.utils.Constant;

public class UnLoadImageDetial extends BaseActivity {

    private EditText unload_img_act_img_title, unload_img_act_img_description;

    private final String IMAGE_TYPE = "image/*";

    private final int IMAGE_CODE = 101;
    private final int IMAGE_CODE_KITKAT = 102;
    private TextView unload_img_act_image_url;

    private Bitmap mGetBitmap;

    private String choose_image_url;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.unload_img_act_get_img:
            getImageUrl();
            break;
        case R.id.unload_img_act_unload:
            if (mGetBitmap == null) {
                showShortToast("please choose image");
            } else {
                unLoadBitmap(mGetBitmap);
            }
            break;

        default:
            break;
        }
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

    // public String getPath(final Uri uri) {
    // final boolean isKitKat = Build.VERSION.SDK_INT >=
    // Build.VERSION_CODES.KITKAT;
    // // DocumentProvider
    // if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
    // // ExternalStorageProvider
    // if (isExternalStorageDocument(uri)) {
    // final String docId = DocumentsContract.getDocumentId(uri);
    // final String[] split = docId.split(":");
    // final String type = split[0];
    // if ("primary".equalsIgnoreCase(type)) {
    // return Environment.getExternalStorageDirectory() + "/" + split[1];
    // }
    // }
    // // DownloadsProvider
    // else if (isDownloadsDocument(uri)) {
    //
    // final String id = DocumentsContract.getDocumentId(uri);
    // final Uri contentUri = ContentUris.withAppendedId(
    // Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
    //
    // return getDataColumn(contentUri, null, null);
    // }
    // // MediaProvider
    // else if (isMediaDocument(uri)) {
    // final String docId = DocumentsContract.getDocumentId(uri);
    // final String[] split = docId.split(":");
    // final String type = split[0];
    //
    // Uri contentUri = null;
    // if ("image".equals(type)) {
    // contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    // } else if ("video".equals(type)) {
    // contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    // } else if ("audio".equals(type)) {
    // contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    // }
    //
    // final String selection = "_id=?";
    // final String[] selectionArgs = new String[] {
    // split[1]
    // };
    //
    // return getDataColumn(contentUri, selection, selectionArgs);
    // }
    // }
    // // MediaStore (and general)
    // else if ("content".equalsIgnoreCase(uri.getScheme())) {
    //
    // // Return the remote address
    // if (isGooglePhotosUri(uri))
    // return uri.getLastPathSegment();
    //
    // return getDataColumn(uri, null, null);
    // }
    // // File
    // else if ("file".equalsIgnoreCase(uri.getScheme())) {
    // return uri.getPath();
    // }
    //
    // return null;
    // }

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

    // startActivityForResult(getAlbum, IMAGE_CODE);
    // }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            printLog("ActivityResult resultCode error");
            return;
        }
        // Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                Uri originalUri = data.getData();
                mGetBitmap = MediaStore.Images.Media.getBitmap(resolver,
                        originalUri);

                // get image url
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(originalUri, proj, null, null,
                        null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                choose_image_url = cursor.getString(column_index);
                unload_img_act_image_url.setText(choose_image_url);

            } catch (IOException e) {
                printLog(e.toString());
            }
        }
        if (requestCode == IMAGE_CODE_KITKAT) {
            printLog("ActivityResult resultCode IMAGE_CODE_KITKAT");
            try {
                Uri originalUri = data.getData();
                mGetBitmap = MediaStore.Images.Media.getBitmap(resolver,
                        originalUri);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // if(bm != null){
        // unLoadBitmap(bm);
        // }
    }

    private void unLoadBitmap(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        String requestMsg = "{" + Constant.UNLOADIMAGE_START + "}";
        sendRequest(requestMsg);
    }

    public String selectImagePath(Intent data) {
        Uri selectedImage = data.getData();
        // Log.e(TAG, selectedImage.toString());
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
        unload_img_act_img_title = $(R.id.unload_img_act_img_title);
        unload_img_act_image_url = $(R.id.unload_img_act_image_url);
        unload_img_act_img_description = $(R.id.unload_img_act_img_description);
        $(R.id.unload_img_act_get_img).setOnClickListener(this);
        $(R.id.unload_img_act_unload).setOnClickListener(this);
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
        if (Constant.UNLOADIMAGE_START_SUCCESS.equals(result)) {
            File mFile = new File(choose_image_url);
            Log.i(this.getClass().getSimpleName(), "start unload pic stream ");
            sendRequest(mFile);
        }
        if (Constant.UNLOADIMAGE_SUCCESS.equals(result)) {
            sendRequest("{_IMAGE_END_}");
        }
        if ("_SERVER_CLOSE_".equals(result)){
            showShortToast(result); 
        }else{
            showShortToast(result); 
        }
    }

}
