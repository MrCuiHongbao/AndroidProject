package com.example.hongbaocui.littletools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hongbaocui.littletools.Utils.FileStorage;
import com.example.hongbaocui.littletools.Utils.FileUtils;
import com.example.hongbaocui.littletools.Utils.ImageUtils;
//import com.example.hongbaocui.littletools.Utils.LifeStyle;
import com.example.hongbaocui.littletools.Utils.PoiUtils;
//import com.example.hongbaocui.littletools.wordUtils.WordUtils;
import com.example.hongbaocui.littletools.Utils.SharedHelper;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
//import java.util.Observable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//enum GlobalVariable {
//    CAMERA_REQUEST_CODE,// 拍照回传码
//    GALLERY_REQUEST_CODE// 相册选择回传吗
//}
public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_CAPTURE = 2;
    private static final int REQUEST_PICTURE = 5;
    private static final int REVERSAL_LEFT = 3;
    private static final int REVERSAL_UP = 4;
    private Uri imageUri;
    private Uri localUri = null;
    private SharedHelper sh;
    Timer mTimer2;
    TimerTask mTask2;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sh = new SharedHelper(mContext);
        initViews();
    }
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
    private  void initViews() {
      Button   button = findViewById(R.id.buttonSaf);
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            System.out.println("测试一下");
            List<String> stringList = new ArrayList<String>();
              stringList.add("拍照");
              stringList.add("从相册选择");
              final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MainActivity.this, stringList);
              optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      optionBottomDialog.dismiss();
                      switch (position) {
                          case 0:{
//                              openSysCamera();
                              checkPremission(); //检查权限
                          }break;
                          case 1:{
                              openGallery();
                          }break;
                          default:{

                          }
                      }
                  }
              });
          }
      });

        Button   start_button = findViewById(R.id.buttonStart0);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
                Date date = new Date(System.currentTimeMillis());
               String  dataString = dateToString(date,"yyyy-MM-dd");

                sh.save("startDate",dataString);
               final TextView  textView = findViewById(R.id.textViewCal);
//                time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));
                if (mTimer2 == null && mTask2 == null) {
                    mTimer2 = new Timer();
                    mTask2 = new TimerTask() {
                        int countTime=0;
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(getStringTime(countTime++));
                                }
                            });
                        }
                    };
                    mTimer2.schedule(mTask2, 0, 1000);
                }
            }
        });
        Button   end_button = findViewById(R.id.buttonEnd);
        end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
                Date date = new Date(System.currentTimeMillis());
                String  dataString = dateToString(date,"yyyy-MM-dd");
                sh.save("endDate",dataString);

                mTimer2.cancel();
                mTimer2 = null;
                mTask2 = null;
            }
        });

        Button save_button = findViewById(R.id.saveButton);
        save_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    InputStream templetDocStream = getAssets().open("旁站监理记录表.doc");//旁站监理记录表.doc

                    String targetDocPath = mContext.getExternalFilesDir("poi").getPath() + File.separator + "请假单3.docx";//这个目录，不需要申请存储权限
                    Map<String,String> dataMap = new HashMap<String,String>();

                 EditText  editText  = findViewById(R.id.editTextName);
                    if(editText.getText().toString().length()>0) {
                        dataMap.put("$name$", editText.getText().toString());
                    }
                 EditText  editText2  = findViewById(R.id.editTextName2);
                    if(editText2.getText().toString().length()>0) {
                        dataMap.put("$dept$", editText2.getText().toString());
                    }
                  String   startDate = sh.read("startDate");
                    if (startDate.length()>0) {
                        dataMap.put("$StartDate$", startDate);
                    }
                  String   endDate = sh.read("endDate");
                    if (endDate.length()>0) {
                        dataMap.put("$EndDate$",endDate);
                    }
                    EditText  editTextQA  = findViewById(R.id.editTextQA);
                    if(editTextQA.getText().toString().length()>0) {
                        dataMap.put("$Reason1$", editTextQA.getText().toString());
                    }
                    EditText  editTextResult  = findViewById(R.id.editTextResult);
                    if(editTextResult.getText().toString().length()>0) {
                        dataMap.put("$Reason2$", editTextResult.getText().toString());
                    }
//                    dataMap.put("$writeDate$", "2018年10月14日");
//                    dataMap.put("$number$", "110110110");
//                    dataMap.put("$keys$", "Cuihongbao");
//                    dataMap.put("$dept$", "移动开发组");
//                    dataMap.put("$Conditions$", "情况复杂，无法评估");
//                    dataMap.put("$Reason1$", "倒休一天，导致新项目没有交接完全所致，影响轻微");
//                    dataMap.put("$StartDate$", "2018年10月14日上午");
//                    dataMap.put("$EndDate$", "2018年10月14日下午");
//                    InputStream templetJpGStream = getAssets().open("ic_launcher.png");
//                    dataMap.put("$photo$",templetJpGStream);

                    String targetDocPath1 = mContext.getExternalFilesDir("poi").getPath() + File.separator + "p.png";//这个目录，不需要申请存储权限

                    PoiUtils.writeToDoc(templetDocStream,targetDocPath,dataMap,targetDocPath1);

