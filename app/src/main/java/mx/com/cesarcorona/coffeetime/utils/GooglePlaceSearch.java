package mx.com.cesarcorona.coffeetime.utils;

import android.content.Context;
import android.location.Location;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mx.com.cesarcorona.coffeetime.pojo.GPlace;

/**
 * Created by ccabrera on 26/10/17.
 */

public class GooglePlaceSearch {



    private String apiKey;
    private GooglePlaceSearchInterface googlePlaceSearchInterface;
    private Location location;
    private Context context;





    public interface GooglePlaceSearchInterface{
       void OnPlacesFound(List<GPlace> places);
       void OnError(String message);
    }


    public GooglePlaceSearch(Context context,String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }


    public void placesByTextSearch(String query){
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
        StringBuilder stringBuilder = new StringBuilder(url);
        String queryEcoded = "";
        try {
            queryEcoded = URLEncoder.encode(query,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append(queryEcoded).append("&");
        stringBuilder.append("location=").append(location.getLatitude()).append(",").append(location.getLongitude());
        stringBuilder.append("&").append("radius=").append(10000).append("&key=").append(this.apiKey);
        executeSearch(stringBuilder.toString());
    }


    public void setGooglePlaceSearchInterface(GooglePlaceSearchInterface googlePlaceSearchInterface) {
        this.googlePlaceSearchInterface = googlePlaceSearchInterface;
    }


    private void executeSearch(String url){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObjectResponse ;
                        String arrayResults = "";
                        try {
                            jsonObjectResponse = new JSONObject(response);
                            arrayResults = jsonObjectResponse.getString("results");
                            Type listType = new TypeToken<ArrayList<GPlace>>(){}.getType();

                            List<GPlace> yourClassList = new Gson().fromJson(arrayResults, listType);
                            if(googlePlaceSearchInterface != null){
                                googlePlaceSearchInterface.OnPlacesFound(yourClassList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }


    public void setLocation(Location location) {
        this.location = location;
    }
}
