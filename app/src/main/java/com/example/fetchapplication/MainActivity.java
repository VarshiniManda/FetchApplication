package com.example.fetchapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetchapplication);

        listView = findViewById(R.id.list_view);

        new FetchData().execute();
    }

    private class FetchData extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> result = new ArrayList<>();
            try {
                URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = bufferedReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);

                    JSONArray jsonArray = new JSONArray(responseStrBuilder.toString());

                    List<JSONObject> filteredList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.optString("name");
                        if (name == null || name.trim().isEmpty() || name.equals("null")) {
                            continue;
                        }
                        filteredList.add(jsonObject);
                    }

                    HashMap<Integer, List<JSONObject>> groupedMap = new HashMap<>();
                    for (JSONObject item : filteredList) {
                        int listId = item.getInt("listId");
                        if (!groupedMap.containsKey(listId)) {
                            groupedMap.put(listId, new ArrayList<>());
                        }
                        groupedMap.get(listId).add(item);
                    }

                    for (int key : groupedMap.keySet()) {
                        List<JSONObject> group = groupedMap.get(key);
                        Collections.sort(group, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject o1, JSONObject o2) {
                                int compareListId = Integer.compare(o1.optInt("listId"), o2.optInt("listId"));
                                if (compareListId != 0) {
                                    return compareListId;
                                }
                                return o1.optString("name").compareTo(o2.optString("name"));
                            }
                        });
                        for (JSONObject jsonObject : group) {
                            int id = jsonObject.getInt("id");
                            String name = jsonObject.getString("name");
                            result.add("ListID: " + key + "\nid: " + id + " listId: " + key + " Name: " + name);
                        }
                    }

                } finally {
                    urlConnection.disconnect();
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            CustomAdapter adapter = new CustomAdapter(MainActivity.this, result);
            listView.setAdapter(adapter);
        }

    }
}
