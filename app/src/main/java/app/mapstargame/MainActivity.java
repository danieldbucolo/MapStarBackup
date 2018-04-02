package app.mapstargame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity {
    InterstitialAd mInterstitialAd;

    private String[] cities;
    private int[][] coor;
    private int[] rands;
    private TextView objectiveText;
    private TextView scoreText;
    private RelativeLayout layout;
    private int totalScore = 0;
    private int totalScoreForAllLevels = 0;
    private int currentLevel;
    private int arrayLength;
    private int minimumScore;
    private String levelName;
    private boolean attractionLevelNext = false;
    private boolean attractionLevel = false;
    private int attractionLevelNum = 1;
    public static int timeOfThelevel = 0;
    public static boolean soundsOn = true;
    public static String mapName = "";
    public static boolean prem = false;
    private GraphicsView graphic;
    private Highscore highScore;

    private int backgroundX;
    private int backgroundY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        mapName = args.getString(PlayFragment.EXTRA_MAP_NAME);
        soundsOn = args.getBoolean(PlayFragment.EXTRA_SOUNDS_ON);
        timeOfThelevel = args.getInt(PlayFragment.EXTRA_TIME_OF_LEVEL);
        prem = args.getBoolean(PlayFragment.EXTRA_PREM);

        if (!prem) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-1409082372034038/9971885101");

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }

        if (mapName.equals("World")) {
            attractionLevelNext = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.main_landscape);
        } else if (mapName.equals("USA")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.main_landscape);
        } else if (mapName.equals("Europe")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.main_landscape);
        } else if (mapName.equals("Africa")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.main_portrait);
        } else if (mapName.equals("Asia")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.main_landscape);
        }else if (mapName.equals("Latin America")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.main_portrait);
        }

        highScore = new Highscore(getApplicationContext());
        objectiveText = (TextView) findViewById(R.id.objective_text);
        scoreText = (TextView) findViewById(R.id.score_text);

        layout = (RelativeLayout) findViewById(R.id.root_view);

        if (timeOfThelevel == 0)
            timeOfThelevel = 6;
        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (mapName.equals("World")) {
            arrayLength = 17;
            currentLevel = 1;
            levelName = "Cities";
            initialize(1);
        } else if (mapName.equals("USA")) {
            arrayLength = 10;
            currentLevel = 26;
            levelName = "states(select the center)";
            initialize(21);
        } else if (mapName.equals("Europe")) {
            arrayLength = 8;
            currentLevel = 41;
            levelName = "Countries(select the center)";
            initialize(41);
        } else if (mapName.equals("Africa")) {
            arrayLength = 8;
            currentLevel = 61;
            levelName = "Countries\n(select the center)";
            initialize(61);
        } else if (mapName.equals("Asia")) {
            arrayLength = 8;
            currentLevel = 81;
            levelName = "Countries(select the center)";
            initialize(81);
        }else if (mapName.equals("Latin America")) {
            arrayLength = 9;
            currentLevel = 101;
            levelName = "Countries\n(select the center)";
            initialize(101);
        }
    }

    private void initialize(int level) {
        totalScore = 0;
        objectiveText.setText("                                                                                               ");
        objectiveText.invalidate();
        scoreText.setText("0");
        scoreText.invalidate();
        cities = new String[arrayLength];
        coor = new int[arrayLength][2];

        if (mapName.equals("World")) {
            layout.setBackgroundResource(R.drawable.world);
        } else if (mapName.equals("USA")) {
            layout.setBackgroundResource(R.drawable.usa);
        } else if (mapName.equals("Europe")) {
            layout.setBackgroundResource(R.drawable.europe);
        } else if (mapName.equals("Asia")) {
            layout.setBackgroundResource(R.drawable.asia);
        } else if (mapName.equals("Africa")) {
            layout.setBackgroundResource(R.drawable.africa);
        } else if (mapName.equals("Latin America")) {
            layout.setBackgroundResource(R.drawable.latin_america);
        }

        try {
            InputStream inputStream = null;
            switch (level) {
                // NEW LEVELS: Resources set here
                case 1:
                    inputStream = getResources().openRawResource(R.raw.level1);
                    break;
                case 2:
                    inputStream = getResources().openRawResource(R.raw.level2);
                    break;
                case 3:
                    inputStream = getResources().openRawResource(R.raw.level3);
                    break;
                case 4:
                    inputStream = getResources().openRawResource(R.raw.level4);
                    break;
                case 5:
                    inputStream = getResources().openRawResource(R.raw.level5);
                    break;
                case 6:
                    inputStream = getResources().openRawResource(R.raw.level6);
                    break;
                case 7:
                    inputStream = getResources().openRawResource(R.raw.level7);
                    break;
                case 8:
                    inputStream = getResources().openRawResource(R.raw.level8);
                    break;
                case 9:
                    inputStream = getResources().openRawResource(R.raw.level9);
                    break;
                case 10:
                    inputStream = getResources().openRawResource(R.raw.level10);
                    break;
                case 11:
                    inputStream = getResources().openRawResource(R.raw.level11);
                    break;
                case 12:
                    inputStream = getResources().openRawResource(R.raw.level12);
                    break;
                case 13:
                    inputStream = getResources().openRawResource(R.raw.level13);
                    break;
                case 14:
                    inputStream = getResources().openRawResource(R.raw.level14);
                    break;
                case 15:
                    inputStream = getResources().openRawResource(R.raw.level15);
                    break;
                case 16:
                    inputStream = getResources().openRawResource(R.raw.level16);
                    break;
                case 17:
                    inputStream = getResources().openRawResource(R.raw.level17);
                    break;
                case 18:
                    inputStream = getResources().openRawResource(R.raw.level18);
                    break;
                case 19:
                    inputStream = getResources().openRawResource(R.raw.level19);
                    break;
                case 21:
                    inputStream = getResources().openRawResource(R.raw.level21);
                    break;
                case 22:
                    inputStream = getResources().openRawResource(R.raw.level22);
                    break;
                case 23:
                    inputStream = getResources().openRawResource(R.raw.level23);
                    break;
                case 24:
                    inputStream = getResources().openRawResource(R.raw.level24);
                    break;
                case 25:
                    inputStream = getResources().openRawResource(R.raw.level25);
                    break;
                case 26:
                    inputStream = getResources().openRawResource(R.raw.level26);
                    break;
                case 27:
                    inputStream = getResources().openRawResource(R.raw.level27);
                    break;
                case 28:
                    inputStream = getResources().openRawResource(R.raw.level28);
                    break;
                case 29:
                    inputStream = getResources().openRawResource(R.raw.level29);
                    break;
                case 30:
                    inputStream = getResources().openRawResource(R.raw.level30);
                    break;
                case 31:
                    inputStream = getResources().openRawResource(R.raw.level31);
                    break;
                case 32:
                    inputStream = getResources().openRawResource(R.raw.level32);
                    break;
                case 33:
                    inputStream = getResources().openRawResource(R.raw.level33);
                    break;
                case 41:
                    inputStream = getResources().openRawResource(R.raw.level41);
                    break;
                case 42:
                    inputStream = getResources().openRawResource(R.raw.level42);
                    break;
                case 43:
                    inputStream = getResources().openRawResource(R.raw.level43);
                    break;
                case 44:
                    inputStream = getResources().openRawResource(R.raw.level44);
                    break;
                case 45:
                    inputStream = getResources().openRawResource(R.raw.level45);
                    break;
                case 46:
                    inputStream = getResources().openRawResource(R.raw.level46);
                    break;
                case 47:
                    inputStream = getResources().openRawResource(R.raw.level47);
                    break;
                case 48:
                    inputStream = getResources().openRawResource(R.raw.level48);
                    break;
                case 49:
                    inputStream = getResources().openRawResource(R.raw.level49);
                    break;
                case 50:
                    inputStream = getResources().openRawResource(R.raw.level50);
                    break;
                case 51:
                    inputStream = getResources().openRawResource(R.raw.level51);
                    break;
                case 52:
                    inputStream = getResources().openRawResource(R.raw.level52);
                    break;
                case 53:
                    inputStream = getResources().openRawResource(R.raw.level53);
                    break;
                case 54:
                    inputStream = getResources().openRawResource(R.raw.level54);
                    break;
                case 55:
                    inputStream = getResources().openRawResource(R.raw.level55);
                    break;
                case 56:
                    inputStream = getResources().openRawResource(R.raw.level56);
                    break;
                case 57:
                    inputStream = getResources().openRawResource(R.raw.level57);
                    break;
                case 58:
                    inputStream = getResources().openRawResource(R.raw.level58);
                    break;
                case 59:
                    inputStream = getResources().openRawResource(R.raw.level59);
                    break;
                case 60:
                    inputStream = getResources().openRawResource(R.raw.level60);
                    break;
                case 61:
                    inputStream = getResources().openRawResource(R.raw.level61);
                    break;
                case 62:
                    inputStream = getResources().openRawResource(R.raw.level62);
                    break;
                case 63:
                    inputStream = getResources().openRawResource(R.raw.level63);
                    break;
                case 64:
                    inputStream = getResources().openRawResource(R.raw.level64);
                    break;
                case 65:
                    inputStream = getResources().openRawResource(R.raw.level65);
                    break;
                case 66:
                    inputStream = getResources().openRawResource(R.raw.level66);
                    break;
                case 67:
                    inputStream = getResources().openRawResource(R.raw.level67);
                    break;
                case 68:
                    inputStream = getResources().openRawResource(R.raw.level68);
                    break;
                case 69:
                    inputStream = getResources().openRawResource(R.raw.level69);
                    break;
                case 70:
                    inputStream = getResources().openRawResource(R.raw.level70);
                    break;
                case 71:
                    inputStream = getResources().openRawResource(R.raw.level71);
                    break;
                case 72:
                    inputStream = getResources().openRawResource(R.raw.level72);
                    break;
                case 73:
                    inputStream = getResources().openRawResource(R.raw.level73);
                    break;
                case 74:
                    inputStream = getResources().openRawResource(R.raw.level74);
                    break;
                case 75:
                    inputStream = getResources().openRawResource(R.raw.level75);
                    break;
                case 76:
                    inputStream = getResources().openRawResource(R.raw.level76);
                    break;
                case 77:
                    inputStream = getResources().openRawResource(R.raw.level77);
                    break;
                case 78:
                    inputStream = getResources().openRawResource(R.raw.level78);
                    break;
                case 79:
                    inputStream = getResources().openRawResource(R.raw.level79);
                    break;
                case 80:
                    inputStream = getResources().openRawResource(R.raw.level80);
                    break;
                case 81:
                    inputStream = getResources().openRawResource(R.raw.level81);
                    break;
                case 82:
                    inputStream = getResources().openRawResource(R.raw.level82);
                    break;
                case 83:
                    inputStream = getResources().openRawResource(R.raw.level83);
                    break;
                case 84:
                    inputStream = getResources().openRawResource(R.raw.level84);
                    break;
                case 85:
                    inputStream = getResources().openRawResource(R.raw.level85);
                    break;
                case 86:
                    inputStream = getResources().openRawResource(R.raw.level86);
                    break;
                case 87:
                    inputStream = getResources().openRawResource(R.raw.level87);
                    break;
                case 88:
                    inputStream = getResources().openRawResource(R.raw.level88);
                    break;
                case 89:
                    inputStream = getResources().openRawResource(R.raw.level89);
                    break;
                case 90:
                    inputStream = getResources().openRawResource(R.raw.level90);
                    break;
                case 91:
                    inputStream = getResources().openRawResource(R.raw.level91);
                    break;
                case 92:
                    inputStream = getResources().openRawResource(R.raw.level92);
                    break;
                case 93:
                    inputStream = getResources().openRawResource(R.raw.level93);
                    break;
                case 94:
                    inputStream = getResources().openRawResource(R.raw.level94);
                    break;
                case 95:
                    inputStream = getResources().openRawResource(R.raw.level95);
                    break;
                case 96:
                    inputStream = getResources().openRawResource(R.raw.level96);
                    break;
                case 97:
                    inputStream = getResources().openRawResource(R.raw.level97);
                    break;
                case 98:
                    inputStream = getResources().openRawResource(R.raw.level98);
                    break;
                case 99:
                    inputStream = getResources().openRawResource(R.raw.level99);
                    break;
                case 101:
                    inputStream = getResources().openRawResource(R.raw.level101);
                    break;
                case 102:
                    inputStream = getResources().openRawResource(R.raw.level102);
                    break;
                case 103:
                    inputStream = getResources().openRawResource(R.raw.level103);
                    break;
                case 104:
                    inputStream = getResources().openRawResource(R.raw.level104);
                    break;
                case 105:
                    inputStream = getResources().openRawResource(R.raw.level105);
                    break;
                case 106:
                    inputStream = getResources().openRawResource(R.raw.level106);
                    break;
                case 107:
                    inputStream = getResources().openRawResource(R.raw.level107);
                    break;
                case 108:
                    inputStream = getResources().openRawResource(R.raw.level108);
                    break;
                case 109:
                    inputStream = getResources().openRawResource(R.raw.level109);
                    break;
                case 110:
                    inputStream = getResources().openRawResource(R.raw.level110);
                    break;
                case 111:
                    inputStream = getResources().openRawResource(R.raw.level111);
                    break;
                case 112:
                    inputStream = getResources().openRawResource(R.raw.level112);
                    break;
                case 113:
                    inputStream = getResources().openRawResource(R.raw.level113);
                    break;
                case 114:
                    inputStream = getResources().openRawResource(R.raw.level114);
                    break;
                case 115:
                    inputStream = getResources().openRawResource(R.raw.level115);
                    break;
                case 116:
                    inputStream = getResources().openRawResource(R.raw.level116);
                    break;
                case 117:
                    inputStream = getResources().openRawResource(R.raw.world_attraction_level_1);
                    break;
                case 118:
                    inputStream = getResources().openRawResource(R.raw.world_attraction_level_2);
                    break;
                case 119:
                    inputStream = getResources().openRawResource(R.raw.world_attraction_level_3);
                    break;
                case 120:
                    inputStream = getResources().openRawResource(R.raw.world_attraction_level_4);
                    break;
                default:
                    break;
            }

            // prepare the file for reading
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;

            // read every line of the file into the line-variable, on line at
            // the time
            int i = 0;
            String[] temp;
            while ((line = buffreader.readLine()) != null) {
                temp = line.split(",");
                cities[i] = temp[0].trim();
                coor[i][0] = Integer.parseInt(temp[1].trim());
                coor[i][1] = Integer.parseInt(temp[2].trim());
                i++;
            }

            // close the file again
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (graphic != null)
            graphic.initializeGraphics();
        graphic = new GraphicsView(this);
        addContentView(graphic, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
    }

    private void requestNewInterstitial() {
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public class GraphicsView extends View {
        private boolean GameOn;
        private Handler timerHandler;
        private Handler cityHandler;
        private Handler handler;
        private int timee;
        private Toast toast;
        private int filled;
        private int xCoor;
        private int yCoor;
        private int counter;
        private int currentObjective;

        private boolean touchedLocation;

        private boolean answer;

        private boolean showAttraction;

        private boolean[] lines;

        final Dialog dialog1;

        Dialog dialog2;

        public GraphicsView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            initializeGraphics();
            toast = Toast
                    .makeText(getContext(), timee + "", Toast.LENGTH_SHORT);
            dialog1 = new Dialog(MainActivity.this);

            dialog1.setContentView(R.layout.custom_dialogue);
            dialog1.setTitle("Ready!");

            final TextView text = (TextView) dialog1.findViewById(R.id.text);
            text.setTextSize(15);
            text.setText("Click as close to the given locations as fast as you can.\nMinumum score for this" +
                    " level is: " + minimumScore);

            if (mapName.equals("Asia") || mapName.equals("Latin America"))
                text.setText("Click close to the locations quickly\n minumum score is: "
                        + minimumScore);
            Button okButton = (Button) dialog1.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    dialog2 = new Dialog(MainActivity.this);
                    dialog2.setContentView(R.layout.custom_dialogue);
                    if (mapName.equals("World"))
                        dialog2.setTitle("Level " + currentLevel);
                    else if (mapName.equals("USA"))
                        dialog2.setTitle("Level " + (currentLevel - 25));
                    else if (mapName.equals("Europe"))
                        dialog2.setTitle("Level " + (currentLevel - 40));
                    else if (mapName.equals("Asia"))
                        dialog2.setTitle("Level " + (currentLevel - 80));
                    else if (mapName.equals("Africa"))
                        dialog2.setTitle("Level " + (currentLevel - 60));
                    else if (mapName.equals("Latin America"))
                        dialog2.setTitle("Level " + (currentLevel - 100));
                    final TextView text = (TextView) dialog2.findViewById(R.id.text);
                    text.setTextSize(20);
                    text.setText(levelName);
                    Button okButton = (Button) dialog2.findViewById(R.id.ok_button);
                    okButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            timerHandler = new Handler();
                            cityHandler = new Handler();
                            handler = new Handler();
                            new Thread() {
                                public void run() {
                                    runThread();
                                }
                            }.start();
                        }
                    });
                    if (!isFinishing())
                        dialog2.show();

                }
            });
            if (!isFinishing())
                dialog1.show();

        }

        private void initializeGraphics() {
            rands = new int[6];
            touchedLocation = false;
            answer = false;
            showAttraction = false;
            postInvalidate();
            xCoor = -1;
            yCoor = -1;
            timee = timeOfThelevel;
            GameOn = false;
            filled = 0;
            for (int i = 0; i < rands.length; i++) {
                rands[i] = -1;
            }
        }

        @SuppressWarnings("static-access")
        private void runThread() {
            try {
                counter++;
                xCoor = -1;
                yCoor = -1;
                touchedLocation = false;
                answer = false;
                timee = timeOfThelevel;
                lines = new boolean[10];
                GameOn = true;
                postInvalidate();
                cityHandler.post(cityRunnable);

                for (int i = timee; i > 0 && GameOn; i--) {
                    timerHandler.post(timer);
                    Thread.currentThread().sleep(1000);
                    timee--;
                }
                if (GameOn)
                    handler.post(handlerRunnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getCurrent() {
            Random rand = new Random();
            int cur = 0;
            cur = rand.nextInt(cities.length);
            while (!check(rands, cur, filled))
                cur = rand.nextInt(cities.length);
            rands[filled] = cur;
            filled++;
            return cur;
        }

        private boolean check(int[] randoms, int number, int length) {
            for (int j = 0; j < length; j++) {
                if (randoms[j] == number)
                    return false;
            }
            return true;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Bitmap bmp = null;

            if (showAttraction) {
                float scaleFactorX = 1 + (((float) getWidth() - 1920) / 1920);
                float scaleFactorY = 1 + (((float) getHeight() - 1080) / 1080);

                String resourceName = "world_" + (attractionLevelNum - 1) + "_" +
                        (currentObjective + 1);

                int id = getResources().getIdentifier(resourceName,
                        "drawable",
                        getPackageName());
                bmp = BitmapFactory.decodeResource(getResources(), id);

                int height = Math.round(getHeight() - (75 * scaleFactorY));

                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(3);

                canvas.drawRect(Math.round(100 * scaleFactorX),
                        height - Math.round(300 * scaleFactorY),
                        Math.round(400 * scaleFactorX),
                        height,
                        paint);
                canvas.drawBitmap(bmp,
                        null,
                        new Rect(Math.round(110 * scaleFactorX),
                                height - Math.round(290 * scaleFactorY),
                                Math.round(390 * scaleFactorX),
                                height - Math.round(10 * scaleFactorY)),
                        null);
            }

            // Draw touch and objective distance
            Paint paint = new Paint();
            if (xCoor != -1 && yCoor != -1) {
                if (touchedLocation) {
                    paint.setColor(Color.rgb(147, 147, 255));
                    paint.setStrokeWidth(10);
                    paint.setStyle(Style.STROKE);
                    bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.a);
                    canvas.drawBitmap(bmp, null, new Rect(xCoor - 36,
                            yCoor - 72, xCoor + 36, yCoor), null);
                }

                if (answer) {
                    bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.b);

                    int randX = 0;
                    int randY = 0;

                    float scaleFactorX;
                    float scaleFactorY;

                    backgroundX = layout.getWidth();
                    backgroundY = layout.getHeight();

                    if (mapName.equals("Africa") || mapName.equals("Latin America")) {
                        scaleFactorX = 1 + (((float) getWidth() - 1080) / 1080);
                        scaleFactorY = 1 + (((float) getHeight() - 1920) / 1920);
                    } else {
                        scaleFactorX = 1 + (((float) getWidth() - 1920) / 1920);
                        scaleFactorY = 1 + (((float) getHeight() - 1080) / 1080);
                    }

                    if (!attractionLevel && !mapName.equals("Latin America")) {
                        randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX);
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY);
                    }

                    if (mapName.equals("Latin America")) {
                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);

                        scaleFactorX = 1 + (((float) backgroundX - 360) / 360);
                        scaleFactorY = 1 + (((float) backgroundY - 389) / 389);

                        randX = Math.round((coor[rands[filled - 1]][0]) * scaleFactorX);
                        randY = Math.round((coor[rands[filled - 1]][1]) * scaleFactorY) +
                                paddingVert;
                    }

                    if (attractionLevel) {
                        randX = Math.round((coor[rands[filled - 1]][0] * 3 + 50) * scaleFactorX);
                        randY = Math.round((coor[rands[filled - 1]][1] * 3) * scaleFactorY);
                    }

                    if (mapName.equals("USA")) {
                        // Objective centering algorithm for USA
                        int diff = (int) Math.round((randY - (getHeight() / 2)) * .8);
                        randY = (getHeight() / 2) + diff - 20;
                    }

                    if (mapName.equals("Europe")) {
                        // Objective centering algorith for Europe
                        randY -= Math.round(40 * scaleFactorY);
                    }

                    if (mapName.equals("Asia")) {
                        // Objective centering algorithm for Asia
                        // Fix centering issue with padding on large resg
                        int paddingHoriz = Math.round((getWidth() - backgroundX) / 2);
                        scaleFactorX = backgroundX / 1920.0f;
                        randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX) +
                                paddingHoriz;

                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                        scaleFactorY = backgroundY / 1080.0f;
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                                paddingVert;
                    }

                    if (mapName.equals("Africa")) {
                        // Objective centering algorithm for Africa
                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                        scaleFactorY = backgroundY / 1920.0f;
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                                paddingVert;

                        if (mapName.equals("Africa")) {
                            randX -= Math.round(75 * scaleFactorX);
                            randY += Math.round(50 * scaleFactorY);
                        }
                    }

                    canvas.drawBitmap(bmp, null, new Rect(randX - 36,
                            randY - 36, randX + 36, randY + 36), null);
                }



                paint.setColor(Color.rgb(255, 157, 60));
                paint.setStrokeWidth(10);


                int randX = 0;
                int randY = 0;

                float scaleFactorX;
                float scaleFactorY;

                backgroundX = layout.getWidth();
                backgroundY = layout.getHeight();

                if (mapName.equals("Africa") || mapName.equals("Latin America")) {
                    scaleFactorX = 1 + (((float) getWidth() - 1080) / 1080);
                    scaleFactorY = 1 + (((float) getHeight() - 1920) / 1920);
                } else {
                    scaleFactorX = 1 + (((float) getWidth() - 1920) / 1920);
                    scaleFactorY = 1 + (((float) getHeight() - 1080) / 1080);
                }

                if (!attractionLevel && !mapName.equals("Latin America")) {
                    randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX);
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY);
                }

                if (mapName.equals("Latin America")) {
                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);

                    scaleFactorX = 1 + (((float) backgroundX - 360) / 360);
                    scaleFactorY = 1 + (((float) backgroundY - 389) / 389);

                    randX = Math.round((coor[rands[filled - 1]][0]) * scaleFactorX);
                    randY = Math.round((coor[rands[filled - 1]][1]) * scaleFactorY) +
                            paddingVert;
                }

                if (attractionLevel) {
                    randX = Math.round((coor[rands[filled - 1]][0] * 3 + 50) * scaleFactorX);
                    randY = Math.round((coor[rands[filled - 1]][1] * 3) * scaleFactorY);
                }

                if (mapName.equals("USA")) {
                    // Objective centering algorithm for USA
                    int diff = (int) Math.round((randY - (getHeight() / 2)) * .8);
                    randY = (getHeight() / 2) + diff - 20;
                }

                if (mapName.equals("Europe")) {
                    // Objective centering algorith for Europe
                    randY -= Math.round(40 * scaleFactorY);
                }

                if (mapName.equals("Asia")) {
                    // Objective centering algorithm for Asia
                    // Fix centering issue with padding on large resg
                    int paddingHoriz = Math.round((getWidth() - backgroundX) / 2);
                    scaleFactorX = backgroundX / 1920.0f;
                    randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX) +
                            paddingHoriz;

                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                    scaleFactorY = backgroundY / 1080.0f;
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                            paddingVert;
                }

                if (mapName.equals("Africa")) {
                    // Objective centering algorithm for Africa
                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                    scaleFactorY = backgroundY / 1920.0f;
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                            paddingVert;

                    randX -= Math.round(75 * scaleFactorX);
                    randY += Math.round(50 * scaleFactorY);
                }

                canvas.drawLine(xCoor, yCoor, randX, randY, paint);
            }
        }

        private Runnable handlerRunnable = new Runnable() {
            public void run() {
                GameOn = false;
                Handler hand = new Handler();
                hand.post(dialogueRunnable);
            }
        };

        private Runnable timer = new Runnable() {
            public void run() {
                View layout = getLayoutInflater().inflate(R.layout.toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root), false);
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText(timee + "");

                if (mapName.equals("World") ||
                        mapName.equals("USA") ||
                        mapName.equals("Europe")) {
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                } else {
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 15);
                }
                toast.setView(layout);
                toast.show();
            }
        };

        private Runnable cityRunnable = new Runnable() {
            public void run() {
                currentObjective = getCurrent();
                objectiveText.setText(cities[currentObjective]);
                if (attractionLevel) {
                    showAttraction = true;
                }
                postInvalidate();
            }
        };

        private Runnable dialogueRunnable = new Runnable() {
            public void run() {
                AlertDialog.Builder builder;
                final AlertDialog alertDialog;

                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_dialogue,
                        (ViewGroup) findViewById(R.id.layout_root));

                // Display level score
                int score = 0;
                if (xCoor != -1 && yCoor != -1) {
                    int dis;

                    int randX = 0;
                    int randY = 0;

                    float scaleFactorX;
                    float scaleFactorY;

                    if (mapName.equals("Africa") || mapName.equals("Latin America")) {
                        scaleFactorX = 1 + (((float) getWidth() - 1080) / 1080);
                        scaleFactorY = 1 + (((float) getHeight() - 1920) / 1920);
                    } else {
                        scaleFactorX = 1 + (((float) getWidth() - 1920) / 1920);
                        scaleFactorY = 1 + (((float) getHeight() - 1080) / 1080);
                    }

                    if (!attractionLevel && !mapName.equals("Latin America")) {
                        randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX);
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY);
                    }

                    if (mapName.equals("Latin America")) {
                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);

                        scaleFactorX = 1 + (((float) backgroundX - 360) / 360);
                        scaleFactorY = 1 + (((float) backgroundY - 389) / 389);

                        randX = Math.round((coor[rands[filled - 1]][0]) * scaleFactorX);
                        randY = Math.round((coor[rands[filled - 1]][1]) * scaleFactorY) +
                                paddingVert;
                    }

                    if (attractionLevel) {
                        randX = Math.round((coor[rands[filled - 1]][0] * 3 + 50) * scaleFactorX);
                        randY = Math.round((coor[rands[filled - 1]][1] * 3) * scaleFactorY);
                    }

                    if (mapName.equals("USA")) {
                        // Objective centering algorithm for USA
                        int diff = (int) Math.round((randY - (getHeight() / 2)) * .8);
                        randY = (getHeight() / 2) + diff - 20;
                    }

                    if (mapName.equals("Europe")) {
                        // Objective centering algorith for Europe
                        randY -= Math.round(40 * scaleFactorY);
                    }

                    if (mapName.equals("Asia")) {
                        // Objective centering algorithm for Asia
                        // Fix centering issue with padding on large resg
                        int paddingHoriz = Math.round((getWidth() - backgroundX) / 2);
                        scaleFactorX = backgroundX / 1920.0f;
                        randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX) +
                                paddingHoriz;

                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                        scaleFactorY = backgroundY / 1080.0f;
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                                paddingVert;
                    }

                    if (mapName.equals("Africa")) {
                        // Objective centering algorithm for Africa
                        int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                        scaleFactorY = backgroundY / 1920.0f;
                        randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                                paddingVert;

                        randX -= Math.round(75 * scaleFactorX);
                        randY += Math.round(50 * scaleFactorY);
                    }

                    dis = (int) Math.pow(Math.pow(xCoor - randX, 2)
                            + Math.pow(yCoor - randY, 2), .5);

                    score = dis;
                    score = 171 - score;
                }

                int scoreTime = 0;

                if (score <= 0)
                    score = 0;
                else {
                    scoreTime = timee;
                    scoreTime *= 10;
                    score += scoreTime;
                }

                totalScore += score;
                scoreText.setText("" + totalScore);
                TextView texte = (TextView) layout.findViewById(R.id.text);

                if (score == 0)
                    texte.setText("   Score: " + score + "   ");
                else {
                    texte.setTextSize(20);
                    texte.setText("   Score: " + (score - scoreTime)
                            + "   \nBonus speed: " + scoreTime);
                }

                Button okButton = (Button) layout.findViewById(R.id.ok_button);
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(layout);
                alertDialog = builder.create();

                okButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (counter < 5) {
                            timerHandler = new Handler();
                            cityHandler = new Handler();

                            new Thread() {
                                public void run() {
                                    runThread();
                                }
                            }.start();
                        } else {
                            objectiveText.setText("");
                            objectiveText.postInvalidate();
                            MediaPlayer mp = null;
                            final Dialog dialog3 = new Dialog(MainActivity.this);
                            dialog3.setContentView(R.layout.custom_dialogue);
                            final TextView text = (TextView) dialog3
                                    .findViewById(R.id.text);
                            text.setTextSize(20);
                            if (totalScore >= minimumScore) {
                                totalScoreForAllLevels += totalScore;
                                int position = 0;

                                if (mapName.equals("World"))
                                    position = 0;

                                if (mapName.equals("USA"))
                                    position = 1;

                                if (mapName.equals("Europe"))
                                    position = 2;

                                if (mapName.equals("Asia"))
                                    position = 3;

                                if (mapName.equals("Africa"))
                                    position = 4;

                                if (mapName.equals("Latin America"))
                                    position = 5;

                                if (highScore.addScore("k" + "," + (position),
                                        totalScoreForAllLevels)) {
                                    dialog2 = new Dialog(MainActivity.this);
                                    dialog2
                                            .setContentView(R.layout.custom_dialogue);
                                    dialog2.setTitle("Congratulations");
                                    final TextView text4 = (TextView) dialog2
                                            .findViewById(R.id.text);
                                    text4.setTextSize(20);
                                    text4.setText("Congratulations, you've achieved a new high score");
                                    Button okButton = (Button) dialog2
                                            .findViewById(R.id.ok_button);
                                    okButton
                                            .setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    dialog2.dismiss();
                                                    nextLevel(dialog3, text,
                                                            true);
                                                }
                                            });
                                    mp = MediaPlayer.create(getContext(),
                                            R.raw.highest_score);
                                    if (soundsOn){
                                        try {
                                            mp.prepare();
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mp.start();
                                    }
                                    if (!isFinishing())
                                        dialog2.show();
                                } else {
                                    mp = MediaPlayer.create(getContext(),
                                            R.raw.next_level);
                                    if (soundsOn){
                                        try {
                                            mp.prepare();
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mp.start();
                                    }
                                    nextLevel(dialog3, text, false);
                                }
                                if (currentLevel == 19 || currentLevel == 33
                                        || currentLevel == 60|| currentLevel == 80 || currentLevel == 99 || currentLevel == 116) {
                                    final Dialog dialog4 = new Dialog(MainActivity.this);
                                    dialog4
                                            .setContentView(R.layout.custom_dialogue2);
                                    dialog4
                                            .setTitle("Congratulations! You finished the game");
                                    Button restartButton = (Button) dialog4
                                            .findViewById(R.id.restart_game_button);
                                    Button startOverButton = (Button) dialog4
                                            .findViewById(R.id.reset_button);
                                    restartButton
                                            .setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent j = new Intent(
                                                            getApplicationContext(),
                                                            MapStarActivity.class);
                                                    startActivity(j);
                                                    finish();
                                                }
                                            });
                                    startOverButton
                                            .setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog4.dismiss();
                                                    totalScoreForAllLevels = 0;
                                                    minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                                    if (mapName.equals("World")) {
                                                        arrayLength = 17;
                                                        currentLevel = 1;
                                                        levelName = "Cities";
                                                        initialize(1);
                                                    } else if (mapName.equals("USA")) {
                                                        arrayLength = 10;
                                                        currentLevel = 26;
                                                        levelName = "states(select the center)";
                                                        initialize(21);
                                                    } else if (mapName.equals("Europe")) {
                                                        arrayLength = 8;
                                                        currentLevel = 41;
                                                        levelName = "Countries(select the center)";
                                                        initialize(41);
                                                    } else if (mapName.equals("Asia")) {
                                                        arrayLength = 8;
                                                        currentLevel = 81;
                                                        levelName = "Countries\n(select the center)";
                                                        initialize(81);
                                                    }else if (mapName.equals("Africa")) {
                                                        arrayLength = 8;
                                                        currentLevel = 61;
                                                        levelName = "Countries(select the center)";
                                                        initialize(61);
                                                    }else if (mapName.equals("LAtin America")) {
                                                        arrayLength = 8;
                                                        currentLevel = 61;
                                                        levelName = "Countries\n(select the center)";
                                                        initialize(61);
                                                    }
                                                }
                                            });
                                    if (!isFinishing())
                                        dialog4.show();
                                }
                            }
                            // *************************************
                            else {
                                mp = MediaPlayer.create(getContext(),
                                        R.raw.fail_level);
                                if (soundsOn){
                                    try {
                                        mp.prepare();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    mp.start();
                                }
                                final Dialog dialog4 = new Dialog(MainActivity.this);
                                dialog4
                                        .setContentView(R.layout.custom_dialogue2);
                                dialog4.setTitle("Sorry , You lose!"
                                        + "\nHighest score is: "
                                        + highScore.getScore(mapName));
                                Button restartButton = (Button) dialog4
                                        .findViewById(R.id.restart_game_button);
                                Button startOverButton = (Button) dialog4
                                        .findViewById(R.id.reset_button);
                                restartButton
                                        .setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent j = new Intent(
                                                        getApplicationContext(),
                                                        MapStarActivity.class);
                                                startActivity(j);
                                                finish();
                                            }
                                        });
                                startOverButton
                                        .setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog4.dismiss();
                                                totalScoreForAllLevels = 0;
                                                minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                                if (mapName.equals("World")) {
                                                    arrayLength = 17;
                                                    currentLevel = 1;
                                                    levelName = "Cities";
                                                    initialize(1);
                                                } else if (mapName.equals("USA")) {
                                                    arrayLength = 10;
                                                    currentLevel = 26;
                                                    levelName = "states(select the center)";
                                                    initialize(21);
                                                } else if (mapName.equals("Europe")) {
                                                    arrayLength = 8;
                                                    currentLevel = 41;
                                                    levelName = "Countries(select the center)";
                                                    initialize(41);
                                                } else if (mapName.equals("Asia")) {
                                                    arrayLength = 8;
                                                    currentLevel = 81;
                                                    levelName = "Countries(select the center)";
                                                    initialize(81);
                                                }else if (mapName.equals("Africa")) {
                                                    arrayLength = 8;
                                                    currentLevel = 61;
                                                    levelName = "Countries(select the center)";
                                                    initialize(61);
                                                }else if (mapName.equals("Latin America")) {
                                                    arrayLength = 9;
                                                    currentLevel = 101;
                                                    levelName = "Countries(select the center)";
                                                    initialize(101);
                                                }
                                            }
                                        });
                                if (!isFinishing())
                                    dialog4.show();
                            }
                        }
                        alertDialog.dismiss();
                    }

                    private void nextLevel(final Dialog dialog3,
                                           final TextView text, boolean high) {
                        attractionLevel = false;
                        dialog3.setTitle("Congratulations , You win!");
                        if (high)
                            text.setText("Score: " + totalScore
                                    + "  Total score: "
                                    + totalScoreForAllLevels);
                        else
                            text.setText("Score: " + totalScore
                                    + "  Total score: "
                                    + totalScoreForAllLevels);
                        Button okButton = (Button) dialog3
                                .findViewById(R.id.ok_button);
                        okButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog3.dismiss();

                                if ((currentLevel % 2) == 0 && !prem) {
                                    if (mInterstitialAd.isLoaded()) {
                                        mInterstitialAd.show();
                                    }
                                }

                                if (attractionLevelNext) {
                                    // Attraction level
                                    currentLevel = (attractionLevelNum * 5) - 3;
                                    attractionLevel = true;
                                    attractionLevelNext = false;
                                    arrayLength = 16;
                                    levelName = "Landmarks";
                                    minimumScore = (int) (.6 * 5 *
                                            (((timeOfThelevel - 1) * 10) + 150));
                                    attractionLevelNum++;
                                    initialize(116 + attractionLevelNum - 1);
                                } else {
                                    if (currentLevel == 2) {
                                        arrayLength = 15;
                                        currentLevel = 3;
                                        levelName = "Countries";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(2);
                                    } else if (currentLevel == 3) {
                                        arrayLength = 12;
                                        currentLevel = 4;
                                        levelName = "Capitals";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(3);
                                    } else if (currentLevel == 4) {
                                        arrayLength = 15;
                                        currentLevel = 5;
                                        levelName = "Countries";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(4);
                                    } else if (currentLevel == 5) {
                                        arrayLength = 15;
                                        currentLevel = 6;
                                        levelName = "Seas";
                                        attractionLevelNext = true;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(5);
                                    } else if (currentLevel == 7) {
                                        arrayLength = 10;
                                        currentLevel = 8;
                                        levelName = "Historical treasures";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(6);
                                    } else if (currentLevel == 8) {
                                        arrayLength = 15;
                                        currentLevel = 9;
                                        levelName = "Countries";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(7);
                                    } else if (currentLevel == 9) {
                                        arrayLength = 15;
                                        currentLevel = 10;
                                        levelName = "Islands";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(8);
                                    } else if (currentLevel == 10) {
                                        arrayLength = 15;
                                        currentLevel = 11;
                                        levelName = "Places Famous People were born";
                                        attractionLevelNext = true;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(9);
                                    } else if (currentLevel == 12) {
                                        arrayLength = 15;
                                        currentLevel = 13;
                                        levelName = "Capitals";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(10);
                                    } else if (currentLevel == 13) {
                                        arrayLength = 9;
                                        currentLevel = 14;
                                        levelName = "Nature";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(11);
                                    } else if (currentLevel == 14) {
                                        arrayLength = 10;
                                        currentLevel = 15;
                                        levelName = "Geo records";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(12);
                                    } else if (currentLevel == 15) {
                                        arrayLength = 15;
                                        currentLevel = 16;
                                        levelName = "Cities";
                                        attractionLevelNext = true;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(13);
                                    } else if (currentLevel == 17) {
                                        arrayLength = 13;
                                        currentLevel = 18;
                                        levelName = "Cities";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(14);
                                    } else if (currentLevel == 18) {
                                        arrayLength = 13;
                                        currentLevel = 19;
                                        levelName = "Capitals";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(15);
                                    } else if (currentLevel == 19) {
                                        arrayLength = 14;
                                        currentLevel = 20;
                                        levelName = "Cities";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(16);
                                    } else if (currentLevel == 20) {
                                        arrayLength = 13;
                                        currentLevel = 21;
                                        levelName = "Cities";
                                        attractionLevelNext = true;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(17);
                                    } else if (currentLevel == 22) {
                                        arrayLength = 12;
                                        currentLevel = 23;
                                        levelName = "Historical events";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(18);
                                    } else if (currentLevel == 23) {
                                        arrayLength = 14;
                                        currentLevel = 24;
                                        levelName = "Rivers - the flowing part";
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(19);
                                    } else if (currentLevel == 24) {
                                        attractionLevelNext = true;
                                        arrayLength = 10;
                                        currentLevel = 25;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(20);
                                        //ENDD WORLD LEVELS
                                    } else if (currentLevel == 26) {
                                        arrayLength = 10;
                                        currentLevel = 27;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(22);
                                    } else if (currentLevel == 27) {
                                        arrayLength = 10;
                                        currentLevel = 28;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(23);
                                    } else if (currentLevel == 28) {
                                        arrayLength = 9;
                                        currentLevel = 29;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(24);
                                    } else if (currentLevel == 29) {
                                        arrayLength = 9;
                                        currentLevel = 30;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(25);
                                    } else if (currentLevel == 30) {
                                        levelName = "Cities";
                                        arrayLength = 11;
                                        currentLevel = 31;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(26);
                                    } else if (currentLevel == 31) {
                                        arrayLength = 11;
                                        currentLevel = 32;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(27);
                                    } else if (currentLevel == 32) {
                                        arrayLength = 11;
                                        currentLevel = 33;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(28);
                                    } else if (currentLevel == 33) {
                                        arrayLength = 11;
                                        currentLevel = 34;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(29);
                                    } else if (currentLevel == 34) {
                                        arrayLength = 11;
                                        currentLevel = 35;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(30);
                                    } else if (currentLevel == 35) {
                                        arrayLength = 11;
                                        currentLevel = 36;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(31);
                                    } else if (currentLevel == 36) {
                                        levelName = "Rivers - the flowing part";
                                        arrayLength = 10;
                                        currentLevel = 37;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(32);
                                    } else if (currentLevel == 37) {
                                        levelName = "Lakes";
                                        arrayLength = 10;
                                        currentLevel = 38;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(33);
                                    } else if (currentLevel == 41) {
                                        arrayLength = 8;
                                        currentLevel = 42;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(42);
                                    } else if (currentLevel == 42) {
                                        arrayLength = 8;
                                        currentLevel = 43;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(43);
                                    } else if (currentLevel == 43) {
                                        arrayLength = 8;
                                        currentLevel = 44;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(44);
                                    } else if (currentLevel == 44) {
                                        arrayLength = 8;
                                        currentLevel = 45;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(45);
                                    } else if (currentLevel == 45) {
                                        levelName = "Cities";
                                        arrayLength = 8;
                                        currentLevel = 46;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(46);
                                    } else if (currentLevel == 46) {
                                        arrayLength = 8;
                                        currentLevel = 47;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(47);
                                    } else if (currentLevel == 47) {
                                        arrayLength = 8;
                                        currentLevel = 48;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(48);
                                    } else if (currentLevel == 48) {
                                        arrayLength = 8;
                                        currentLevel = 49;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(49);
                                    } else if (currentLevel == 49) {
                                        arrayLength = 8;
                                        currentLevel = 50;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(50);
                                    } else if (currentLevel == 50) {
                                        arrayLength = 8;
                                        currentLevel = 51;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(51);
                                    } else if (currentLevel == 51) {
                                        arrayLength = 8;
                                        currentLevel = 52;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(52);
                                    } else if (currentLevel == 52) {
                                        arrayLength = 8;
                                        currentLevel = 53;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(53);
                                    } else if (currentLevel == 53) {
                                        arrayLength = 8;
                                        currentLevel = 54;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(54);
                                    } else if (currentLevel == 54) {
                                        arrayLength = 8;
                                        currentLevel = 55;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(55);
                                    } else if (currentLevel == 55) {
                                        arrayLength = 8;
                                        currentLevel = 56;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(56);
                                    } else if (currentLevel == 56) {
                                        arrayLength = 8;
                                        currentLevel = 57;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(57);
                                    } else if (currentLevel == 57) {
                                        arrayLength = 8;
                                        currentLevel = 58;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(58);
                                    } else if (currentLevel == 58) {
                                        levelName = "Seas";
                                        arrayLength = 8;
                                        currentLevel = 59;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(59);
                                    } else if (currentLevel == 59) {
                                        levelName = "Rivers - the flowing part";
                                        arrayLength = 8;
                                        currentLevel = 60;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(60);
                                    } else if (currentLevel == 61) {
                                        arrayLength = 8;
                                        currentLevel = 62;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(62);
                                    } else if (currentLevel == 62) {
                                        arrayLength = 8;
                                        currentLevel = 63;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(63);
                                    } else if (currentLevel == 63) {
                                        arrayLength = 8;
                                        currentLevel = 64;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(64);
                                    } else if (currentLevel == 64) {
                                        arrayLength = 8;
                                        currentLevel = 65;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(65);
                                    } else if (currentLevel == 65) {
                                        levelName = "Cities";
                                        arrayLength = 8;
                                        currentLevel = 66;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(66);
                                    } else if (currentLevel == 66) {
                                        arrayLength = 8;
                                        currentLevel = 67;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(67);
                                    } else if (currentLevel == 67) {
                                        arrayLength = 8;
                                        currentLevel = 68;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(68);
                                    } else if (currentLevel == 68) {
                                        arrayLength = 8;
                                        currentLevel = 69;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(69);
                                    } else if (currentLevel == 69) {
                                        arrayLength = 8;
                                        currentLevel = 70;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(70);
                                    } else if (currentLevel == 70) {
                                        arrayLength = 8;
                                        currentLevel = 71;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(71);
                                    } else if (currentLevel == 71) {
                                        arrayLength = 8;
                                        currentLevel = 72;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(72);
                                    } else if (currentLevel == 72) {
                                        arrayLength = 8;
                                        currentLevel = 73;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(73);
                                    } else if (currentLevel == 73) {
                                        arrayLength = 8;
                                        currentLevel = 74;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(74);
                                    } else if (currentLevel == 74) {
                                        arrayLength = 8;
                                        currentLevel = 75;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(75);
                                    } else if (currentLevel == 75) {
                                        arrayLength = 8;
                                        currentLevel = 76;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(76);
                                    } else if (currentLevel == 76) {
                                        arrayLength = 8;
                                        currentLevel = 77;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(77);
                                    } else if (currentLevel == 77) {
                                        arrayLength = 8;
                                        currentLevel = 78;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(78);
                                    } else if (currentLevel == 78) {
                                        arrayLength = 8;
                                        currentLevel = 79;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(79);
                                    } else if (currentLevel == 79) {
                                        arrayLength = 8;
                                        currentLevel = 80;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(80);
                                    } else if (currentLevel == 81) {
                                        arrayLength = 8;
                                        currentLevel = 82;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(82);
                                    } else if (currentLevel == 82) {
                                        arrayLength = 8;
                                        currentLevel = 83;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(83);
                                    } else if (currentLevel == 83) {
                                        arrayLength = 8;
                                        currentLevel = 84;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(84);
                                    } else if (currentLevel == 84) {
                                        levelName = "Cities";
                                        arrayLength = 8;
                                        currentLevel = 85;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(85);
                                    } else if (currentLevel == 85) {
                                        arrayLength = 8;
                                        currentLevel = 86;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(86);
                                    } else if (currentLevel == 86) {
                                        arrayLength = 8;
                                        currentLevel = 87;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(87);
                                    } else if (currentLevel == 87) {
                                        arrayLength = 8;
                                        currentLevel = 88;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(88);
                                    } else if (currentLevel == 88) {
                                        arrayLength = 8;
                                        currentLevel = 89;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(89);
                                    } else if (currentLevel == 89) {
                                        arrayLength = 8;
                                        currentLevel = 90;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(90);
                                    } else if (currentLevel == 90) {
                                        arrayLength = 8;
                                        currentLevel = 91;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(91);
                                    } else if (currentLevel == 91) {
                                        arrayLength = 8;
                                        currentLevel = 92;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(92);
                                    } else if (currentLevel == 92) {
                                        arrayLength = 8;
                                        currentLevel = 93;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(93);
                                    } else if (currentLevel == 93) {
                                        arrayLength = 8;
                                        currentLevel = 94;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(94);
                                    } else if (currentLevel == 94) {
                                        arrayLength = 8;
                                        currentLevel = 95;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(95);
                                    } else if (currentLevel == 95) {
                                        arrayLength = 8;
                                        currentLevel = 96;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(96);
                                    } else if (currentLevel == 96) {
                                        arrayLength = 8;
                                        currentLevel = 97;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(97);
                                    } else if (currentLevel == 97) {
                                        arrayLength = 8;
                                        currentLevel = 98;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(98);
                                    } else if (currentLevel == 98) {
                                        arrayLength = 8;
                                        currentLevel = 99;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(99);
                                    }else if (currentLevel == 101) {
                                        arrayLength = 9;
                                        currentLevel = 102;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(102);
                                    } else if (currentLevel == 102) {
                                        arrayLength = 8;
                                        currentLevel = 103;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(103);
                                    } else if (currentLevel == 103) {
                                        arrayLength = 8;
                                        currentLevel = 104;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(104);
                                    } else if (currentLevel == 104) {
                                        levelName = "Cities";
                                        arrayLength = 8;
                                        currentLevel = 105;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(105);
                                    } else if (currentLevel == 105) {
                                        arrayLength = 8;
                                        currentLevel = 106;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(106);
                                    } else if (currentLevel == 106) {
                                        arrayLength = 10;
                                        currentLevel = 107;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(107);
                                    } else if (currentLevel == 107) {
                                        arrayLength = 9;
                                        currentLevel = 108;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(108);
                                    } else if (currentLevel == 108) {
                                        arrayLength = 8;
                                        currentLevel = 109;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(109);
                                    } else if (currentLevel == 109) {
                                        arrayLength = 8;
                                        currentLevel = 110;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(110);
                                    } else if (currentLevel == 110) {
                                        arrayLength = 8;
                                        currentLevel = 111;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(111);
                                    } else if (currentLevel == 111) {
                                        arrayLength = 8;
                                        currentLevel = 112;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(112);
                                    } else if (currentLevel == 112) {
                                        arrayLength = 8;
                                        currentLevel = 113;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(113);
                                    } else if (currentLevel == 113) {
                                        arrayLength = 8;
                                        currentLevel = 114;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(114);
                                    } else if (currentLevel == 114) {
                                        arrayLength = 8;
                                        currentLevel = 115;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(115);
                                    } else if (currentLevel == 115) {
                                        arrayLength = 8;
                                        currentLevel = 116;
                                        minimumScore = (int) (.6 * 5 * (((timeOfThelevel - 1) * 10) + 150));
                                        initialize(116);
                                    }
                                }
                            }
                        });
                        if (!isFinishing())
                            dialog3.show();
                    }
                });
                alertDialog.show();
            }
        };

        public boolean onTouchEvent(MotionEvent event) {
            if (GameOn) {
                xCoor = (int) event.getX();
                yCoor = (int) event.getY();
                postInvalidate();
                GameOn = false;
                touchedLocation = true;

                int randX = 0;
                int randY = 0;

                float scaleFactorX;
                float scaleFactorY;

                backgroundX = layout.getWidth();
                backgroundY = layout.getHeight();

                if (mapName.equals("Africa") || mapName.equals("Latin America")) {
                    scaleFactorX = 1 + (((float) getWidth() - 1080) / 1080);
                    scaleFactorY = 1 + (((float) getHeight() - 1920) / 1920);
                } else {
                    scaleFactorX = 1 + (((float) getWidth() - 1920) / 1920);
                    scaleFactorY = 1 + (((float) getHeight() - 1080) / 1080);
                }

                if (!attractionLevel && !mapName.equals("Latin America")) {
                    randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX);
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY);
                }

                if (mapName.equals("Latin America")) {
                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);

                    scaleFactorX = 1 + (((float) backgroundX - 360) / 360);
                    scaleFactorY = 1 + (((float) backgroundY - 389) / 389);

                    randX = Math.round((coor[rands[filled - 1]][0]) * scaleFactorX);
                    randY = Math.round((coor[rands[filled - 1]][1]) * scaleFactorY) +
                            paddingVert;
                }

                if (attractionLevel) {
                    randX = Math.round((coor[rands[filled - 1]][0] * 3 + 50) * scaleFactorX);
                    randY = Math.round((coor[rands[filled - 1]][1] * 3) * scaleFactorY);
                }

                if (mapName.equals("USA")) {
                    // Objective centering algorithm for USA
                    int diff = (int) Math.round((randY - (getHeight() / 2)) * .8);
                    randY = (getHeight() / 2) + diff - 20;
                }

                if (mapName.equals("Europe")) {
                    // Objective centering algorith for Europe
                    randY -= Math.round(40 * scaleFactorY);
                }

                if (mapName.equals("Asia")) {
                    // Objective centering algorithm for Asia
                    // Fix centering issue with padding on large resg
                    int paddingHoriz = Math.round((getWidth() - backgroundX) / 2);
                    scaleFactorX = backgroundX / 1920.0f;
                    randX =  Math.round((coor[rands[filled - 1]][0] * 4) * scaleFactorX) +
                            paddingHoriz;

                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                    scaleFactorY = backgroundY / 1080.0f;
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                            paddingVert;
                }

                if (mapName.equals("Africa")) {
                    // Objective centering algorithm for Africa
                    int paddingVert = Math.round((getHeight() - backgroundY) / 2);
                    scaleFactorY = backgroundY / 1920.0f;
                    randY =  Math.round((coor[rands[filled - 1]][1] * 4) * scaleFactorY) +
                            paddingVert;

                    randX -= Math.round(75 * scaleFactorX);
                    randY += Math.round(50 * scaleFactorY);
                }

                int dis;
                dis = (int) Math.pow(Math.pow(xCoor - randX, 2)
                        + Math.pow(yCoor - randY, 2), .5);
                int distance = xCoor - randX;
                if (distance > (450 - distance)) {
                    dis = (int) Math.pow(Math.pow(450 - distance, 2)
                            + Math.pow(yCoor - randY, 2), .5);
                }
                MediaPlayer mp = null;
                if (dis <= 120)
                    mp = MediaPlayer.create(getContext(), R.raw.win);
                else
                    mp = MediaPlayer.create(getContext(), R.raw.lose);
                if (soundsOn){
                    try {
                        mp.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
                postInvalidate();
                DrawCount drawCounter = new DrawCount(1500, 150);
                drawCounter.start();
            }
            return super.onTouchEvent(event);
        }

        public class DrawCount extends CountDownTimer {

            int current = 0;

            public DrawCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);

            }

            public void onFinish() {
                answer = true;
                postInvalidate();
                MyCount counter = new MyCount(1500, 1000);
                counter.start();
            }

            public void onTick(long millisUntilFinished) {
                lines[current] = true;
                current++;
                postInvalidate();
            }
        }

        public class MyCount extends CountDownTimer {
            public MyCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }

            public void onFinish() {
                handler = new Handler();
                handler.post(handlerRunnable);
            }

            public void onTick(long millisUntilFinished) {
            }
        }
    }
}
