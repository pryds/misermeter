package eu.pryds.misermeter;

import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.util.Log;

public class BitcoinAddress implements AddressArrayAdapter.AddressItem {
    private static final int[] COMPATIPLE_CONVERTERS = new int[] {
        BalanceConverter.CONV_BITCOINCHARTS,
        BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER
    };
    
    private String address;
    private double balance; // in BTC
    private String comment;
    private BalanceConverter conv;
    private long lastUpdate;
    
    private static final String CURRENCY_NAME = "BTC";
    
    public BitcoinAddress(String address, String comment, BalanceConverter converter) {
        this.address = address;
        this.balance = 0.0;
        this.comment = comment;
        this.conv = converter;
        this.lastUpdate = 0;
    }
    
    public void updateFromFeed() {
        final long MINIMUM_DELAY_TIME = 10000; // 10 sec
        // blockchain.info says: "Please limit your queries to a maxmimum of 1 every 10 seconds."
        
        long updateTime = System.currentTimeMillis();
        
        if (updateTime > lastUpdate + MINIMUM_DELAY_TIME) {
            String feedUrl = "http://blockchain.info/da/q/addressbalance/" + address;
            String satoshiStr = Util.readStringFromHTTP(feedUrl).trim();
            if (satoshiStr != null) {
                long satoshi = Long.parseLong(satoshiStr);
                Log.d(MainActivity.DEBUG_STR, "Blockchain address result: " + satoshi + " - Address: " + address);
                balance = satoshi * 0.00000001;
                lastUpdate = updateTime;
            }
        }
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
        //TODO: currency
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
}
