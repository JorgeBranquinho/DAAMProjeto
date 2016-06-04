package almapenada.daam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HelpActivity extends AppCompatActivity {

    private SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        sliderShow = (SliderLayout) findViewById(R.id.slider);

        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .description("Tutorial parte 1")
                .image(R.drawable.add_img);

        TextSliderView textSliderView2 = new TextSliderView(this);
        textSliderView2
                .description("Tutorial parte 2")
                .image(R.drawable.logo);

        TextSliderView textSliderView3 = new TextSliderView(this);
        textSliderView3
                .description("Tutorial parte 3")
                .image(R.drawable.temp_fista);

        sliderShow.addSlider(textSliderView);
        sliderShow.addSlider(textSliderView2);
        sliderShow.addSlider(textSliderView3);


    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    private File drawableToFile(int id){
        try{
            File f=new File("file name");
            InputStream inputStream = getResources().openRawResource(id);
            OutputStream out=new FileOutputStream(f);
            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            inputStream.close();
            return f;
        }
        catch (IOException e){}
        return null;
    }

}
