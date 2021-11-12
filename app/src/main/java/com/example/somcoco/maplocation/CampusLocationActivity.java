package com.example.somcoco.maplocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.somcoco.R;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.Objects;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class CampusLocationActivity extends AppCompatActivity
        implements TMapView.OnCalloutRightButtonClickCallback,
        TMapGpsManager.onLocationChangedCallback {

    LocationManager locationManager;
    RelativeLayout layout;
    RelativeLayout relativeLayout;
    SpeechRecognizer recognizer;
    EditText searchBox;
    ImageView mic;
    ImageView submit;
    ImageView myLoc;
    ImageView exploreLoc;
    Toolbar toolbar;
    TMapView tMapView;
    TMapGpsManager gpsManager;
    TMapCircle tMapCircle;
    Intent inIntent;
    Dialog dialog;
    double logi;
    double lati;
    String curKeyword;
    final int PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campuslocation);

        inIntent = getIntent();

        relativeLayout = (RelativeLayout) findViewById(R.id.location_parent);
        layout = (RelativeLayout) findViewById(R.id.loc_map);
        searchBox = (EditText) findViewById(R.id.loc_search_keyword);
        submit = (ImageView) findViewById(R.id.loc_search_btn);
        mic = (ImageView) findViewById(R.id.loc_search_mic);
        myLoc = (ImageView) findViewById(R.id.loc_myloc);
        exploreLoc = (ImageView) findViewById(R.id.loc_explore);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkLocationService();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.my_marker);
        bitmap = bitmap.createScaledBitmap(bitmap, 55, 55, true);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);

        toolbar = (Toolbar) findViewById(R.id.loc_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setTitle("캠퍼스 찾기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx1329ea6bfcbf4eccb8beb6ec6c3c74e5");
        tMapView.setIcon(bitmap);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(15);
        tMapView.setSightVisible(true);
        tMapView.setCenterPoint(126.94660656887974,37.21241601317397);
        tMapView.setLocationPoint(126.94660656887974,37.21241601317397);

        gpsManager = new TMapGpsManager(this);
        gpsManager.setMinTime(5);
        gpsManager.setMinDistance(100);
        gpsManager.setProvider(gpsManager.NETWORK_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            requestPermissions(new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 3);
        }
        gpsManager.OpenGps();

        layout.addView(tMapView);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplication().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        ArrayList<String> arrBuilding = new ArrayList<>();

        if (inIntent.getBooleanExtra("check", false)) {
            arrBuilding.clear();
            curKeyword = inIntent.getStringExtra("title");
            arrBuilding.add(curKeyword);
            searchBox.setText(curKeyword);
            searchPOI(arrBuilding);
        }

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case IME_ACTION_SEARCH:
                        if (searchBox.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"검색어를 입력해주세요.",Toast.LENGTH_SHORT).show();
                        } else {
                            arrBuilding.clear();
                            curKeyword = searchBox.getText().toString();
                            arrBuilding.add(curKeyword);
                            searchPOI(arrBuilding);
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"검색어를 입력해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    arrBuilding.clear();
                    curKeyword = searchBox.getText().toString();
                    arrBuilding.add(curKeyword);
                    searchPOI(arrBuilding);
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                recognizer.setRecognitionListener(listener);
                recognizer.startListening(intent);
            }
        });

        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Snackbar snackbar = Snackbar.make(relativeLayout,"원활한 서비스 이용을 위해 위치서비스를 활성화 해주세요.",5000)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            });
                    View snackbarView = snackbar.getView();
                    TextView snackbarText = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    snackbar.setActionTextColor(Color.GREEN);
                    snackbar.show();
                }else{
                    tMapView.setCenterPoint(gpsManager.getLocation().getLongitude(), gpsManager.getLocation().getLatitude());
                    tMapView.setLocationPoint(gpsManager.getLocation().getLongitude(), gpsManager.getLocation().getLatitude());
                }

            }
        });

        exploreLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curKeyword == null){
                    Toast.makeText(getApplicationContext(),"검색된 장소가 없습니다",Toast.LENGTH_SHORT).show();
                }else{
                    arrBuilding.clear();
                    arrBuilding.add(curKeyword);
                    searchPOI(arrBuilding);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchPOI(ArrayList<String> arrPOI){
        final TMapData tMapData = new TMapData();
        final ArrayList<TMapPoint> arrTmapPoint = new ArrayList<>();
        final ArrayList<String> arrTitle = new ArrayList<>();
        final ArrayList<String> arrAddress = new ArrayList<>();

        for (int i = 0; i < arrPOI.size(); i++) {
            tMapData.findTitlePOI(arrPOI.get(i), new TMapData.FindTitlePOIListenerCallback() {
                @Override
                public void onFindTitlePOI(ArrayList<TMapPOIItem> arrayList) {
                    for (int j = 0; j < arrayList.size(); j++){
                        TMapPOIItem tMapPOIItem = arrayList.get(j);
                        arrTmapPoint.add(tMapPOIItem.getPOIPoint());
                        arrTitle.add(tMapPOIItem.getPOIName());
                        arrAddress.add(tMapPOIItem.upperAddrName + " " +
                                tMapPOIItem.middleAddrName + " " + tMapPOIItem.lowerAddrName);
                    }
                    tMapView.setCenterPoint(arrTmapPoint.get(0).getLongitude(),arrTmapPoint.get(0).getLatitude());
                    setMultiMarkers(arrTmapPoint, arrTitle, arrAddress);
                }
            });
        }
    }

    private void setMultiMarkers(ArrayList<TMapPoint> arrTPoint, ArrayList<String> arrTitle, ArrayList<String> arrAddress){
        for (int i = 0; i < arrTPoint.size(); i++){
            Bitmap bitmap = createMarkerIcon(R.drawable.outline_location_on_black_24dp);

            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
            tMapMarkerItem.setIcon(bitmap);

            tMapMarkerItem.setTMapPoint(arrTPoint.get(i));

            tMapView.addMarkerItem("markerItem" + i, tMapMarkerItem);

            setBalloonView(tMapMarkerItem, arrTitle.get(i), arrAddress.get(i));
        }
    }

    private void setBalloonView(TMapMarkerItem marker, String title, String address){
        marker.setCanShowCallout(true);

        if(marker.getCanShowCallout()){
            marker.setCalloutTitle(title);
            marker.setCalloutSubTitle(address);

            Bitmap bitmap = createMarkerIcon(R.drawable.outline_expand_more_black_24dp);
            marker.setCalloutRightButtonImage(bitmap);
        }
    }

    private Bitmap createMarkerIcon(int image) {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),image);
        bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false);

        return bitmap;
    }

    @Override
    public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
        logi = tMapMarkerItem.getTMapPoint().getLongitude();
        lati = tMapMarkerItem.getTMapPoint().getLatitude();

        showDialog();
    }

    public void showDialog(){
        dialog.show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button noBtn = dialog.findViewById(R.id.noBtn);
        Button yesBtn = dialog.findViewById(R.id.yesBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CampusDirectionActivity.class);
                intent.putExtra("logi",logi);
                intent.putExtra("lati",lati);
                startActivity(intent);

                dialog.dismiss();
            }
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                searchBox.setText(matches.get(i));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    @Override
    public void onLocationChange(Location location) {
        //tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        //tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationService();
    }

    public void checkLocationService() {
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Snackbar snackbar = Snackbar.make(relativeLayout,"원활한 서비스 이용을 위해 위치서비스를 활성화 해주세요.",5000)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            View snackbarView = snackbar.getView();
            TextView snackbarText = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();
        }
    }
}