//                    Map<String, Object> param = new HashMap<String, Object>();
//                    param.put("$name", "Cuihongbao");
//                    param.put("$dept$", "信息管理与信息系统");
//                    param.put("${sex}", "男");
//                    param.put("$Conditions$", "山东财经大学");
//                    param.put("${date}", new Date().toString());
//                    param.put("$StartDate$", "2018年10月14日上午");
//                    param.put("$EndDate$", "2018年10月14日下午");
//                    Map<String,Object> header = new HashMap<String, Object>();
//                    header.put("width", 100);
//                    header.put("height", 150);
//                    header.put("type", "png");
//
//                    InputStream templetJpGStream = getAssets().open("ic_launcher.png");
//                    header.put("content", WordUtils.inputStream2ByteArray(templetJpGStream, true));
//                    param.put("$Conditions$",header);

//                    Map<String,Object> twocode = new HashMap<String, Object>();
//                    twocode.put("width", 100);
//                    twocode.put("height", 100);
//                    twocode.put("type", "png");
//                    twocode.put("content", ZxingEncoderHandler.getTwoCodeByteArray("测试二维码,huangqiqing", 100,100));
//                    param.put("${twocode}",twocode);

//                    WordUtils worldUtils = new WordUtils();
//                    String path = "file:///android_asset/旁站监理记录表.doc";
//                    List<String []> list = new ArrayList<String[]>();
//                    worldUtils.getWord(templetDocStream,param,list,targetDocPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 获取时间
     *
     * @return 格式化后的时间
     */
    private static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }
    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINESE, "%02d:%02d:%02d", hour, min, second);
    }
    private void checkPremission() {
        final String permission = Manifest.permission.CAMERA;  //相机权限
        final String permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE; //写入数据权限
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission1) != PackageManager.PERMISSION_GRANTED) {  //先判断是否被赋予权限，没有则申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {  //给出权限申请说明
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            } else { //直接申请权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE); //申请权限，可同时申请多个权限，并根据用户是否赋予权限进行判断
            }
        } else {  //赋予过权限，则直接调用相机拍照
            openCamera();
        }
    }
    /**
     * 打开系统相机
     */
    private void openCamera(){
        Intent intent = new Intent();
        File file = new FileStorage().createIconFile(); //工具类，稍后会给出
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.ddz.demo", file);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片
            try {
                imageUri = Uri.fromFile(ImageUtils.createFile(FileUtils.getInst().getPhotoPathForLockWallPaper(), true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);//启动拍照
    }

    private  void openGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
            // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
            // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_REQUEST_CODE);

        }else { //权限已经被授予，在这里直接写要执行的相应方法即可
            choosePhoto();
        }
    }
    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        startActivityForResult(intentToPickPic,REQUEST_PICTURE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {  //申请权限的返回值
            case CAMERA_REQUEST_CODE:
                int length = grantResults.length;
                final boolean isGranted = length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[length - 1];
                if (isGranted) {  //如果用户赋予权限，则调用相机
                    openCamera();
                } else { //未赋予权限，则做出对应提示

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        fragment4ImageView0 = findViewById(R.id.fragment4ImageView0);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAPTURE:
                    if (null != imageUri) {
                        localUri = imageUri;
                        String path = getPath(MainActivity.this,localUri);
//                        setBitmap(localUri);
                        File file = new File(path);
                        ImageButton btn = (ImageButton) findViewById(R.id.button);
                        if(file.exists()){
                            Bitmap bm = BitmapFactory.decodeFile(path);
                            btn.setImageBitmap(bm);
                        }
                    }
                    break;
                case REQUEST_PICTURE:
                    localUri = data.getData();
                    String path = getPath(MainActivity.this,localUri);
//                        setBitmap(localUri);
                    File file = new File(path);
                    ImageButton btn = (ImageButton) findViewById(R.id.button);
                    if(file.exists()){
                        Bitmap bm = BitmapFactory.decodeFile(path);
                        btn.setImageBitmap(bm);
                    }
//                    setBitmap(localUri);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // 1. DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // 1.1 ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // 1.2 DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.
                        withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return pathHead + getDataColumn(context,
                        contentUri, null, null);
            }
            // 1.3 MediaProvider
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
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // 2. MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {//判断是否是google相册图片
                return uri.getLastPathSegment();
            } else if (isGooglePlayPhotosUri(uri)) {//判断是否是Google相册图片
                return getImageUrlWithAuthority(context, uri);
            } else {//其他类似于media这样的图片，和android4.4以下获取图片path方法类似
                return getFilePath_below19(context, uri);
            }
        }
        // 3. 判断是否是文件形式 File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }
    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
     **/
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.contentprovider/0/1/mediakey:/local%3A821abd2f-9f8c-4931-bbe9-a975d1f5fabc/ORIGINAL/NONE/1075342619
     **/
    public static boolean isGooglePlayPhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
    /**
     * Google相册图片获取路径
     **/
    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * 将图片流读取出来保存到手机本地相册中
     **/
    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    /**
     * 获取小于api19时获取相册中图片真正的uri
     * 对于路径是：content://media/external/images/media/33517这种的，需要转成/storage/emulated/0/DCIM/Camera/IMG_20160807_133403.jpg路径，也是使用这种方法
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePath_below19(Context context,Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        Cursor cursor = null;
        String path = "";
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            //获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
            path = cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

}
///**
//     * 根据Uri拿到当前图像
//     *
//     * @param uri
//     */
//private void setBitmap(Uri uri) {
//        if (uri != null) {
//            Observable.create((rx.Observable.OnSubscribe<Bitmap>) subscriber -> {
//                subscriber.onNext(ImageUtils.getCompressBitmap(LifeStyle.getContext(), uri));
//                subscriber.onCompleted();
//            }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(bitmap -> {
//                        ivWallpaper.setCropMode(CropImageView.CropMode.FREE);
//                        if (bitmap != null) ivWallpaper.setImageBitmap(bitmap);
//                    });
//        }
//}
