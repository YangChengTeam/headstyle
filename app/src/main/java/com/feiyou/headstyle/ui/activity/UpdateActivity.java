package com.feiyou.headstyle.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feiyou.headstyle.R;
import com.feiyou.headstyle.net.service.UpdateService;
import com.feiyou.headstyle.util.FileUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class UpdateActivity extends BaseActivity {
	private static final int MSG_UPDATE = 0x0;

	@BindView(R.id.cur_size)
	TextView tvCurSize;
	@BindView(R.id.total_size)
	TextView tvTotalSize;
	@BindView(R.id.update_pb)
	ProgressBar progressBar;

	@BindView(R.id.cancel)
	Button cancelBtn;
	@BindView(R.id.hide)
	Button hideBtn;

	private boolean isStop;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE) {
				UpdateService.DownLoadInfo downLoadInfo = (UpdateService.DownLoadInfo) msg.obj;
				if (downLoadInfo != null) {
					tvCurSize.setText(FileUtils.formatSize(downLoadInfo.downloadSize));
					tvTotalSize.setText(FileUtils.formatSize(downLoadInfo.fileSize));
					if (downLoadInfo.fileSize != 0) {
						progressBar.setProgress((int) (100.0 * downLoadInfo.downloadSize / downLoadInfo.fileSize));
					}
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

    @Override
    public int getLayoutId() {
        return R.layout.activity_update;
    }

    @Override
    public void loadData() {
        super.loadData();
        if (UpdateService.hasStop()) {
            this.finish();
            startActivity(new Intent(UpdateActivity.this, MainActivity.class));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    if (UpdateService.hasStop()) {
                        return;
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = MSG_UPDATE;
                    msg.obj = UpdateService.getDownLoadInfo();
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    @Override
    protected void initVars() {
        super.initVars();
        tvCurSize.setText("");
        tvTotalSize.setText("");
        progressBar.setProgress(0);
    }

    @Override
    public void initViews() {
        super.initViews();
    }

    @OnClick({R.id.cancel,R.id.hide})
    public void clickEvent(View view){
        if(view.getId() == R.id.cancel){
            isStop = true;
            stopService(new Intent(UpdateActivity.this, UpdateService.class));
            finish();
        }
        if(view.getId() == R.id.hide){
            finish();
        }
    }


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
