package eu.pryds.misermeter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.util.Log;

public class Util {
    public static String readStringFromHTTP(String urlString) {
        BufferedReader in;
        try {
            // Create a URL for the desired page
            URL url = new URL(urlString);

            // Read all the text returned by the server
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            if ((str = in.readLine()) != null) {
                return str;
            }
            in.close();
        } catch (Exception e) {
            Log.d("FeedUpdate", "Error: " + e.toString());
        }
        return "0";
    }
}
