package com.feiyou.headstyle.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.adapter.GalleryImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainGalleryActivity extends Activity {
    private List<String> images;

    private List<String> images1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gallery);
        images = new ArrayList<String>();

        images1 = new ArrayList<String>();


        images.add("http://a.hiphotos.baidu.com/image/pic/item/8435e5dde71190efeefb4933ca1b9d16fcfa60de.jpg");
        images.add("http://a.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbf6cd825d31dd8bc3eb03541f6.jpg");
        images.add("http://f.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de79c24cbb90eef01f3b2979f9.jpg");
        images.add("http://image93.360doc.com/DownloadImg/2016/01/2018/64780757_15.jpg");
        images.add("http://f.hiphotos.baidu.com/image/pic/item/aa18972bd40735fa4bc04a549a510fb30f24082e.jpg");
        images.add("http://pic31.nipic.com/20130630/7447430_165537858000_2.jpg");
        images.add("http://a.hiphotos.baidu.com/image/pic/item/dcc451da81cb39dbce6b325ad2160924ab183016.jpg");
        images.add("http://f.hiphotos.baidu.com/image/pic/item/35a85edf8db1cb1364f267ccd954564e93584b51.jpg");
        images.add("http://h.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a72eaa2549d543ad4bd01302b2.jpg");


        images1.add("http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d17b6a2ea386d55fbb2fbd9e2.jpg");
        images1.add("http://g.hiphotos.baidu.com/image/pic/item/e824b899a9014c0870b4e6910f7b02087bf4f473.jpg");
        images1.add("http://d.hiphotos.baidu.com/image/pic/item/6159252dd42a28346729f83f5eb5c9ea15cebf73.jpg");
        images1.add("http://g.hiphotos.baidu.com/image/pic/item/b8014a90f603738dafe7c216b61bb051f819ec51.jpg");
        images1.add("http://a.hiphotos.baidu.com/image/pic/item/9213b07eca80653864e86a2792dda144ad348280.jpg");
        images1.add("http://e.hiphotos.baidu.com/image/pic/item/a8ec8a13632762d0dbb06a8ba5ec08fa513dc67c.jpg");
        images1.add("http://f.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e1c0ac8fe878ba61ea8d34580.jpg");
        images1.add("http://a.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a7310c3391d443ad4bd1130280.jpg");


        /*ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        GalleryImageAdapter adapter = new GalleryImageAdapter(this, images);
        viewPager.setAdapter(adapter);

        adapter.addNewDataList(images1);
        adapter.notifyDataSetChanged();*/
    }
}
