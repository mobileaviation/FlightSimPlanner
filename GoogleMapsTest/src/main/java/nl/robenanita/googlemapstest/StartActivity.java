package nl.robenanita.googlemapstest;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import java.util.logging.Logger;

import nl.robenanita.googlemapstest.Classes.NetworkCheck;
import nl.robenanita.googlemapstest.Settings.SettingsActivity;
import nl.robenanita.googlemapstest.database.*;
import nl.robenanita.googlemapstest.inappbilling.util.IabHelper;
import nl.robenanita.googlemapstest.inappbilling.util.IabResult;
import nl.robenanita.googlemapstest.inappbilling.util.Inventory;
import nl.robenanita.googlemapstest.inappbilling.util.Purchase;

public class StartActivity extends ActionBarActivity {
    private String TAG = "GooglemapsTest";
    final String ITEM_SKU = "com.mobileaviationtools.noadds";
    private final int REQUEST_PERMISSION = 20;

    Button startNavigationBtn;
    TextView airportCountTxt;
    TextView flightplanCountTxt;
    TextView navaidsCountTxt;
    TextView fixesCountTxt;
    TextView runwaysCountTxt;
    TextView databaseVersionTxt;
    TextView fsuipcTextView;
    TextView xpuipcTextView;
    TextView installationTextView;
    TextView airportChartCountTxt;

    CountDownTimer adLoadTimer;

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkDatabaseVersion();

        boolean adds = loadAds();

        final TextView countDownTxt  = (TextView) findViewById(R.id.countDownTxt);
//        if (adds)
//            countDownTxt.setText("5 Sec..");
//        else
            countDownTxt.setVisibility(View.INVISIBLE);


