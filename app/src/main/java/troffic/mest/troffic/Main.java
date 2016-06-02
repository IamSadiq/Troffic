package troffic.mest.troffic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main extends AppCompatActivity {

    private String jay_son;

    //final static String url_str = "http://www.qiddis.herobo.com/";
    final static String url_str = "http://10.2.77.47/Troffic/";
    SharedPreferences prefs;
    static SharedPreferences.Editor editor;

    //GCM Troffic App
    final static String API_KEY = "AIzaSyDDA7kNE3w4mq1LZRd6CiMHnCdDm-NxBKw";
    final static String PROJECT_ID = "rock-task-132423";

    //String[] src = {"Adenta", "Nima", "Darkuman", "Ashama", "Botwe", "Madina","Lapaz", "Tesano"};
    //String[] dst = {"Kokompe", "Kokomlele", "Achimota", "Shiyashi", "Circle", "Kasoa","Karneshi", "Osu"};

    Spinner source;
    Spinner destination;
    TextView fare;
    ListView listView;

    ArrayList<String> routes;
    ArrayAdapter route_adapter = null;

    ArrayAdapter<String> source_adapter;
    ArrayAdapter<String> dest_adapter;

    String selected_source;
    String selected_dest;
    String pref_fetch;

    ArrayList<String> source_arr;
    ArrayList<String> dest_arr;
    Bundle fare_bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        fare = (TextView)findViewById(R.id.tv);
        source = (Spinner) findViewById(R.id.source);
        destination = (Spinner) findViewById(R.id.destination);
        fare_bundle = new Bundle();

        source.setSelected(false);
        destination.setSelected(false);

        source_arr = new ArrayList<>();
        dest_arr = new ArrayList<>();
        routes = new ArrayList<>();

        pref_fetch = fetchRoute();

        if (pref_fetch != null){
            try {
                JSONArray json = new JSONArray(pref_fetch);
                for (int i = 0; i < json.length()-1; i++){
                    JSONObject j_json = new JSONObject(json.getString(i));
                    String src = j_json.getString("from_terminal");
                    String dest = j_json.getString("to_terminal");

                   // if (source_arr.get(i) != src && dest_arr.get(i) != dest){
                        source_arr.add(src);
                        dest_arr.add(dest);
                        fare_bundle.putString(src + dest, j_json.getString("fare"));
                    //}
                }
                Log.d("JSON FROM PREF", jay_son);
                Toast.makeText(Main.this, "Array Count: " + json.length(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
                Log.e("ROUTE PARSE ERROR", e.toString());
            }

            // Initialize and set Adapter
            source_adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, source_arr);
            // Initialize and set Adapter
            dest_adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, dest_arr);

            source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dest_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            source.setAdapter(source_adapter);
            destination.setAdapter(dest_adapter);

            source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter, View v,
                                           int position, long id) {
                    // On selecting a spinner item
                    selected_source = adapter.getItemAtPosition(position).toString();
                    source.setSelected(true);
                    goToReviews();
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter, View v,
                                           int position, long id) {
                    // On selecting a spinner item
                    selected_dest = adapter.getItemAtPosition(position).toString();
                    destination.setSelected(true);
                    goToReviews();
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

        }else {
            Toast.makeText(Main.this, "No Route Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        server_req();
    }

    public void server_req(){
        //String returned;
        final Bundle hello_data = new Bundle();
        hello_data.putString("hello", "Hello World!");
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                JSONGetter getter = new JSONGetter();
                jay_son = getter.DoHttpRequest(url_str +"_new_json.php", "POST", hello_data);
                Log.d("JSON ROUTE", jay_son);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (jay_son != null) {
                    storeRouteInPref(jay_son);
                    Log.d("JAY SON", jay_son);
                }
            }
        };
        task.execute();
    }

    public void storeRouteInPref(String json_route){
        editor = prefs.edit();
        editor.putString("route", json_route);
        editor.apply();
    }

    public String fetchRoute(){
        return prefs.getString("route", "");
    }

    public void goToReviews(){
        if (source.isSelected() && destination.isSelected() &&
                !selected_source.equalsIgnoreCase("where i am") && !selected_dest.equalsIgnoreCase("where i'm going")){

            fare.setText("FARE COST: " + fare_bundle.getString(selected_source + selected_dest));
            routes.add(selected_source + " -----> " + selected_dest);

            route_adapter = new ArrayAdapter<String>(
                    Main.this,
                    R.layout.list_view_container,
                    R.id.list_tv,
                    routes);

            listView = (ListView) findViewById(R.id.list_item);
            listView.setAdapter(route_adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(Main.this, reviews.class).putExtra("source_dest","FROM  " + selected_source.toLowerCase() + "  TO  " +
                            selected_dest.toLowerCase()));
                }
            });
        }
    }
}
