package nl.robenanita.googlemapstest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import nl.robenanita.googlemapstest.Settings.SettingsActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.CheckDatabaseSource;
import nl.robenanita.googlemapstest.database.FixesDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.database.NavaidsDataSource;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.inappbilling.util.IabHelper;
import nl.robenanita.googlemapstest.inappbilling.util.IabResult;
import nl.robenanita.googlemapstest.inappbilling.util.Inventory;
import nl.robenanita.googlemapstest.inappbilling.util.Purchase;

public class StartActivity extends ActionBarActivity {
    private String TAG = "GooglemapsTest";
    final String ITEM_SKU = "com.mobileaviationtools.noadds";

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

        //ipAddressTxt = (EditText) findViewById(R.id.ipAddressEdit);
        //setupIpAddressEdit();

        fsuipcTextView = (TextView) findViewById(R.id.fsuipcTextView);
        fsuipcTextView.setMovementMethod(LinkMovementMethod.getInstance());
        xpuipcTextView = (TextView) findViewById(R.id.xpuipcTextView);
        xpuipcTextView.setMovementMethod(LinkMovementMethod.getInstance());
        installationTextView = (TextView) findViewById(R.id.installationTextView);
        installationTextView.setMovementMethod(LinkMovementMethod.getInstance());

        SetupLinkbutton();

        GetCounts();

        //testSimBtn = (Button) findViewById(R.id.testSimServerBtn);
        //testSimBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TestServer("www.google.nl");
//            }
//        });

