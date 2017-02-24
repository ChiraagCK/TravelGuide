package com.wipro.chiraag.mobiletravelguide;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoShow extends AppCompatActivity {

    public ListView myListview;
    public ArrayList<HashMap<String,String>> myThumbnails = new ArrayList<>();
    private ArrayList<MediaStore.Video> videoArrayList = new ArrayList<MediaStore.Video>();
    public ArrayList<String> Links = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    private ProgressDialog dlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_video_show);

        myListview = (ListView) findViewById(R.id.myListView);
        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String myUrl = myThumbnails.get(position).get("My_Link");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)));

            }
        });

        if (isNetworkAvailable())
        {

            dlg = ProgressDialog.show(this, "Preparing Video List", "please wait");
            PlaceTask task = new PlaceTask();

            StringBuilder sb = new StringBuilder();

            String query1 = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+ LocationAddress.ADDRESS;
            String query2 = "&type=video&key=AIzaSyBrI-LtpV7HCUsrzTJlBugQVJczg3-nGxc";
            String queryf = query1+query2;
            String val = String.valueOf(sb.append(queryf));

            task.execute(val);
            Log.w("val.........", val);
        }

        else
        {
            Toast.makeText(getBaseContext(), "Not connected to network", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager cmanager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo nInfo = cmanager.getActiveNetworkInfo();

        if (nInfo != null && nInfo.isConnected())
            return true;
        else
        {}
        return false;
    }


    private class PlaceTask extends AsyncTask<String, Void, JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... strings)
        {
            JSONObject result = null;
            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String response = "";

                String line = reader.readLine();
                while (line != null) {
                    response += line;
                    line = reader.readLine();
                }

                result = new JSONObject(response);
            }

            catch (Exception e)
            {
                Log.e("Placetask", "ERROR" + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            dlg.dismiss();
            //parse json response; update listview via adapter
            if (jsonObject != null)
            {
                Toast.makeText(getBaseContext(),"HERE",Toast.LENGTH_LONG).show();
                try
                {
                    JSONArray array = jsonObject.getJSONArray("items");
                    Log.w("JSONArray",""+array);
                    Log.w("JSONArrayLength",""+array.length());
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i).getJSONObject("id");
                        HashMap<String,String> thumbs = new HashMap<>();
                        thumbs.put("My_Link","https://www.youtube.com/watch?v="+object.getString("videoId"));
                        thumbs.put("My_Title", array.getJSONObject(i).getJSONObject("snippet").getString("title"));
                       Log.w("Title",array.getJSONObject(i).getJSONObject("snippet").getString("title"));
                        myThumbnails.add(thumbs);
                    }

                    String src[]= new String[]{"My_Link","My_Title"};
                    int dest[]= new int[]{R.id.Thumbnail,R.id.FilePath};
                    simpleAdapter = new SimpleAdapter(getBaseContext(),myThumbnails,R.layout.row,src,dest);

                    myListview.setAdapter(simpleAdapter);

                    Toast.makeText(getBaseContext(),"Coming Here",Toast.LENGTH_LONG).show();
                    for(int i=0;i<Links.size();i++)
                    {
                        Log.w("Link",""+Links.get(i));
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(VideoShow.this, "Parsing Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            else
            {

                Toast.makeText(VideoShow.this, "no result", Toast.LENGTH_LONG).show();
            }
        }
    }
}
