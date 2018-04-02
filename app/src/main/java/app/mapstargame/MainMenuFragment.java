package app.mapstargame;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainMenuFragment extends Fragment implements View.OnClickListener{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View menuView = inflater.inflate(R.layout.main_menu_fragment, container, false);

        ImageButton playGameButton = (ImageButton) menuView.findViewById(R.id.play_game_button);
        playGameButton.setOnClickListener(this);

        ImageButton highScoresButton = (ImageButton) menuView.findViewById(R.id.high_score_button);
        highScoresButton.setOnClickListener(this);

        ImageButton optionsButton = (ImageButton) menuView.findViewById(R.id.options_button);
        optionsButton.setOnClickListener(this);

        ImageButton creditsButton = (ImageButton) menuView.findViewById(R.id.credits_button);
        creditsButton.setOnClickListener(this);


        return menuView;
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,
                R.animator.slide_out_right);

		if (viewId == R.id.play_game_button) {
            PlayFragment fragment = new PlayFragment();
            fragmentTransaction.replace(R.id.mapstar_fragment, fragment);
		} else if (viewId == R.id.options_button) {
			OptionsFragment fragment = new OptionsFragment();
            fragmentTransaction.replace(R.id.mapstar_fragment, fragment);
		} else if (viewId == R.id.high_score_button) {
			HighScoreFragment fragment = new HighScoreFragment();
            fragmentTransaction.replace(R.id.mapstar_fragment, fragment);
		} else if (viewId == R.id.credits_button) {
			CreditsFragment fragment = new CreditsFragment();
            fragmentTransaction.replace(R.id.mapstar_fragment, fragment);
		}
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
	}

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}