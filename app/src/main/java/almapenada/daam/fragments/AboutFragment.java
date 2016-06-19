package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import almapenada.daam.R;

/**
 */
public class AboutFragment extends Fragment {

    private SliderLayout sliderShow;
    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_help, container, false);

        sliderShow = (SliderLayout) rootView.findViewById(R.id.slider);

        TextSliderView textSliderView = new TextSliderView(rootView.getContext());
        textSliderView.image(R.drawable.img_about1);

        TextSliderView textSliderView2 = new TextSliderView(rootView.getContext());
        textSliderView2.image(R.drawable.img_about2);


        sliderShow.addSlider(textSliderView);
        sliderShow.addSlider(textSliderView2);

        return rootView;
    }

}
