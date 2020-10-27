package dod.app.beaconscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener, BeaconConsumer {

    protected static final String TAG1 = "::MonitoringActivity::";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this, 1);

        BluetoothAdapter   mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent;

        if (!mBluetoothAdapter.isEnabled()) {
            intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1: // 25번 줄에서 requestCode 값 1
                if(resultCode==RESULT_OK){
                    // 블루투스 기능을 켰을 때
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Android Activity또는 Service에 바인딩 을 해제합니다 BeaconService.
        beaconManager.unbind(this);
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG1, ":::::최소하나의 비콘 발견하였음:::::");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG1, ":::::더이상 비콘을 찾을 수 없음:::::");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                if(state==0){
                    Log.i(TAG1, ":::::비콘이 보이는 상태이다. state : "+state + ":::::");
                } else {
                    Log.i(TAG1, ":::::비콘이 보이지 않는 상태이다. state : "+state +":::::");
                }
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                List<Beacon> list = (List<Beacon>)beacons;
                if (list.size() > 0) {
                    findViewById(R.id.noneText).setVisibility(View.GONE);
                    findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);

                    BeaconListAdapter listAdapter = new BeaconListAdapter(beacons);
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.setAdapter(listAdapter);
                }else {
                    findViewById(R.id.noneText).setVisibility(View.VISIBLE);
                    findViewById(R.id.recyclerView).setVisibility(View.GONE);
                }
            }
        });

        try {
            //알려주는 BeaconService전달 일치 비콘을 찾고 시작하는 Region개체를 지역에서 비콘을 볼 수있는 동안 추정 mDistance에있는 모든 초 업데이트를 제공합니다.
            beaconManager.startRangingBeaconsInRegion(new Region("C2:02:DD:00:13:DD", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
