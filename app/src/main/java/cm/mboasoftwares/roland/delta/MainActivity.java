package cm.mboasoftwares.roland.delta;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.graphics.Point;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Margins;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.RelativeLayout.BELOW;
import static android.widget.RelativeLayout.END_OF;

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private static final int START_LEVEL = 1;
    private int mLevel;
    private Button mNextLevelButton;
    private InterstitialAd mInterstitialAd;
    private TextView mLevelTextView;
    private TextView discriminantText;
    private TextView nbrSolutionsText;

    private static final int MARGIN_TOP = 100;

    private EditText aValue;
    private EditText bValue;
    private EditText cValue;

    Double delta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nbrSolutionsText = findViewById(R.id.number_of_solutions);
        nbrSolutionsText.setVisibility(View.INVISIBLE);

        discriminantText = (TextView) findViewById(R.id.app_title);
        discriminantText.setVisibility(View.INVISIBLE);

        // Create the next level button, which tries to show an interstitial when clicked.
        mNextLevelButton = ((Button) findViewById(R.id.next_level_button));
        mNextLevelButton.setEnabled(false);
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluate();
                showInterstitial();
            }
        });

        // Create the text view to show the level number.
        mLevelTextView = (TextView) findViewById(R.id.level);
        mLevel = START_LEVEL;
        mLevelTextView.setText(Html.fromHtml("&Delta").toString() + " =");
        mLevelTextView.setVisibility(View.INVISIBLE);

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        //Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        RelativeLayout.LayoutParams blp1 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp1.addRule(BELOW, R.id.equation_text);
        blp1.addRule(Gravity.CENTER);
        blp1.setMargins(0, MARGIN_TOP,0,0);

        RelativeLayout.LayoutParams blp2 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp2.addRule(BELOW, R.id.equation_text);
        blp2.addRule(Gravity.CENTER);
        blp2.addRule(END_OF, R.id.a_value_text);
        blp2.setMargins(0, MARGIN_TOP,0,0);

        RelativeLayout.LayoutParams blp3 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp3.addRule(BELOW, R.id.equation_text);
        blp3.addRule(Gravity.CENTER);
        blp3.addRule(END_OF, R.id.b_value_text);
        blp3.setMargins(0, MARGIN_TOP,0,0);

        RelativeLayout.LayoutParams edt1 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        edt1.addRule(BELOW, R.id.a_value_text);
        edt1.addRule(Gravity.CENTER);

        RelativeLayout.LayoutParams edt2 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        edt2.addRule(BELOW, R.id.a_value_text);
        edt2.addRule(Gravity.CENTER);
        edt2.addRule(END_OF, R.id.a_value);

        RelativeLayout.LayoutParams edt3 = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        edt3.addRule(BELOW, R.id.a_value_text);
        edt3.addRule(Gravity.CENTER);
        edt3.addRule(END_OF, R.id.b_value);

        TextView equationText = (TextView) findViewById(R.id.equation_text);
        equationText.setText(Html.fromHtml("Given function, f(x) = ax<sup>2</sup> + bx + c. Set the values of a, b and c below to evaluate the discriminant of equation f(x) = 0"));
        TextView aText = (TextView) findViewById(R.id.a_value_text);
        aText.setLayoutParams(blp1);
        TextView bText = (TextView) findViewById(R.id.b_value_text);
        bText.setLayoutParams(blp2);
        TextView cText = (TextView) findViewById(R.id.c_value_text);
        cText.setLayoutParams(blp3);

        aValue = (EditText) findViewById(R.id.a_value);
        aValue.setLayoutParams(edt1);
        bValue = (EditText) findViewById(R.id.b_value);
        bValue.setLayoutParams(edt2);
        cValue = (EditText) findViewById(R.id.c_value);
        cValue.setLayoutParams(edt3);
    }

    private double evaluate() {

        if (!isEditTextEmpty(aValue)) {
            Double a = Double.parseDouble(aValue.getText().toString().trim());
            if (!isEditTextEmpty(bValue)) {
                Double b = Double.parseDouble(bValue.getText().toString().trim());
                if (!isEditTextEmpty(cValue)) {
                    Double c = Double.parseDouble(cValue.getText().toString().trim());
                    delta = b*b - 4*a*c;
                    if (delta < 0) {
                        nbrSolutionsText.setText(Html.fromHtml("&Delta").toString() + " < 0. The equation f(x) = 0 has no solutions.");
                    } else if (delta == 0) {
                        nbrSolutionsText.setText(Html.fromHtml("&Delta").toString() + " = 0. The equation f(x) = 0 has one solution.");
                    } else {
                        nbrSolutionsText.setText(Html.fromHtml("&Delta").toString() + " > 0. The equation f(x) = 0 has two solutions.");
                    }
                    nbrSolutionsText.setVisibility(View.VISIBLE);
                    discriminantText.setVisibility(View.VISIBLE);
                    mLevelTextView.setVisibility(View.VISIBLE);
                    mLevelTextView.setText(Html.fromHtml("&Delta").toString() + " = " + delta);
                } else {
                    Toast.makeText(this, "Please enter the value of c", Toast.LENGTH_LONG).show();
                    cValue.requestFocus();
                }
            } else {
                Toast.makeText(this, "Please enter the value of b", Toast.LENGTH_LONG).show();
                bValue.requestFocus();
            }
        } else {
            Toast.makeText(this, "Please enter the value of a", Toast.LENGTH_LONG).show();
            aValue.requestFocus();
        }

        return 1.0;
    }
    private static boolean isEditTextEmpty( EditText et){
        String s = et.getText().toString();
        return s.equals("");  // really empty.
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mNextLevelButton.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mNextLevelButton.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        mNextLevelButton.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.

        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }
}
