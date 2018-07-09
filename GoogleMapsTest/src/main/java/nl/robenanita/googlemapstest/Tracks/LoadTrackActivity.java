package nl.robenanita.googlemapstest.Tracks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;

public class LoadTrackActivity extends AppCompatActivity {
    private Integer track_id;
    private static final String TAG = "GooglemapsTest";
    private Button loadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_track);

        track_id = -1;

        loadButton = (Button) findViewById(R.id.trackLoadBtn);
        loadButton.setEnabled(false);
        Button cancelButton = (Button) findViewById(R.id.trackCancelBtn);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (track_id>-1) {
                    Intent intent = new Intent();
                    intent.putExtra("track_id", track_id);
                    setResult(501, intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra("track_id", track_id);
                    setResult(502, intent);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(502);
                finish();
            }
        });

        Loadtracks();
    }

    private void Loadtracks()
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(this);
        locationTrackingDataSource.open();
        ArrayList<LocationTrackingDataSource.Track> tracks = locationTrackingDataSource.getTracks();
        locationTrackingDataSource.close();

        ListView trackList = (ListView) findViewById(R.id.tracksList);
        final TrackAdapter adapter = new TrackAdapter(tracks);
        trackList.setAdapter(adapter);


        trackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TrackAdapter adapter1 = (TrackAdapter) adapterView.getAdapter();
                LocationTrackingDataSource.Track t = adapter1.GetTracks(i);
                adapter1.setSelectedIndex(i);
                adapter1.notifyDataSetChanged();
                track_id = t.id;
                Log.i(TAG, "Selected track: " + track_id.toString());
                loadButton.setEnabled(true);
            }
        });

    }

}