        startNavigationBtn = (Button) findViewById(R.id.startNavigationBtn);
        startNavigationBtn.setEnabled(false);
        startNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startNavigationIntent = new Intent(StartActivity.this, NavigationActivity.class);
                startNavigationIntent.putExtra("key", 1);
                StartActivity.this.startActivityForResult(startNavigationIntent, 0);
            }
        });


        fsuipcTextView = (TextView) findViewById(R.id.fsuipcTextView);
        fsuipcTextView.setMovementMethod(LinkMovementMethod.getInstance());
        xpuipcTextView = (TextView) findViewById(R.id.xpuipcTextView);
        xpuipcTextView.setMovementMethod(LinkMovementMethod.getInstance());
        installationTextView = (TextView) findViewById(R.id.installationTextView);
        installationTextView.setMovementMethod(LinkMovementMethod.getInstance());

        SetupLinkbutton();

        GetCounts();

        checkPermissions();

        if (!checkPlayServices(this))
        {
            Toast.makeText(this, "There is a problem with the installed version of GooglePlay Services!", Toast.LENGTH_LONG).show();
        }

        NetworkCheck networkCheck = new NetworkCheck();
        networkCheck.SetOnResult(new NetworkCheck.OnResult() {
            @Override
            public void Checked(Boolean result) {
                Toast.makeText(StartActivity.this, "Internet connection is not present. This app will work without, but for charts loading its necessary!",
                    Toast.LENGTH_LONG).show();
            }
        });

        if (adds) {
            startNavigationBtn.setEnabled(true);
            setupInAppBilling();
        }
        else
        {
            startNavigationBtn.setEnabled(true);
        }
    }

    public boolean checkPlayServices(Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void checkPermissions()
    {
        new AskPermission.Builder(this).setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.WAKE_LOCK).setCallback(new PermissionCallback() {
            @Override
            public void onPermissionsGranted(int requestCode) {
                Toast.makeText(StartActivity.this, "Permission Received!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionsDenied(int requestCode) {
                Toast.makeText(StartActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }).setErrorCallback(new ErrorCallback() {
            @Override
            public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setMessage("We need extra permissions set for FlightSim Planner to work correctly!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionInterface.onDialogShown();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            @Override
            public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setMessage("We need extra permissions set for FlightSim Planner to work correctly!, Open the settings Screen");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionInterface.onSettingsShown();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        }).request(REQUEST_PERMISSION);
    }

    private void SetupLinkbutton() {
        ImageButton openaipBtn = (ImageButton) findViewById(R.id.openaipBtn);
        ImageButton openWeatherBtn = (ImageButton) findViewById(R.id.OpenWeatherMapBtn);
        ImageButton ourairportsBtn = (ImageButton) findViewById(R.id.ourairportsBtn);
        ImageButton xplaneBtn = (ImageButton) findViewById(R.id.xplaneBtn);
        ImageButton weatherBtn = (ImageButton) findViewById(R.id.weatherBtn);
        ImageButton canadaBtn = (ImageButton) findViewById(R.id.canadaBtn);
        ImageButton skylinesBtn = (ImageButton) findViewById(R.id.SkyLinesBtn);
        ImageButton chartBundleBtn = (ImageButton) findViewById(R.id.chartbundleBtn);

        openaipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.openaiplink));
            }
        });
        openWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.openweatherlink));
            }
        });
        ourairportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.ourairportlink));
            }
        });
        xplaneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.xplanelink));
            }
        });
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.weatherlink));
            }
        });
        canadaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.canadalink));
            }
        });
        skylinesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.skylineslink));
            }
        });
        chartBundleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getResources().getString(R.string.chartbundlelink));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void buyAddsClick()
    {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase)
            {
                if (result.isFailure()) {
                    // Handle error
                    return;
                }
                else if (purchase.getSku().equals(ITEM_SKU)) {
                    consumeItem();
                }

            }
        };

        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");


    }

    private void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        // Remove the Advertisements and set the database to purchased = 1
                        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(StartActivity.this);
                        propertiesDataSource.open(true);
                        propertiesDataSource.setNoAdvertisements();
                        propertiesDataSource.close(true);
                        removeAdds();

                    } else {
                        // handle error
                    }
                }
            };

    private void setupInAppBilling()
    {
        final Button buyAddsButton;
        buyAddsButton = (Button) findViewById(R.id.buyAppButton);

        final boolean debug = false;// BuildConfig.DEBUG;

        buyAddsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debug){
                    Log.i(TAG, "This is in debug so set the NoAdvertisement");
                    PropertiesDataSource propertiesDataSource = new PropertiesDataSource(StartActivity.this);
                    propertiesDataSource.open(true);
                    propertiesDataSource.setNoAdvertisements();
                    propertiesDataSource.close(true);
                }
                else {

                    buyAddsClick();
                }
            }


        });

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlvCpZVdjzA5ZnCbcf1ht9U0PnXIqlugZHqFpd/LlWX33JB9EwwfK0e+m8xOTyWOdZbvNAdkFjHh7UOoBtGIhVz3mAbbApRIndq6hmInfZzH2/X1QpccAlCJSvq8T9/rIHiShfWgiq5nJNdblJnA/2g7EtL8OBSXgZyqyZzgNCIKFUxz5cOrZ0+QF2IA8Gft1C1QFG7IcBE6Yc8un3blYF0A/MY/VUjUCTGLaxIRLNx6PVbB8hSwLOjULybZpFIQm2lSvo/zXISJs0cGqTHH+MV1dS27e23OkiFCiPhE3dGSZYUK4ZQhON3c47T1pPzW7aPuokij3k/LudFbXt9u4OQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);


        try {

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d(TAG, "In-app Billing setup failed: " +
                                result);
                        buyAddsButton.setEnabled(false);
                    } else {
                        Log.d(TAG, "In-app Billing is set up OK");
                        buyAddsButton.setEnabled(true);
                    }
                }
            });
        }
        catch(Exception e) {
            Log.d(TAG, "In-App Billing setup crash: " + e.getMessage());
            buyAddsButton.setEnabled(false);
        }
    }

    private void removeAdds()
    {
        Button buyAddsButton;
        buyAddsButton = (Button) findViewById(R.id.buyAppButton);
        buyAddsButton.setVisibility(View.GONE);

        LinearLayout adLeft = (LinearLayout) findViewById(R.id.adLeftLayout);
        adLeft.setVisibility(View.GONE);
        LinearLayout adRight = (LinearLayout) findViewById(R.id.adRightLayout);
        adRight.setVisibility(View.GONE);
    }


    Country selectedCounry;
    Continent selectedContinent;

    private void GetCounts() {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(this);
        flightPlanDataSource.open();
        Integer fpCount = flightPlanDataSource.GetFlightplanCount();
        flightPlanDataSource.close();

        AirportDataSource airportDataSource = new AirportDataSource(this);
        airportDataSource.open(-1);
        Integer apCount = airportDataSource.GetAirportsCount();
        airportDataSource.close();

        NavaidsDataSource navaidsDataSource = new NavaidsDataSource(this);
        navaidsDataSource.open(-1);
        Integer naCount = navaidsDataSource.GetNavaidsCount();
        navaidsDataSource.close();

        RunwaysDataSource runwaysDataSource = new RunwaysDataSource(this);
        runwaysDataSource.open();
        Integer ruCount = runwaysDataSource.GetRunwaysCount();
        runwaysDataSource.close();

        FixesDataSource fixesDataSource = new FixesDataSource(this);
        fixesDataSource.open(-1);
        Integer fiCount = fixesDataSource.GetFixesCount();
        fixesDataSource.close();

        AirportChartsDataSource airportChartsDataSource = new AirportChartsDataSource(this);
        airportChartsDataSource.open();
        Integer chartCount = airportChartsDataSource.GetChartCount();
        airportChartsDataSource.close();

        airportCountTxt = (TextView) findViewById(R.id.airportCountTxt);
        flightplanCountTxt = (TextView) findViewById(R.id.flightplanCountTxt);
        navaidsCountTxt = (TextView) findViewById(R.id.navaidsCountTxt);
        runwaysCountTxt = (TextView) findViewById(R.id.runwaysCountTxt);
        fixesCountTxt = (TextView) findViewById(R.id.fixesCountTxt);
        airportChartCountTxt = (TextView) findViewById(R.id.chartsCountTxt);
        airportCountTxt.setText(Integer.toString(apCount));
        flightplanCountTxt.setText(Integer.toString(fpCount));
        navaidsCountTxt.setText(Integer.toString(naCount));
        runwaysCountTxt.setText(Integer.toString(ruCount));
        fixesCountTxt.setText(Integer.toString(fiCount));
        airportChartCountTxt.setText(Integer.toString(chartCount));

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open(false);
        Property v = propertiesDataSource.GetProperty("DB_VERSION");
        propertiesDataSource.close(false);

        databaseVersionTxt = (TextView) findViewById(R.id.databaseVersionTxt);
        databaseVersionTxt.setText(v.value1);


    }

    private void checkDatabaseVersion() {
        CheckDatabaseSource checkDatabaseSource = new CheckDatabaseSource(this);
        checkDatabaseSource.open();
        checkDatabaseSource.checkVersion();
    }

    public boolean loadAds()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open(true);
        boolean v = propertiesDataSource.checkNoAdvertisements();
        propertiesDataSource.close(true);

        if (!v) {
            AdView leftAd = (AdView) findViewById(R.id.adLeft);
            AdRequest leftRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
                    .addTestDevice("B066A1E31D72FAD22CD44C97D2DAAFE6")
                    .build();
            leftAd.setAdSize(AdSize.BANNER);
            leftAd.setAdUnitId("ca-app-pub-5281313269938308/1228468278");
            leftAd.loadAd(leftRequest);
            leftAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    //adLoadTimer.start();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    Log.i(TAG, "Failed to load the add...");
                    //adLoadTimer.start();
                }
            });

            AdView rightAd = (AdView) findViewById(R.id.adRight);
            AdRequest rightRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
                    .addTestDevice("B066A1E31D72FAD22CD44C97D2DAAFE6")
                    .build();
            rightAd.setAdSize(AdSize.BANNER);
            rightAd.setAdUnitId("ca-app-pub-5281313269938308/5658667870");
            rightAd.loadAd(rightRequest);
            return true;
        }
        else
        {
            removeAdds();
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsActivity() {
        Intent startSettingsIntent = new Intent(StartActivity.this, SettingsActivity.class);
        StartActivity.this.startActivityForResult(startSettingsIntent, 400);
    }
}
