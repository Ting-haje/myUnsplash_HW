package com.example.fal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePhoto{
    //存调用该类的活动
    private Context mycontext;

    SavePhoto(Context context){
        mycontext = context;
    }
    private static File mPhotoFile = null;
    public static void setPhotoFile(File photoFile){
        mPhotoFile = photoFile;
    }

    public static File getPhotoFile(){

        return mPhotoFile;
    }
    void saveImageToGallery(Bitmap bmp) {

        String fileName = "test";
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath + "test1.jpg");
//            file = new File(galleryPath, photoName);
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            }
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(galleryPath + "test1.jpg"));
            intent.setData(uri);
            mycontext.sendBroadcast(intent);
        }catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                    setPhotoFile(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),fileName,null);
            MediaStore.Images.Media.insertImage(mycontext.getContentResolver(),bmp,fileName,null);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            mycontext.sendBroadcast(intent);
            AlertDialog.Builder builder  = new AlertDialog.Builder(mycontext);
            builder.setMessage("保存成功" ) ;
            builder.show();
            //ToastUtils.showToast(context,"图片保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder  = new AlertDialog.Builder(mycontext);
            builder.setMessage("保存失败" ) ;
            builder.show();
            //ToastUtils.showToast(context,"图片保存失败");
        }

    }

//    public void saveImageToGallery(Bitmap bitmap) {
//        // 首先保存图片
//        File file = null;
//        String fileName = System.currentTimeMillis() + ".jpg";
//        File root = new File(Environment.getExternalStorageDirectory(), mycontext.getPackageName());
//        File dir = new File(root, "images");
//        if (dir.mkdirs() || dir.isDirectory()) {
//            file = new File(dir, fileName);
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(mycontext.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 通知图库更新
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            MediaScannerConnection.scanFile(mycontext, new String[]{file.getAbsolutePath()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                            mediaScanIntent.setData(uri);
//                            mycontext.sendBroadcast(mediaScanIntent);
//                        }
//                    });
//        } else {
//            String relationDir = file.getParent();
//            File file1 = new File(relationDir);
//            mycontext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
//        }
//    }

}


