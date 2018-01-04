/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.feiyou.headstyle.view.GalleryWidget;

import android.content.Context;
import android.view.ViewGroup;

import com.feiyou.headstyle.view.TouchView.UrlTouchImageView;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Class wraps URLs to adapter, then it instantiates {@link com.feiyou.headstyle.view.TouchView.UrlTouchImageView} objects to paging up through them.
 */
public class UrlPagerAdapter extends BasePagerAdapter {

    public List<String> lists;

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    public UrlPagerAdapter(Context context, List<String> resources) {
        super(context, resources);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        try {
            if (object != null) {
                ((GalleryViewPager) container).mCurrentView = ((UrlTouchImageView) object).getImageView();
            }
        } catch (Exception e) {
            Logger.e("UrlPagerAdapter error");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        final UrlTouchImageView iv = new UrlTouchImageView(mContext);
        iv.setUrl(mResources.get(position));
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        collection.addView(iv, 0);
        return iv;
    }
}
