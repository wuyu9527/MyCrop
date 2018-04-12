package com.whxcs.mycrop;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
   /* static {
        System.loadLibrary("native-lib");
    }*/

    public static final String whx = "MyCrop";

    ZoomableDraweeView image;
    String url = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1523415005&di=be5f7dba8e499846d1340aa24bca7ec5&src=http://pic3.16pic.com/00/53/72/16pic_5372096_b.jpg";
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //图片缓存初始化
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.seekbar);
        //监听器
        seekBar.setOnSeekBarChangeListener(this);
        image = findViewById(R.id.image);
        image.setImageURI(url);
        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        String test = "+5";
//        tv.setText(stringFromJNI(test));

        findViewById(R.id.addDu).setOnClickListener(this);
        findViewById(R.id.moveDu).setOnClickListener(this);


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    @SuppressWarnings("JniMissingFunction")
    public native String stringFromJNI(String test);

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float rotation = (float) (360 * progress / 100);
        image.setImageRotation(rotation);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDu:
                image.setImageRotation(3);
                break;
            case R.id.moveDu:
                image.setImageRotation(-3);
                break;
        }
    }
}
