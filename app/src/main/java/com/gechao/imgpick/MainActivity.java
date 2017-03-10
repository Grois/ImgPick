package com.gechao.imgpick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.yongchun.library.view.ImageSelectorActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_CROP;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_PREVIEW;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_MAX_SELECT_NUM;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SELECT_MODE;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SHOW_CAMERA;
import static com.yongchun.library.view.ImageSelectorActivity.MODE_MULTIPLE;
import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_IMAGE;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private List<String> imgs;
    private ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //第一：默认初始化
        Bmob.initialize(this, "a7d3ab9735f438161234ba3578ff1775");

        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv);


    }


    public void pick(View v) {
        start(MainActivity.this, 9, MODE_MULTIPLE, true, true, true);


    }

    public static void start(Activity activity, int maxSelectNum, int mode, boolean isShow, boolean enablePreview, boolean enableCrop) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_SELECT_MODE, mode);
        intent.putExtra(EXTRA_SHOW_CAMERA, isShow);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    //    2、在onActivityResult中接收 选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
//            for (String item :
//                    images) {
//                System.out.println(item.toString());
//            }

//            initData();
            gridView.setAdapter(new GvAdapter());
            String path[] = new String[images.size()];
            for (int i = 0; i < images.size(); i++) {
                path[i]=images.get(i);
            }
            uploadFiles(path);
        }
    }

    private void uploadFiles(final String[] path) {
        BmobFile.uploadBatch(path, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if (urls.size() == path.length) {//如果数量相等，则代表文件全部上传完成
                    //do something
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                System.out.println("错误码" + statuscode + ",错误描述：" + errormsg);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
                System.out.println(curIndex+"/"+total);
                System.out.println("当前进度"+curPercent);
                System.out.println("总进度"+totalPercent);
            }
        });

    }


    class GvAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_img, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bitmap bitmap = getLoacalBitmap(images.get(position));
            holder.imageView.setImageBitmap(bitmap);

            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageView;
        ImageView ivDelete;
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
