package com.feiyou.headstyle.listener;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.finalteam.galleryfinal.PauseOnScrollListener;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/1/9 0009 18:18
 */
public class FrescoPauseOnScrollListener extends PauseOnScrollListener {

    public FrescoPauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        super(pauseOnScroll, pauseOnFling);
    }

    @Override
    public void resume() {
        Fresco.getImagePipeline().resume();
    }

    @Override
    public void pause() {
        Fresco.getImagePipeline().pause();
    }
}
