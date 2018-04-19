package com.viu.vrplayer.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kevinzha on 2017/12/28.
 */

public class StartActivity extends Activity implements View.OnClickListener {
    private Context mContext;
    private String TAG = "StartActivity";
    private TextView mChoseLocalFileBtn;
    private TextView mChoseLocalSDCardBtn;
    private  int REQUEST_CODE = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int MSG_PLAYLIST_SUCC = 1001;
    private static final int MSG_PLAYLIST_FAIL = 1002;
    private MyHandler mHandler;
    private long mStartGetTimeMs;
    private TextView mEmptyView;
    private RelativeLayout mSplashRl;
    private ExpandableListView sampleList;
    private TextView mInputTv;
    private RelativeLayout mInputRl;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_activity);
        mContext = this;
        mHandler = new MyHandler();
        initView();

        SampleListLoader loaderTask = new SampleListLoader();
        loaderTask.execute();
    }

    private void initView(){
        sampleList = (ExpandableListView) findViewById(R.id.sample_list);
        mChoseLocalFileBtn = (TextView)findViewById(R.id.choseLocalBtn);
        mChoseLocalSDCardBtn =(TextView)findViewById(R.id.choseLocalSDCardBtn);
        mInputRl = (RelativeLayout)findViewById(R.id.input_ly);
        mInputTv = (TextView)findViewById(R.id.input_tv);
        mInputTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,InputActivity.class);
                startActivity(intent);
            }
        });
        mChoseLocalFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasPermission = requestPermission(mContext);
                if (!hasPermission) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI  );
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mChoseLocalSDCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasPermission = requestPermission(mContext);
                if (!hasPermission) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mSplashRl = (RelativeLayout) findViewById(R.id.splash_rl);
        mEmptyView = (TextView)findViewById(R.id.empty_tv);
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getPlayList();
            }
        });
    }


    private final class SampleListLoader extends AsyncTask<String, Void, List<SampleGroup>>{
        private boolean sawError;
        @Override
        protected List<SampleGroup> doInBackground(String... uris) {
            List<SampleGroup> result = new ArrayList<>();
            InputStream is = null;
            try {
                is = getAssets().open("playlist.json");
                readSampleGroups(new JsonReader(new InputStreamReader(is, "UTF-8")) , result);
            } catch (Exception e) {
                Log.e(TAG, "Error loading sample list: ", e);
                sawError = true;
            } finally {
                Util.closeQuietly(is);
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<SampleGroup> result) {
            Message msg = null;
            if (sawError){
                msg = mHandler.obtainMessage(MSG_PLAYLIST_FAIL);
            }else {
                msg = mHandler.obtainMessage(MSG_PLAYLIST_SUCC);
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }
    }
    /**
     * 请求权限
     */
    protected boolean requestPermission(Context ct) {
        //api版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限checkCallPhonePermission != PackageManager.PERMISSION_GRANTED
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ct, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"请检查SD读取权限", Toast.LENGTH_SHORT);
                }
            });
            return true;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
         sp = getSharedPreferences(InputActivity.LAST_PLAY_URL , Context.MODE_PRIVATE);
        String lastPlayUrl = sp.getString(InputActivity.KEY_PLAY_URL,"");
        if (!lastPlayUrl.isEmpty()){
            mInputTv.setText(lastPlayUrl);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

        }
    }

    private void onSampleGroups(final List<SampleGroup> groups, boolean sawError) {
        if (sawError) {
            Toast.makeText(getApplicationContext(), R.string.sample_list_load_error, Toast.LENGTH_LONG)
                    .show();
        }
        sampleList.setAdapter(new SampleAdapter(this, groups));
        sampleList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
                                        int childPosition, long id) {
                onSampleSelected(groups.get(groupPosition).samples.get(childPosition));
                return true;
            }
        });
    }

    private void onSampleSelected(Sample sample) {
        if (requestPermission(mContext)) {
            startActivity(sample.buildIntent(this));
        }
    }
    class MyHandler extends Handler {
        MyHandler(){

        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PLAYLIST_FAIL:
                    mChoseLocalSDCardBtn.setVisibility(View.VISIBLE);
                    mChoseLocalFileBtn.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    mSplashRl.setVisibility(View.GONE);
                    sampleList.setVisibility(View.GONE);
                    mInputTv.setVisibility(View.VISIBLE);
                    mInputRl.setVisibility(View.VISIBLE);
                    break;
                case MSG_PLAYLIST_SUCC:;
                    mEmptyView.setVisibility(View.GONE);
                    mSplashRl.setVisibility(View.GONE);
                    sampleList.setVisibility(View.VISIBLE);
                    onSampleGroups((List<SampleGroup>)msg.obj, false);
                    mChoseLocalFileBtn.setVisibility(View.VISIBLE);
                    mChoseLocalSDCardBtn.setVisibility(View.VISIBLE);
                    mInputTv.setVisibility(View.VISIBLE);
                    mInputRl.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

//    private void getPlayList(){
//        final List<SampleGroup> result =  new ArrayList<>();
//        mStartGetTimeMs = System.currentTimeMillis();
//        try {
//            OkHttpClient client = new OkHttpClient();
//            // Create request for remote resource.
//            Request request = new Request.Builder()
//                    .url(SZ_URL_PLAYLIST)
//                    .build();
//            Call call = client.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    mHandler.sendEmptyMessage(MSG_PLAYLIST_FAIL);
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    if (response != null && response.code() == 200) {
//                        InputStream inputStream =   new ByteArrayInputStream(response.body().string().getBytes());
////                        InputStream inputStream = mContext.getResources().openRawResource(R.raw.mediaexolist);
//                        readSampleGroups(new JsonReader(new InputStreamReader(inputStream, "UTF-8")), result);
//                        Util.closeQuietly(inputStream);
//                        Message msg = mHandler.obtainMessage(MSG_PLAYLIST_SUCC);
//                        msg.obj = result;
//                        if ((System.currentTimeMillis() - mStartGetTimeMs )> 800) {
//                            mHandler.sendMessage(msg);
//                        }else{
//                            mHandler.sendMessageDelayed(msg, 800);
//                        }
//                    }else{
//                        mHandler.sendEmptyMessage(MSG_PLAYLIST_FAIL);
//                    }
//                }
//            });
//        }catch (Exception e){
//
//        }
//    }

    private void readSampleGroups(JsonReader reader, List<SampleGroup> groups) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readSampleGroup(reader, groups);
        }
        reader.endArray();
    }

    private void readSampleGroup(JsonReader reader, List<SampleGroup> groups) throws IOException {
        String groupName = "";
        ArrayList<Sample> samples = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    groupName = reader.nextString();
                    break;
                case "samples":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        samples.add(readEntry(reader, false));
                    }
                    reader.endArray();
                    break;
                case "_comment":
                    reader.nextString(); // Ignore.
                    break;
                default:

            }
        }
        reader.endObject();

        SampleGroup group = getGroup(groupName, groups);
        group.samples.addAll(samples);
    }

    private Sample readEntry(JsonReader reader, boolean insidePlaylist) throws IOException {
        String sampleName = null;
        String url = null;
        String extension = null;
        String format = null;
        String description;
        int duration = 0;
        UUID drmUuid = null;
        String drmLicenseUrl = null;
        String[] drmKeyRequestProperties = null;
        boolean preferExtensionDecoders = false;
        ArrayList<UriSample> playlistSamples = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    sampleName = reader.nextString();
                    break;
                case "uri":
                    url = reader.nextString();
                    break;
                case "extension":
                    extension = reader.nextString();
                    break;
                case "prefer_extension_decoders":
                    preferExtensionDecoders = reader.nextBoolean();
                    break;
                case "description":
                    description = reader.nextString();
                    break;
                case "thumbnail":
                    reader.nextString();
                    break;
                case "playlist":
                    playlistSamples = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        playlistSamples.add((UriSample) readEntry(reader, true));
                    }
                    reader.endArray();
                    break;
                case "format":
                    format = reader.nextString();
                    break;
                case "duration":
                    duration = reader.nextInt();
                    break;
                default:
                    break;
            }
        }
        reader.endObject();

        if (playlistSamples != null) {
            UriSample[] playlistSamplesArray = playlistSamples.toArray(
                    new UriSample[playlistSamples.size()]);
            return new PlaylistSample(sampleName, drmUuid, drmLicenseUrl, drmKeyRequestProperties,
                    preferExtensionDecoders, playlistSamplesArray);
        } else {
            return new UriSample(sampleName, drmUuid, drmLicenseUrl, drmKeyRequestProperties,
                    preferExtensionDecoders, url, extension,format,duration);
        }
    }

    private SampleGroup getGroup(String groupName, List<SampleGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (Util.areEqual(groupName, groups.get(i).title)) {
                return groups.get(i);
            }
        }
        SampleGroup group = new SampleGroup(groupName);
        groups.add(group);
        return group;
    }

    private static final class SampleAdapter extends BaseExpandableListAdapter {

        private final Context context;
        private final List<SampleGroup> sampleGroups;

        public SampleAdapter(Context context, List<SampleGroup> sampleGroups) {
            this.context = context;
            this.sampleGroups = sampleGroups;
        }

        @Override
        public Sample getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).samples.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent,
                        false);
            }
            ((TextView) view).setText(getChild(groupPosition, childPosition).name);
            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getGroup(groupPosition).samples.size();
        }

        @Override
        public SampleGroup getGroup(int groupPosition) {
            return sampleGroups.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1,
                        parent, false);
            }
            ((TextView) view).setText(getGroup(groupPosition).title);
            return view;
        }

        @Override
        public int getGroupCount() {
            return this.sampleGroups.size();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    private static final class SampleGroup {

        public final String title;
        public final List<Sample> samples;

        public SampleGroup(String title) {
            this.title = title;
            this.samples = new ArrayList<>();
        }

    }

    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, VRPlayerActivity.class);
