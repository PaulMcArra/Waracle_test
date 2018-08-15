package com.waracle.androidtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";
    private static List<Cake> CAKE_LIST = new ArrayList<>();
    private static CakeAdapter CAKE_ADAPTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            CAKE_ADAPTER = new CakeAdapter(getApplicationContext(), CAKE_LIST);
            new LoadData().execute(JSON_URL);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment is responsible for loading in some JSON and
     * then displaying a list of cakes with images.
     * Fix any crashes
     * Improve any performance issues
     * Use good coding practices to make code more secure
     */

    public static class PlaceholderFragment extends ListFragment {

        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        private ListView cakeList;

        public PlaceholderFragment() { /**/ }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            cakeList = (ListView) rootView.findViewById(R.id.list);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setListAdapter(CAKE_ADAPTER);
        }
    }


    public class LoadData extends AsyncTask<String, String, List<Cake>> {
        @Override
        protected List<Cake> doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            List<Cake> data = new ArrayList<>();

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                String responseString = readStream(urlConnection.getInputStream());
                data = parseData(responseString);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return data;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        private List<Cake> parseData(String jString) {

            List<Cake> dataList = new ArrayList<>();
            try {
                JSONArray jArray = new JSONArray(jString);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        String title = jArray.getJSONObject(i).getString("title");
                        String desc = jArray.getJSONObject(i).getString("desc");
                        String image = jArray.getJSONObject(i).getString("image");
                        Bitmap bmp = null;
                        try {
                            InputStream in = new java.net.URL(image).openStream();
                            bmp = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                        Cake cake = new Cake(title, desc, image, bmp);
                        dataList.add(cake);
                    }
                }

            } catch (JSONException e) {
                Log.e("CatalogClient", "unexpected JSON exception", e);
            }

            return dataList;
        }

        protected void onPostExecute(List<Cake> cakes) {
            super.onPostExecute(cakes);
            for (Cake cake : cakes){
                CAKE_LIST.add(cake);
            }
            CAKE_ADAPTER.notifyDataSetChanged();
        }

        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Loading CAKES...", Toast.LENGTH_LONG).show();
        }
    }

    public static class CakeAdapter extends BaseAdapter
    {
        List<Cake> cakes;
        LayoutInflater inflater;

        public CakeAdapter(Context context, List<Cake> cList)
        {
            inflater = LayoutInflater.from(context);
            this.cakes = cList;
        }

        @Override
        public int getCount()
        {
            return cakes.size();
        }

        @Override
        public Object getItem(int position)
        {
            return cakes.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        private class ViewHolder
        {
            TextView title;
            TextView description;
            ImageView image;
        }
        @Override

        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_layout, null);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.description = (TextView)convertView.findViewById(R.id.desc);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            }

            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(cakes.get(position).getName());
            holder.description.setText(cakes.get(position).getDescription());
            holder.image.setImageBitmap(cakes.get(position).getBmImage());

            return convertView;
        }
    }
}

