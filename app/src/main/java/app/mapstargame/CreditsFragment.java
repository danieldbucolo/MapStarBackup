package app.mapstargame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * The class that contain the Credits fragment
 * @author Daniel Bucolo
 *
 */
public class CreditsFragment extends Fragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View credits = inflater.inflate(R.layout.credits_fragment, container, false);

		//the button that get us back to MainActivity menu
		View goback = credits.findViewById(R.id.back_button);
		goback.setOnClickListener(this);

        return credits;
	}

	public void onClick(View v) {
        getFragmentManager().popBackStack();
	}

}