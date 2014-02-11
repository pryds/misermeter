package eu.pryds.misermeter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.util.Log;

public class Util {
    public static String readStringFromHTTP(String urlString) {
        BufferedReader in;
        StringBuilder str = new StringBuilder();
        try {
            // Create a URL for the desired page
            URL url = new URL(urlString);

            // Read all the text returned by the server
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String readLine;
            while ((readLine = in.readLine()) != null) {
                str.append(readLine).append('\n');
            }
            in.close();
        } catch (Exception e) {
            Log.d(MainActivity.DEBUG_STR, "Error: " + e.toString());
        } //TODO: Finally: close reader
        return str.toString();
    }
}
