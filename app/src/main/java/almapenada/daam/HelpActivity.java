package almapenada.daam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class HelpActivity extends AppCompatActivity {

    private SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        sliderShow = (SliderLayout) findViewById(R.id.slider);

        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .description("Game of Thrones")
                .image("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        TextSliderView textSliderView2 = new TextSliderView(this);
        textSliderView2
                .description("Hannibal")
                .image("http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");

        TextSliderView textSliderView3 = new TextSliderView(this);
        textSliderView3
                .description("Porno XXX xD")
                .image("http://cdn3.nflximg.net/images/3093/2043093.jpg");

        sliderShow.addSlider(textSliderView);
        sliderShow.addSlider(textSliderView2);
        sliderShow.addSlider(textSliderView3);


    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}
