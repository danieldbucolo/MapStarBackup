package app.mapstargame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The class that we save and load high score from and to
 */
public class Highscore {
    private SharedPreferences preferences;
    private long score[];

    private String names[] = {"World", "USA", "Europe", "Africa", "Asia", "Latin America"};

    public Highscore(Context context) {
        // we save to "high"
        preferences = context.getSharedPreferences("high", 0);
        score = new long[12];

        for (int x = 0; x < 10; x++) {
            score[x] = preferences.getLong("score" + x, 0);
        }

    }

    /**
     *
     * @param name
     *            the name of the map
     * @return the score
     */
    public long getScore(String name) {
        // get the score of the x-th position in the Highscore-List
        if (name == "World"){
            return score[0];
        } else if (name == "USA") {
            return score[1];
        } else if (name == "Europe") {
            return score[2];
        } else if (name == "Asia") {
            return score[3];
        } else if (name == "Africa") {
            return score[4];
        } else if (name == "Latin America") {
            return score[5];
        } else {
            return 0;
        }
    }

    /**
     *
     * @param name
     *            the name of the user like"ahmed,5" 5==level
     * @param score
     *            the score he got
     * @return
     */
    public boolean addScore(String name, long score) {
        // add the score with the name to the Highscore-List
        String[] array = name.split(",");
        int position = Integer.parseInt(array[1]);
        // chick if he get higher score or not
        if (score < this.score[position])
            return false;
        this.names[position] = new String(array[0]);
        this.score[position] = score;
        SharedPreferences.Editor editor = preferences.edit();
        for (int x = 0; x < 6; x++) {
            editor.putString("name" + x, this.names[x]);
            editor.putLong("score" + x, this.score[x]);
        }
        editor.commit();
        return true;

    }

}
