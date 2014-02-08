package eu.pryds.misermeter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Currency;

import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.util.Log;
import android.util.SparseArray;

public class BitcoinAddress implements AddressArrayAdapter.AddressItem {
    private static final int[] COMPATIPLE_CONVERTERS = new int[] {
        BalanceConverter.CONV_BITCOINCHARTS
    };
    
    private String address;
    private double balance; // in BTC
    private String comment;
    private BalanceConverter conv;
    
    private static final String CURRENCY_NAME = "BTC";
    
    public BitcoinAddress(String address, String comment, BalanceConverter converter) {
        this.address = address;
        this.balance = 0.0;
        this.comment = comment;
        this.conv = converter;
    }
    
    public void updateFromFeed() {
        String feedUrl = "http://blockchain.info/da/q/addressbalance/";
        long satoshi = Long.parseLong(readStringFromHTTP(feedUrl + address));
        Log.d("FeedUpdate", "Address result: " + satoshi + " - Address: " + address);
        balance = satoshi * 0.00000001;
    }
    
    
    public String getAddress() {
        return address;
    }
    
    public String getShortenedAddress() {
        int shortSize = 15; //in chars
        
        if (shortSize > address.length())
            return address;
        
        return address.substring(0, shortSize) + "...";
    }
    
    public String getBalanceAsString() {
        return String.format(CURRENCY_NAME + " %.8f", balance);
    }
    
    public String getRoundedBalanceAsString() {
        return String.format(CURRENCY_NAME + " %.4f", balance);
    }
    
    public String getComment() {
        return comment;
    }
    
    public String getConvertedBalanceAsString() {
        return String.format("DKK %.2f", conv.convertValue(balance, BalanceConverter.CURRENCY_DKK));
    }
    
    public int getIconRessource() {
        return R.drawable.bitcoin_icon;
    }
    
    public boolean supportsConverter(int converter) {
        for (int i = 0; i < COMPATIPLE_CONVERTERS.length; i++) {
            if (COMPATIPLE_CONVERTERS[i] == converter)
                return true;
        }
        return false;
    }
    
    private String readStringFromHTTP(String urlString) {
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