//            intent.putExtra(LivePlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
//            if (drmSchemeUuid != null) {
//                intent.putExtra(LivePlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
//                intent.putExtra(LivePlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
//                intent.putExtra(LivePlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
//            }
            return intent;
        }

    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String extension;
        public final String format;
        public final int duration;
        public UriSample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
                         String extension, String format, int duration) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
            this.format = format;
            this.duration = duration;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra(VRPlayerActivity.EXTENSION_EXTRA, extension)
                    .putExtra(VRPlayerActivity.URI_LIST_EXTRA,uri);
//                    .putExtra(PlayerActivity.FORMAT_LIST_EXTRA,format)
//                    .putExtra(PlayerActivity.DURATION_LIST_EXTRA,duration)
//                    .setAction(PlayerActivity.ACTION_VIEW);
        }

    }

    private static final class PlaylistSample extends Sample {

        public final UriSample[] children;

        public PlaylistSample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                              String[] drmKeyRequestProperties, boolean preferExtensionDecoders,
                              UriSample... children) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.children = children;
        }

        @Override
        public Intent buildIntent(Context context) {
            String[] uris = new String[children.length];
            String[] extensions = new String[children.length];
            for (int i = 0; i < children.length; i++) {
                uris[i] = children[i].uri;
                extensions[i] = children[i].extension;
            }
            return super.buildIntent(context)
                    .putExtra(VRPlayerActivity.URI_LIST_EXTRA, uris)
                    .putExtra(VRPlayerActivity.EXTENSION_LIST_EXTRA, extensions);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String url = cursor.getString(columnIndex);
            cursor.close();
            if (url != null ){
                Intent intent = new Intent(mContext,VRPlayerActivity.class);
                intent.putExtra(VRPlayerActivity.URI_LIST_EXTRA,url);
                startActivity(intent);
            }
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }
}