        if (adds) {
//            adLoadTimer = new CountDownTimer(5000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
//                    countDownTxt.setText(Long.toString(millisUntilFinished / 1000) + " Sec..");
//                }
//
//                public void onFinish() {
//                    countDownTxt.setText("");
//                    startNavigationBtn.setEnabled(true);
//                }
//            };

            //adLoadTimer.start();
            startNavigationBtn.setEnabled(true);
            setupInAppBilling();
        }
        else
        {
            startNavigationBtn.setEnabled(true);
        }
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
        if (mHelper != null)
            if (!mHelper.handleActivityResult(requestCode,
                    resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        else
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
                        propertiesDataSource.open();
                        propertiesDataSource.setNoAdvertisements();
                        propertiesDataSource.close();
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
                    propertiesDataSource.open();
                    propertiesDataSource.setNoAdvertisements();
                    propertiesDataSource.close();
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

//    private Boolean TestServer(final String ip) {
//        Boolean res = true;
//
//        class Ping extends AsyncTask<String, Integer, Void> {
//
//            @Override
//            protected Void doInBackground(String... strings) {
//                InetAddress in;
//                in = null;
//
//                try {
//                    in = InetAddress.getByName(ip);
//
//                } catch (UnknownHostException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                try {
//                    if (in.isReachable(5000)) {
//                        Log.i(TAG, "Responde OK");
//                    } else {
//                        Log.i(TAG, "No responde: Time out");
//                    }
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    Log.i(TAG, e.toString());
//                }
//                return null;
//            }
//        }
//
//        Ping ping = new Ping();
//        ping.execute();
//
//        return res;
//    }

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

        airportCountTxt = (TextView) findViewById(R.id.airportCountTxt);
        flightplanCountTxt = (TextView) findViewById(R.id.flightplanCountTxt);
        navaidsCountTxt = (TextView) findViewById(R.id.navaidsCountTxt);
        runwaysCountTxt = (TextView) findViewById(R.id.runwaysCountTxt);
        fixesCountTxt = (TextView) findViewById(R.id.fixesCountTxt);
        airportCountTxt.setText(Integer.toString(apCount));
        flightplanCountTxt.setText(Integer.toString(fpCount));
        navaidsCountTxt.setText(Integer.toString(naCount));
        runwaysCountTxt.setText(Integer.toString(ruCount));
        fixesCountTxt.setText(Integer.toString(fiCount));

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open();
        propertiesDataSource.FillProperties();
        propertiesDataSource.close();

        databaseVersionTxt = (TextView) findViewById(R.id.databaseVersionTxt);
        databaseVersionTxt.setText(propertiesDataSource.DBVersion.value1);

    }

    private void checkDatabaseVersion() {
        CheckDatabaseSource checkDatabaseSource = new CheckDatabaseSource(this);
        checkDatabaseSource.open();
        checkDatabaseSource.checkVersion();

//        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
//        propertiesDataSource.open();
//        propertiesDataSource.checkVersion();
    }

//    private void SetupSpinners() {
//        CountryDataSource countryDb = new CountryDataSource(this);
//        ContinentDataSource continentDb = new ContinentDataSource(this);
//
//        countryDb.open();
//        ArrayList<Country> countries = countryDb.getAllCountries(true);
//        countryDb.close();
//
//        continentDb.open();
//        ArrayList<Continent> continents = continentDb.getAllContinents(true);
//        continentDb.close();
//
//        CountryAdapter adapter = new CountryAdapter(countries);
//        countrySpinnerStart.setAdapter(adapter);
//
//        ContinentAdapter continentAdapter = new ContinentAdapter(continents);
//        continentSpinnerStart.setAdapter(continentAdapter);
//
//        countrySpinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                CountryAdapter adapter = (CountryAdapter) adapterView.getAdapter();
//                if (adapter != null) {
//                    selectedCounry = adapter.GetCountry(i);
//                    Log.i(TAG, "OnItemSelected: " + selectedCounry.name);
//                    downloadCountryAirportsBtn.setEnabled((selectedCounry != null));
//                    downloadContinentAirportsBtn.setEnabled(false);
//                } else {
//                    selectedCounry = null;
//                    downloadCountryAirportsBtn.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                selectedCounry = null;
//                downloadCountryAirportsBtn.setEnabled(false);
//            }
//        });
//
//        continentSpinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ContinentAdapter adapter1 = (ContinentAdapter) adapterView.getAdapter();
//                if (adapter1 != null) {
//                    selectedContinent = adapter1.GetContinent(i);
//                    Log.i(TAG, "OnItemSelected: " + selectedContinent.name);
//                    downloadContinentAirportsBtn.setEnabled((selectedContinent != null));
//                    downloadCountryAirportsBtn.setEnabled(false);
//                } else {
//                    selectedContinent = null;
//                    downloadContinentAirportsBtn.setEnabled(false);
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                selectedContinent = null;
//                downloadContinentAirportsBtn.setEnabled(false);
//            }
//        });
//    }

    public boolean loadAds()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open();
        boolean v = propertiesDataSource.checkNoAdvertisements();
        propertiesDataSource.close();

        if (!v) {
            AdView leftAd = (AdView) findViewById(R.id.adLeft);
            AdRequest leftRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
                    .build();
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
                    .build();
            rightAd.loadAd(rightRequest);
            return true;
        }
        else
        {
            removeAdds();
            return false;
        }

    }


//    public void SetButtonState(Boolean Enabled)
//    {
//        if (!Enabled)
//        {
//            downloadContinentAirportsBtn.setEnabled(Enabled);
//            downloadCountryAirportsBtn.setEnabled(Enabled);
//        }
//        startNavigationBtn.setEnabled(Enabled);
//        if (Enabled) GetCounts();
//    }

//    private void SetupLoadButtons()
//    {
//        downloadContinentAirportsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (selectedContinent != null)
//                {
//                    SetButtonState(false);
//                    airportLoadProgress.setVisibility(View.VISIBLE);
//                    airportLoadProgressText.setVisibility(View.VISIBLE);
//                    LoadAirportsByContinentCode(selectedContinent.code, getBaseContext());
//                }
//            }
//        });
//
//        downloadCountryAirportsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (selectedCounry != null)
//                {
//                    SetButtonState(false);
//                    airportLoadProgress.setVisibility(View.VISIBLE);
//                    airportLoadProgressText.setVisibility(View.VISIBLE);
//                    LoadAirportsByCountryCode(selectedCounry.code, getBaseContext());
//                }
//            }
//        });
//    }

//    private void LoadAirportsByCountryCode(String countryCode, Context context)
//    {
//        AsyncXMLAirports loadAirports = new AsyncXMLAirports();
//        loadAirports.CountryCode = countryCode;
//        loadAirports.ContinentCode = "";
//        loadAirports.context = this;
//        loadAirports.startActivity = this;
//        loadAirports.progressBar = airportLoadProgress;
//        loadAirports.progressText = airportLoadProgressText;
//        loadAirports.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    private void LoadAirportsByContinentCode(String code, Context context)
//    {
//        AsyncXMLAirports loadAirports = new AsyncXMLAirports();
//        loadAirports.CountryCode = "";
//        loadAirports.ContinentCode = code;
//        loadAirports.context = this;
//        loadAirports.startActivity = this;
//        loadAirports.progressBar = airportLoadProgress;
//        loadAirports.progressText = airportLoadProgressText;
//        loadAirports.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    private void setupIpAddressEdit()
//    {
//        InputFilter[] filters = new InputFilter[1];
//        filters[0] = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start,
//                                       int end, Spanned dest, int dstart, int dend) {
//                if (end > start) {
//                    String destTxt = dest.toString();
//                    String resultingTxt = destTxt.substring(0, dstart) +
//                            source.subSequence(start, end) +
//                            destTxt.substring(dend);
//                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
//                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
//                        return "";
//                    } else {
//                        String[] splits = resultingTxt.split("\\.");
//                        for (int i=0; i<splits.length; i++) {
//                            if (Integer.valueOf(splits[i]) > 255) {
//                                return "";
//                            }
//                        }
//                    }
//                }
//                return null;
//            }
//        };
//        ipAddressTxt.setFilters(filters);
//    }


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
