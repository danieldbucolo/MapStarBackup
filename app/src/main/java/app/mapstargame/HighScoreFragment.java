package app.mapstargame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * High scores fragment
 * 
 * @author Daniel Bucolo
 * 
 */
public class HighScoreFragment extends Fragment implements OnClickListener {
    private static String[] names = {"World", "USA", "Europe", "Asia", "Africa", "Latin America"};

    private static int padding_table_cells_scores = 60;
    private static int padding_table_rows_scores = 20;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View highScores = inflater.inflate(R.layout.highscore_fragment, container, false);

		TableLayout tl = (TableLayout) highScores.findViewById(R.id.highScores);
		Highscore highScore = new Highscore(getActivity());

		TableRow headers = new TableRow(getActivity());
		TextView mapHeader = new TextView(getActivity());
		mapHeader.setText("Map");
		mapHeader.setPadding(5,
                padding_table_rows_scores,
                padding_table_cells_scores,
                padding_table_rows_scores);
		mapHeader.setTextSize(20);
		TextView scoreHeader = new TextView(getActivity());
		scoreHeader.setText("Score");
		scoreHeader.setPadding(padding_table_cells_scores,
                padding_table_rows_scores,
                5,
                padding_table_rows_scores);
        scoreHeader.setTextSize(20);
		headers.addView(mapHeader);
		headers.addView(scoreHeader);

		tl.addView(headers);

        for (int i=0; i<6; i++) {
            TableRow newRow = new TableRow(getActivity());

            TextView mapName = new TextView(getActivity());
            mapName.setText(names[i]);
            mapName.setPadding(5, 5, padding_table_cells_scores, 5);
            mapName.setTextSize(15);

            TextView mapScore = new TextView(getActivity());
            mapScore.setText(Long.toString(highScore.getScore(names[i])));
            mapScore.setPadding(padding_table_cells_scores, 5, 5, 5);
            mapScore.setTextSize(15);

            newRow.addView(mapName);
            newRow.addView(mapScore);

            tl.addView(newRow);
        }
		View goback = highScores.findViewById(R.id.back_button);
		goback.setOnClickListener(this);

        return highScores;
	}

	public void onClick(View v) {
	    getFragmentManager().popBackStack();
	}
}
