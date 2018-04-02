package app.mapstargame;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * The class that contain the Options fragment
 *
 * @author Daniel Bucolo
 *
 */
public class OptionsFragment extends Fragment implements OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View optionsView = inflater.inflate(R.layout.options_fragment, container, false);

        ArrayAdapter<CharSequence> times = ArrayAdapter.createFromResource(getActivity(),
                R.array.time, android.R.layout.simple_spinner_item);
        times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner timeSpinner = (Spinner) optionsView.findViewById(R.id.time_spinner);
        timeSpinner.setAdapter(times);

        View ok_butt = optionsView.findViewById(R.id.play_button);
        ok_butt.setOnClickListener(this);

        View back_butt = optionsView.findViewById(R.id.back_button);
        back_butt.setOnClickListener(this);

        return optionsView;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                Intent i = new Intent();
                MapStarActivity parent = (MapStarActivity) getActivity();
                String selectedTime = ((Spinner) getView().findViewById(R.id.time_spinner))
                        .getSelectedItem().toString();
                parent.timeOfLevel = Integer.parseInt(selectedTime);

                int soundOption = ((RadioGroup)getView().findViewById(R.id.sound_control))
                        .getCheckedRadioButtonId();

                if (soundOption == R.id.off_option)
                    parent.soundsOn = false;
                else
                    parent.soundsOn = true;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,
                        R.animator.slide_out_right);
                PlayFragment fragment = new PlayFragment();
                fragmentTransaction.replace(R.id.mapstar_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.back_button:
                getFragmentManager().popBackStack();
                break;
        }


    }
}
