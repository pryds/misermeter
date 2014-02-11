package eu.pryds.misermeter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.SparseArray;

public class BitcoinaverageGlobaltickerConverter implements AddressArrayAdapter.BalanceConverter {
    SparseArray<CurrencyRate> rates; // indices are BalanceConverter.CURRENCY_XXX
    long lastUpdate;
    
    public BitcoinaverageGlobaltickerConverter() {
        rates = new SparseArray<CurrencyRate>();
        setupSupportedCurrencies();
        lastUpdate = 0;
    }
    
    public double convertValue(double fromValue, int toCurrency) {
        CurrencyRate r = rates.get(toCurrency);
        if (r == null)
            return 0.0;
        return fromValue * r.rate;
    }

    public void updateFromFeed() {
        /*
         *  bitcoinaverage says: "There is no explicit restriction on how often
         *  you can call the API, however calling it more than once a minute
         *  makes no sense. Please be good."
         */
        
        //Check that we haven't polled within the last minute. If so, break!
        final long MINIMUM_DELAY_TIME = 60000; // 60 sec
        long updateTime = System.currentTimeMillis();
        if (updateTime < lastUpdate + MINIMUM_DELAY_TIME) {
            return;
        }
        
        // Poll JSON feed:
        String feedUrl = "https://api.bitcoinaverage.com/ticker/global/all";
        String jsonStr = Util.readStringFromHTTP(feedUrl);
        
        // Parse JSON data:
        JSONParser parser = new JSONParser();
        HashMap<String, FeedCurrencyInfo> parsedData = null;
        try {
            parsedData = parser.readJsonStream(jsonStr);
        } catch (IOException e) {
            Log.e(MainActivity.DEBUG_STR, "Problem reading json: " + e);
        }
        Log.d(MainActivity.DEBUG_STR, parsedData.size() + " entries parsed from JSON data.");
        
        // Put parsed data into rates array:
        if (parsedData != null) {
            //rates.clear();
            Iterator<Entry<String, FeedCurrencyInfo>> it = parsedData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, FeedCurrencyInfo> pair = (Map.Entry<String, FeedCurrencyInfo>)it.next();
                String name = pair.getKey();
                FeedCurrencyInfo info = pair.getValue();
                int index = findCurrencyIndex(name);
                
                if (index != -1) {
                    double rate = (info.ask + info.bid) / 2.0;
                    rates.put(index, new CurrencyRate(name, rate));
                    Log.d(MainActivity.DEBUG_STR, "Currency: " + name + " - Ask: " + info.ask + " - Bid: " + info.bid + " - Rate: " + rate);
                }
                it.remove();
            }
            Log.d(MainActivity.DEBUG_STR, rates.size() + " entries added to rates.");
        }
    }
    
    private int findCurrencyIndex(String name) {
        int key = 0;
        for(int i = 0; i < rates.size(); i++) {
           key = rates.keyAt(i);
           if (name.equals(rates.get(key).name))
               return key;
        }
        return -1;
    }
    
    //private static final String jsonStr = "{\n  \"AED\": {\n    \"ask\": 2564.82,\n    \"bid\": 2551.61,\n    \"last\": 2553.14,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"AFN\": {\n    \"ask\": 39604.16,\n    \"bid\": 39400.28,\n    \"last\": 39423.87,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ALL\": {\n    \"ask\": 72037.35,\n    \"bid\": 71666.51,\n    \"last\": 71709.42,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"AMD\": {\n    \"ask\": 289058.14,\n    \"bid\": 287570.08,\n    \"last\": 287742.27,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ANG\": {\n    \"ask\": 1249.18,\n    \"bid\": 1242.75,\n    \"last\": 1243.49,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"AOA\": {\n    \"ask\": 68156.73,\n    \"bid\": 67805.86,\n    \"last\": 67846.46,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ARS\": {\n    \"ask\": 5475.66,\n    \"bid\": 5447.47,\n    \"last\": 5450.73,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"AUD\": {\n    \"24h_avg\": 770.94,\n    \"ask\": 779.5,\n    \"bid\": 775.48,\n    \"last\": 775.95,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 277.82,\n    \"volume_percent\": 0.34\n  },\n  \"AWG\": {\n    \"ask\": 1249.87,\n    \"bid\": 1243.43,\n    \"last\": 1244.18,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"AZN\": {\n    \"ask\": 547.54,\n    \"bid\": 544.72,\n    \"last\": 545.04,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BAM\": {\n    \"ask\": 1002.03,\n    \"bid\": 996.87,\n    \"last\": 997.47,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BBD\": {\n    \"ask\": 1396.54,\n    \"bid\": 1389.35,\n    \"last\": 1390.18,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BDT\": {\n    \"ask\": 54109.91,\n    \"bid\": 53831.36,\n    \"last\": 53863.59,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BGN\": {\n    \"ask\": 1003.18,\n    \"bid\": 998.01,\n    \"last\": 998.61,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BHD\": {\n    \"ask\": 263.18,\n    \"bid\": 261.83,\n    \"last\": 261.98,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BIF\": {\n    \"ask\": 1082978.56,\n    \"bid\": 1077403.44,\n    \"last\": 1078048.57,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BMD\": {\n    \"ask\": 698.27,\n    \"bid\": 694.68,\n    \"last\": 695.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BND\": {\n    \"ask\": 884.16,\n    \"bid\": 879.61,\n    \"last\": 880.13,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BOB\": {\n    \"ask\": 4822.66,\n    \"bid\": 4797.83,\n    \"last\": 4800.7,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BRL\": {\n    \"24h_avg\": 1643.58,\n    \"ask\": 1661.61,\n    \"bid\": 1653.05,\n    \"last\": 1654.04,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 10.96,\n    \"volume_percent\": 0.01\n  },\n  \"BSD\": {\n    \"ask\": 698.27,\n    \"bid\": 694.68,\n    \"last\": 695.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BTC\": {\n    \"ask\": 1.0,\n    \"bid\": 1.0,\n    \"last\": 1.0,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BTN\": {\n    \"ask\": 43520.34,\n    \"bid\": 43296.3,\n    \"last\": 43322.23,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BWP\": {\n    \"ask\": 6298.67,\n    \"bid\": 6266.24,\n    \"last\": 6269.99,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BYR\": {\n    \"ask\": 6750783.47,\n    \"bid\": 6716030.8,\n    \"last\": 6720052.23,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"BZD\": {\n    \"ask\": 1389.66,\n    \"bid\": 1382.5,\n    \"last\": 1383.33,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CAD\": {\n    \"24h_avg\": 762.03,\n    \"ask\": 770.5,\n    \"bid\": 766.54,\n    \"last\": 766.99,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 563.21,\n    \"volume_percent\": 0.7\n  },\n  \"CDF\": {\n    \"ask\": 643931.06,\n    \"bid\": 640616.14,\n    \"last\": 640999.73,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CHF\": {\n    \"24h_avg\": 620.29,\n    \"ask\": 627.21,\n    \"bid\": 623.98,\n    \"last\": 624.36,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 72.47,\n    \"volume_percent\": 0.09\n  },\n  \"CLF\": {\n    \"ask\": 16.48,\n    \"bid\": 16.39,\n    \"last\": 16.4,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CLP\": {\n    \"ask\": 386704.98,\n    \"bid\": 384714.25,\n    \"last\": 384944.61,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CNY\": {\n    \"24h_avg\": 4193.23,\n    \"ask\": 4239.65,\n    \"bid\": 4217.83,\n    \"last\": 4220.35,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 7201.55,\n    \"volume_percent\": 8.89\n  },\n  \"COP\": {\n    \"ask\": 1430137.07,\n    \"bid\": 1422774.8,\n    \"last\": 1423626.73,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CRC\": {\n    \"ask\": 358905.43,\n    \"bid\": 357057.81,\n    \"last\": 357271.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CUP\": {\n    \"ask\": 15841.95,\n    \"bid\": 15760.4,\n    \"last\": 15769.84,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CVE\": {\n    \"ask\": 56250.53,\n    \"bid\": 55960.96,\n    \"last\": 55994.47,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"CZK\": {\n    \"ask\": 14102.12,\n    \"bid\": 14029.53,\n    \"last\": 14037.93,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"DJF\": {\n    \"ask\": 124023.31,\n    \"bid\": 123384.84,\n    \"last\": 123458.73,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"DKK\": {\n    \"ask\": 3821.68,\n    \"bid\": 3802.01,\n    \"last\": 3804.28,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"DOP\": {\n    \"ask\": 29992.7,\n    \"bid\": 29838.3,\n    \"last\": 29856.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"DZD\": {\n    \"ask\": 54714.28,\n    \"bid\": 54432.62,\n    \"last\": 54465.21,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"EEK\": {\n    \"ask\": 8148.83,\n    \"bid\": 8106.88,\n    \"last\": 8111.74,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"EGP\": {\n    \"ask\": 4860.35,\n    \"bid\": 4835.33,\n    \"last\": 4838.22,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ERN\": {\n    \"ask\": 10542.37,\n    \"bid\": 10488.09,\n    \"last\": 10494.37,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ETB\": {\n    \"ask\": 13419.11,\n    \"bid\": 13350.03,\n    \"last\": 13358.02,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"EUR\": {\n    \"24h_avg\": 506.51,\n    \"ask\": 512.2,\n    \"bid\": 509.56,\n    \"last\": 509.87,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 4557.62,\n    \"volume_percent\": 5.63\n  },\n  \"FJD\": {\n    \"ask\": 1313.02,\n    \"bid\": 1306.26,\n    \"last\": 1307.04,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"FKP\": {\n    \"ask\": 425.53,\n    \"bid\": 423.34,\n    \"last\": 423.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GBP\": {\n    \"24h_avg\": 420.86,\n    \"ask\": 425.53,\n    \"bid\": 423.34,\n    \"last\": 423.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 524.82,\n    \"volume_percent\": 0.65\n  },\n  \"GEL\": {\n    \"ask\": 1225.64,\n    \"bid\": 1219.33,\n    \"last\": 1220.06,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GHS\": {\n    \"ask\": 1718.99,\n    \"bid\": 1710.14,\n    \"last\": 1711.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GIP\": {\n    \"ask\": 425.53,\n    \"bid\": 423.34,\n    \"last\": 423.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GMD\": {\n    \"ask\": 26659.97,\n    \"bid\": 26522.72,\n    \"last\": 26538.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GNF\": {\n    \"ask\": 4900985.86,\n    \"bid\": 4875755.85,\n    \"last\": 4878675.36,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GTQ\": {\n    \"ask\": 5438.73,\n    \"bid\": 5410.73,\n    \"last\": 5413.97,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"GYD\": {\n    \"ask\": 144822.17,\n    \"bid\": 144076.63,\n    \"last\": 144162.9,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"HKD\": {\n    \"ask\": 5417.57,\n    \"bid\": 5389.68,\n    \"last\": 5392.9,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"HNL\": {\n    \"ask\": 13847.71,\n    \"bid\": 13776.42,\n    \"last\": 13784.67,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"HRK\": {\n    \"ask\": 3916.83,\n    \"bid\": 3896.67,\n    \"last\": 3899.0,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"HTG\": {\n    \"ask\": 28244.25,\n    \"bid\": 28098.86,\n    \"last\": 28115.68,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"HUF\": {\n    \"ask\": 157628.69,\n    \"bid\": 156817.23,\n    \"last\": 156911.13,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"IDR\": {\n    \"ask\": 8456753.65,\n    \"bid\": 8413218.74,\n    \"last\": 8418256.42,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ILS\": {\n    \"24h_avg\": 2434.94,\n    \"ask\": 2461.86,\n    \"bid\": 2449.19,\n    \"last\": 2450.66,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 19.27,\n    \"volume_percent\": 0.02\n  },\n  \"INR\": {\n    \"ask\": 43393.29,\n    \"bid\": 43169.91,\n    \"last\": 43195.76,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"IQD\": {\n    \"ask\": 810838.68,\n    \"bid\": 806664.52,\n    \"last\": 807147.54,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"IRR\": {\n    \"ask\": 17330608.44,\n    \"bid\": 17241391.41,\n    \"last\": 17251715.23,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ISK\": {\n    \"ask\": 80588.79,\n    \"bid\": 80173.92,\n    \"last\": 80221.93,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"JEP\": {\n    \"ask\": 425.53,\n    \"bid\": 423.34,\n    \"last\": 423.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"JMD\": {\n    \"ask\": 74810.6,\n    \"bid\": 74425.48,\n    \"last\": 74470.05,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"JOD\": {\n    \"ask\": 492.38,\n    \"bid\": 489.84,\n    \"last\": 490.14,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"JPY\": {\n    \"24h_avg\": 70670.35,\n    \"ask\": 71478.81,\n    \"bid\": 71110.84,\n    \"last\": 71153.42,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 1351.5,\n    \"volume_percent\": 1.67\n  },\n  \"KES\": {\n    \"ask\": 60058.04,\n    \"bid\": 59748.87,\n    \"last\": 59784.64,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KGS\": {\n    \"ask\": 35614.31,\n    \"bid\": 35430.97,\n    \"last\": 35452.18,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KHR\": {\n    \"ask\": 2782960.96,\n    \"bid\": 2768634.43,\n    \"last\": 2770292.23,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KMF\": {\n    \"ask\": 252017.31,\n    \"bid\": 250719.93,\n    \"last\": 250870.06,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KPW\": {\n    \"ask\": 628443.42,\n    \"bid\": 625208.23,\n    \"last\": 625582.59,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KRW\": {\n    \"ask\": 750375.4,\n    \"bid\": 746512.51,\n    \"last\": 746959.51,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KWD\": {\n    \"ask\": 197.62,\n    \"bid\": 196.6,\n    \"last\": 196.72,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KYD\": {\n    \"ask\": 575.79,\n    \"bid\": 572.82,\n    \"last\": 573.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"KZT\": {\n    \"ask\": 108491.26,\n    \"bid\": 107932.75,\n    \"last\": 107997.38,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LAK\": {\n    \"ask\": 5594570.88,\n    \"bid\": 5565770.34,\n    \"last\": 5569103.02,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LBP\": {\n    \"ask\": 1048860.43,\n    \"bid\": 1043460.96,\n    \"last\": 1044085.76,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LKR\": {\n    \"ask\": 91300.96,\n    \"bid\": 90830.95,\n    \"last\": 90885.33,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LRD\": {\n    \"ask\": 59702.13,\n    \"bid\": 59394.78,\n    \"last\": 59430.35,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LSL\": {\n    \"ask\": 7719.1,\n    \"bid\": 7679.36,\n    \"last\": 7683.96,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LTL\": {\n    \"ask\": 1768.48,\n    \"bid\": 1759.37,\n    \"last\": 1760.43,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LVL\": {\n    \"ask\": 358.93,\n    \"bid\": 357.08,\n    \"last\": 357.29,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"LYD\": {\n    \"ask\": 873.06,\n    \"bid\": 868.57,\n    \"last\": 869.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MAD\": {\n    \"ask\": 5750.36,\n    \"bid\": 5720.76,\n    \"last\": 5724.18,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MDL\": {\n    \"ask\": 9381.98,\n    \"bid\": 9333.68,\n    \"last\": 9339.27,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MGA\": {\n    \"ask\": 1615113.56,\n    \"bid\": 1606799.04,\n    \"last\": 1607761.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MKD\": {\n    \"ask\": 31737.27,\n    \"bid\": 31573.88,\n    \"last\": 31592.79,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MMK\": {\n    \"ask\": 686046.56,\n    \"bid\": 682514.83,\n    \"last\": 682923.51,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MNT\": {\n    \"ask\": 1201723.48,\n    \"bid\": 1195537.07,\n    \"last\": 1196252.94,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MOP\": {\n    \"ask\": 5569.22,\n    \"bid\": 5540.55,\n    \"last\": 5543.87,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MRO\": {\n    \"ask\": 201929.9,\n    \"bid\": 200890.38,\n    \"last\": 201010.67,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MTL\": {\n    \"ask\": 477.34,\n    \"bid\": 474.88,\n    \"last\": 475.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MUR\": {\n    \"ask\": 21131.92,\n    \"bid\": 21023.13,\n    \"last\": 21035.72,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MVR\": {\n    \"ask\": 10757.55,\n    \"bid\": 10702.18,\n    \"last\": 10708.58,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MWK\": {\n    \"ask\": 296694.29,\n    \"bid\": 295166.92,\n    \"last\": 295343.66,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MXN\": {\n    \"ask\": 9274.16,\n    \"bid\": 9226.42,\n    \"last\": 9231.94,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MYR\": {\n    \"ask\": 2323.26,\n    \"bid\": 2311.3,\n    \"last\": 2312.68,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"MZN\": {\n    \"ask\": 21999.01,\n    \"bid\": 21885.76,\n    \"last\": 21898.87,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"NAD\": {\n    \"ask\": 7718.19,\n    \"bid\": 7678.46,\n    \"last\": 7683.06,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"NGN\": {\n    \"ask\": 113873.95,\n    \"bid\": 113287.73,\n    \"last\": 113355.57,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"NIO\": {\n    \"ask\": 17769.89,\n    \"bid\": 17678.42,\n    \"last\": 17689.0,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"NOK\": {\n    \"24h_avg\": 4266.46,\n    \"ask\": 4312.86,\n    \"bid\": 4290.66,\n    \"last\": 4293.23,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 38.5,\n    \"volume_percent\": 0.05\n  },\n  \"NPR\": {\n    \"ask\": 69656.26,\n    \"bid\": 69297.68,\n    \"last\": 69339.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"NZD\": {\n    \"24h_avg\": 832.6,\n    \"ask\": 842.04,\n    \"bid\": 837.71,\n    \"last\": 838.21,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 41.99,\n    \"volume_percent\": 0.05\n  },\n  \"OMR\": {\n    \"ask\": 268.85,\n    \"bid\": 267.47,\n    \"last\": 267.63,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PAB\": {\n    \"ask\": 698.27,\n    \"bid\": 694.68,\n    \"last\": 695.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PEN\": {\n    \"ask\": 1966.59,\n    \"bid\": 1956.47,\n    \"last\": 1957.64,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PGK\": {\n    \"ask\": 1758.89,\n    \"bid\": 1749.83,\n    \"last\": 1750.88,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PHP\": {\n    \"ask\": 31428.55,\n    \"bid\": 31266.75,\n    \"last\": 31285.48,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PKR\": {\n    \"ask\": 73533.05,\n    \"bid\": 73154.5,\n    \"last\": 73198.31,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"PLN\": {\n    \"24h_avg\": 2115.92,\n    \"ask\": 2139.02,\n    \"bid\": 2128.01,\n    \"last\": 2129.28,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 489.32,\n    \"volume_percent\": 0.6\n  },\n  \"PYG\": {\n    \"ask\": 3218560.23,\n    \"bid\": 3201991.25,\n    \"last\": 3203908.55,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"QAR\": {\n    \"ask\": 2542.81,\n    \"bid\": 2529.72,\n    \"last\": 2531.24,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"RON\": {\n    \"ask\": 2293.33,\n    \"bid\": 2281.52,\n    \"last\": 2282.89,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"RSD\": {\n    \"ask\": 59261.54,\n    \"bid\": 58956.46,\n    \"last\": 58991.76,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"RUB\": {\n    \"24h_avg\": 23988.14,\n    \"ask\": 24254.3,\n    \"bid\": 24129.44,\n    \"last\": 24143.89,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 155.55,\n    \"volume_percent\": 0.19\n  },\n  \"RWF\": {\n    \"ask\": 472119.99,\n    \"bid\": 469689.54,\n    \"last\": 469970.79,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SAR\": {\n    \"ask\": 2618.84,\n    \"bid\": 2605.36,\n    \"last\": 2606.92,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SBD\": {\n    \"ask\": 5076.52,\n    \"bid\": 5050.39,\n    \"last\": 5053.42,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SCR\": {\n    \"ask\": 8554.16,\n    \"bid\": 8510.12,\n    \"last\": 8515.21,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SDG\": {\n    \"ask\": 3971.94,\n    \"bid\": 3951.49,\n    \"last\": 3953.86,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SEK\": {\n    \"24h_avg\": 4480.42,\n    \"ask\": 4530.07,\n    \"bid\": 4506.75,\n    \"last\": 4509.44,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 18.31,\n    \"volume_percent\": 0.02\n  },\n  \"SGD\": {\n    \"24h_avg\": 875.84,\n    \"ask\": 885.54,\n    \"bid\": 880.98,\n    \"last\": 881.5,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 31.54,\n    \"volume_percent\": 0.04\n  },\n  \"SHP\": {\n    \"ask\": 425.53,\n    \"bid\": 423.34,\n    \"last\": 423.6,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SLL\": {\n    \"ask\": 3028282.65,\n    \"bid\": 3012693.21,\n    \"last\": 3014497.16,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SOS\": {\n    \"ask\": 754658.16,\n    \"bid\": 750773.22,\n    \"last\": 751222.77,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SRD\": {\n    \"ask\": 2301.38,\n    \"bid\": 2289.54,\n    \"last\": 2290.91,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"STD\": {\n    \"ask\": 12577713.67,\n    \"bid\": 12512964.28,\n    \"last\": 12520456.81,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SVC\": {\n    \"ask\": 6096.65,\n    \"bid\": 6065.27,\n    \"last\": 6068.9,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SYP\": {\n    \"ask\": 99040.07,\n    \"bid\": 98530.21,\n    \"last\": 98589.21,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"SZL\": {\n    \"ask\": 7714.95,\n    \"bid\": 7675.24,\n    \"last\": 7679.83,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"THB\": {\n    \"ask\": 22901.78,\n    \"bid\": 22783.88,\n    \"last\": 22797.52,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TJS\": {\n    \"ask\": 3352.59,\n    \"bid\": 3335.33,\n    \"last\": 3337.33,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TMT\": {\n    \"ask\": 1990.19,\n    \"bid\": 1979.94,\n    \"last\": 1981.13,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TND\": {\n    \"ask\": 1114.24,\n    \"bid\": 1108.51,\n    \"last\": 1109.17,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TOP\": {\n    \"ask\": 1315.03,\n    \"bid\": 1308.26,\n    \"last\": 1309.04,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TRY\": {\n    \"24h_avg\": 1532.76,\n    \"ask\": 1549.94,\n    \"bid\": 1541.96,\n    \"last\": 1542.88,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 20.47,\n    \"volume_percent\": 0.03\n  },\n  \"TTD\": {\n    \"ask\": 4455.38,\n    \"bid\": 4432.44,\n    \"last\": 4435.1,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TWD\": {\n    \"ask\": 21179.19,\n    \"bid\": 21070.16,\n    \"last\": 21082.77,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"TZS\": {\n    \"ask\": 1129957.57,\n    \"bid\": 1124140.61,\n    \"last\": 1124813.72,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"UAH\": {\n    \"ask\": 5958.12,\n    \"bid\": 5927.45,\n    \"last\": 5931.0,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"UGX\": {\n    \"ask\": 1719165.17,\n    \"bid\": 1710315.0,\n    \"last\": 1711339.11,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"USD\": {\n    \"24h_avg\": 690.62,\n    \"ask\": 698.27,\n    \"bid\": 694.68,\n    \"last\": 695.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 65598.29,\n    \"volume_percent\": 80.99\n  },\n  \"UYU\": {\n    \"ask\": 15431.94,\n    \"bid\": 15352.5,\n    \"last\": 15361.69,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"UZS\": {\n    \"ask\": 1539472.24,\n    \"bid\": 1531547.12,\n    \"last\": 1532464.18,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"VEF\": {\n    \"ask\": 4392.84,\n    \"bid\": 4370.22,\n    \"last\": 4372.84,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"VND\": {\n    \"ask\": 14709882.08,\n    \"bid\": 14634156.41,\n    \"last\": 14642919.07,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"VUV\": {\n    \"ask\": 67589.09,\n    \"bid\": 67241.15,\n    \"last\": 67281.41,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"WST\": {\n    \"ask\": 1633.59,\n    \"bid\": 1625.18,\n    \"last\": 1626.16,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XAF\": {\n    \"ask\": 336526.04,\n    \"bid\": 334793.62,\n    \"last\": 334994.09,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XAG\": {\n    \"ask\": 34.96,\n    \"bid\": 34.78,\n    \"last\": 34.8,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XAU\": {\n    \"ask\": 0.55,\n    \"bid\": 0.55,\n    \"last\": 0.55,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XCD\": {\n    \"ask\": 1886.04,\n    \"bid\": 1876.33,\n    \"last\": 1877.46,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XDR\": {\n    \"ask\": 454.77,\n    \"bid\": 452.43,\n    \"last\": 452.7,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XOF\": {\n    \"ask\": 336724.73,\n    \"bid\": 334991.29,\n    \"last\": 335191.88,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"XPF\": {\n    \"ask\": 61263.82,\n    \"bid\": 60948.44,\n    \"last\": 60984.93,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"YER\": {\n    \"ask\": 150114.88,\n    \"bid\": 149342.1,\n    \"last\": 149431.52,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ZAR\": {\n    \"24h_avg\": 7640.77,\n    \"ask\": 7725.85,\n    \"bid\": 7686.07,\n    \"last\": 7690.68,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 19.79,\n    \"volume_percent\": 0.02\n  },\n  \"ZMK\": {\n    \"ask\": 3645687.58,\n    \"bid\": 3626919.77,\n    \"last\": 3629091.51,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ZMW\": {\n    \"ask\": 3913.33,\n    \"bid\": 3893.18,\n    \"last\": 3895.52,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"ZWL\": {\n    \"ask\": 225090.98,\n    \"bid\": 223932.23,\n    \"last\": 224066.31,\n    \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\",\n    \"volume_btc\": 0.0,\n    \"volume_percent\": 0.0\n  },\n  \"timestamp\": \"Sun, 09 Feb 2014 20:08:37 -0000\"\n}";
    
    /*
     *
  {
  "AED": {
    "ask": 2564.82,
    "bid": 2551.61,
    "last": 2553.14,
    "timestamp": "Sun, 09 Feb 2014 20:08:37 -0000",
    "volume_btc": 0.0,
    "volume_percent": 0.0
  },
  "AFN": {
    "ask": 39604.16,
    "bid": 39400.28,
    "last": 39423.87,
    "timestamp": "Sun, 09 Feb 2014 20:08:37 -0000",
    "volume_btc": 0.0,
    "volume_percent": 0.0
  },
 
     *
     */
    
    private class FeedCurrencyInfo {
        public double ask;
        public double bid;
        public double last;
        public FeedCurrencyInfo(double ask, double bid, double last) {
            this.ask = ask;
            this.bid = bid;
            this.last = last;
        }
    }
    
    private class CurrencyRate {
        public String name;
        public double rate;
        public CurrencyRate(String name, double rate) {
            this.name = name;
            this.rate = rate;
        }
        public CurrencyRate(String name) {
            this.name = name;
            this.rate = 0.0d;
        }
    }
    
    private class JSONParser {
        //public List readJsonStream(InputStream in) throws IOException {
        //  JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        public HashMap<String, FeedCurrencyInfo> readJsonStream(String in) throws IOException {
            JsonReader reader = new JsonReader(new StringReader(in));
            try {
                return readCurrencies(reader);
            } finally {
                reader.close();
            }
        }

        public HashMap<String, FeedCurrencyInfo> readCurrencies(JsonReader reader) throws IOException {
            HashMap<String, FeedCurrencyInfo> messages = new HashMap<String, FeedCurrencyInfo>();
            
            reader.beginObject();
            while (reader.hasNext()) {
                String currencyName = reader.nextName();
                messages.put(currencyName, readCurrency(reader));
            }
            reader.endObject();
            return messages;
        }

        public FeedCurrencyInfo readCurrency(JsonReader reader) throws IOException {
            double ask = -1;
            double bid = -1;
            double last = -1;
            
            if (reader.peek() == JsonToken.STRING) {
                reader.skipValue();
            } else {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("ask")) {
                        ask = reader.nextDouble();
                    } else if (name.equals("bid")) {
                        bid = reader.nextDouble();
                    } else if (name.equals("last")) {
                        last = reader.nextDouble();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
            return new FeedCurrencyInfo(ask, bid, last);
        }
    }
    
    private void setupSupportedCurrencies() {
        rates.put(CURRENCY_AED, new CurrencyRate("AED"));
        rates.put(CURRENCY_AFN, new CurrencyRate("AFN"));
        rates.put(CURRENCY_ALL, new CurrencyRate("ALL"));
        rates.put(CURRENCY_AMD, new CurrencyRate("AMD"));
        rates.put(CURRENCY_ANG, new CurrencyRate("ANG"));
        rates.put(CURRENCY_AOA, new CurrencyRate("AOA"));
        rates.put(CURRENCY_ARS, new CurrencyRate("ARS"));
        rates.put(CURRENCY_AUD, new CurrencyRate("AUD"));
        rates.put(CURRENCY_AWG, new CurrencyRate("AWG"));
        rates.put(CURRENCY_AZN, new CurrencyRate("AZN"));
        rates.put(CURRENCY_BAM, new CurrencyRate("BAM"));
        rates.put(CURRENCY_BBD, new CurrencyRate("BBD"));
        rates.put(CURRENCY_BDT, new CurrencyRate("BDT"));
        rates.put(CURRENCY_BGN, new CurrencyRate("BGN"));
        rates.put(CURRENCY_BHD, new CurrencyRate("BHD"));
        rates.put(CURRENCY_BIF, new CurrencyRate("BIF"));
        rates.put(CURRENCY_BMD, new CurrencyRate("BMD"));
        rates.put(CURRENCY_BND, new CurrencyRate("BND"));
        rates.put(CURRENCY_BOB, new CurrencyRate("BOB"));
        rates.put(CURRENCY_BRL, new CurrencyRate("BRL"));
        rates.put(CURRENCY_BSD, new CurrencyRate("BSD"));
        rates.put(CURRENCY_BTC, new CurrencyRate("BTC"));
        rates.put(CURRENCY_BTN, new CurrencyRate("BTN"));
        rates.put(CURRENCY_BWP, new CurrencyRate("BWP"));
        rates.put(CURRENCY_BYR, new CurrencyRate("BYR"));
        rates.put(CURRENCY_BZD, new CurrencyRate("BZD"));
        rates.put(CURRENCY_CAD, new CurrencyRate("CAD"));
        rates.put(CURRENCY_CDF, new CurrencyRate("CDF"));
        rates.put(CURRENCY_CHF, new CurrencyRate("CHF"));
        rates.put(CURRENCY_CLF, new CurrencyRate("CLF"));
        rates.put(CURRENCY_CLP, new CurrencyRate("CLP"));
        rates.put(CURRENCY_CNY, new CurrencyRate("CNY"));
        rates.put(CURRENCY_COP, new CurrencyRate("COP"));
        rates.put(CURRENCY_CRC, new CurrencyRate("CRC"));
        rates.put(CURRENCY_CUP, new CurrencyRate("CUP"));
        rates.put(CURRENCY_CVE, new CurrencyRate("CVE"));
        rates.put(CURRENCY_CZK, new CurrencyRate("CZK"));
        rates.put(CURRENCY_DJF, new CurrencyRate("DJF"));
        rates.put(CURRENCY_DKK, new CurrencyRate("DKK"));
        rates.put(CURRENCY_DOP, new CurrencyRate("DOP"));
        rates.put(CURRENCY_DZD, new CurrencyRate("DZD"));
        rates.put(CURRENCY_EEK, new CurrencyRate("EEK"));
        rates.put(CURRENCY_EGP, new CurrencyRate("EGP"));
        rates.put(CURRENCY_ERN, new CurrencyRate("ERN"));
        rates.put(CURRENCY_ETB, new CurrencyRate("ETB"));
        rates.put(CURRENCY_EUR, new CurrencyRate("EUR"));
        rates.put(CURRENCY_FJD, new CurrencyRate("FJD"));
        rates.put(CURRENCY_FKP, new CurrencyRate("FKP"));
        rates.put(CURRENCY_GBP, new CurrencyRate("GBP"));
        rates.put(CURRENCY_GEL, new CurrencyRate("GEL"));
        rates.put(CURRENCY_GHS, new CurrencyRate("GHS"));
        rates.put(CURRENCY_GIP, new CurrencyRate("GIP"));
        rates.put(CURRENCY_GMD, new CurrencyRate("GMD"));
        rates.put(CURRENCY_GNF, new CurrencyRate("GNF"));
        rates.put(CURRENCY_GTQ, new CurrencyRate("GTQ"));
        rates.put(CURRENCY_GYD, new CurrencyRate("GYD"));
        rates.put(CURRENCY_HKD, new CurrencyRate("HKD"));
        rates.put(CURRENCY_HNL, new CurrencyRate("HNL"));
        rates.put(CURRENCY_HRK, new CurrencyRate("HRK"));
        rates.put(CURRENCY_HTG, new CurrencyRate("HTG"));
        rates.put(CURRENCY_HUF, new CurrencyRate("HUF"));
        rates.put(CURRENCY_IDR, new CurrencyRate("IDR"));
        rates.put(CURRENCY_ILS, new CurrencyRate("ILS"));
        rates.put(CURRENCY_INR, new CurrencyRate("INR"));
        rates.put(CURRENCY_IQD, new CurrencyRate("IQD"));
        rates.put(CURRENCY_IRR, new CurrencyRate("IRR"));
        rates.put(CURRENCY_ISK, new CurrencyRate("ISK"));
        rates.put(CURRENCY_JEP, new CurrencyRate("JEP"));
        rates.put(CURRENCY_JMD, new CurrencyRate("JMD"));
        rates.put(CURRENCY_JOD, new CurrencyRate("JOD"));
        rates.put(CURRENCY_JPY, new CurrencyRate("JPY"));
        rates.put(CURRENCY_KES, new CurrencyRate("KES"));
        rates.put(CURRENCY_KGS, new CurrencyRate("KGS"));
        rates.put(CURRENCY_KHR, new CurrencyRate("KHR"));
        rates.put(CURRENCY_KMF, new CurrencyRate("KMF"));
        rates.put(CURRENCY_KPW, new CurrencyRate("KPW"));
        rates.put(CURRENCY_KRW, new CurrencyRate("KRW"));
        rates.put(CURRENCY_KWD, new CurrencyRate("KWD"));
        rates.put(CURRENCY_KYD, new CurrencyRate("KYD"));
        rates.put(CURRENCY_KZT, new CurrencyRate("KZT"));
        rates.put(CURRENCY_LAK, new CurrencyRate("LAK"));
        rates.put(CURRENCY_LBP, new CurrencyRate("LBP"));
        rates.put(CURRENCY_LKR, new CurrencyRate("LKR"));
        rates.put(CURRENCY_LRD, new CurrencyRate("LRD"));
        rates.put(CURRENCY_LSL, new CurrencyRate("LSL"));
        rates.put(CURRENCY_LTL, new CurrencyRate("LTL"));
        rates.put(CURRENCY_LVL, new CurrencyRate("LVL"));
        rates.put(CURRENCY_LYD, new CurrencyRate("LYD"));
        rates.put(CURRENCY_MAD, new CurrencyRate("MAD"));
        rates.put(CURRENCY_MDL, new CurrencyRate("MDL"));
        rates.put(CURRENCY_MGA, new CurrencyRate("MGA"));
        rates.put(CURRENCY_MKD, new CurrencyRate("MKD"));
        rates.put(CURRENCY_MMK, new CurrencyRate("MMK"));
        rates.put(CURRENCY_MNT, new CurrencyRate("MNT"));
        rates.put(CURRENCY_MOP, new CurrencyRate("MOP"));
        rates.put(CURRENCY_MRO, new CurrencyRate("MRO"));
        rates.put(CURRENCY_MTL, new CurrencyRate("MTL"));
        rates.put(CURRENCY_MUR, new CurrencyRate("MUR"));
        rates.put(CURRENCY_MVR, new CurrencyRate("MVR"));
        rates.put(CURRENCY_MWK, new CurrencyRate("MWK"));
        rates.put(CURRENCY_MXN, new CurrencyRate("MXN"));
        rates.put(CURRENCY_MYR, new CurrencyRate("MYR"));
        rates.put(CURRENCY_MZN, new CurrencyRate("MZN"));
        rates.put(CURRENCY_NAD, new CurrencyRate("NAD"));
        rates.put(CURRENCY_NGN, new CurrencyRate("NGN"));
        rates.put(CURRENCY_NIO, new CurrencyRate("NIO"));
        rates.put(CURRENCY_NOK, new CurrencyRate("NOK"));
        rates.put(CURRENCY_NPR, new CurrencyRate("NPR"));
        rates.put(CURRENCY_NZD, new CurrencyRate("NZD"));
        rates.put(CURRENCY_OMR, new CurrencyRate("OMR"));
        rates.put(CURRENCY_PAB, new CurrencyRate("PAB"));
        rates.put(CURRENCY_PEN, new CurrencyRate("PEN"));
        rates.put(CURRENCY_PGK, new CurrencyRate("PGK"));
        rates.put(CURRENCY_PHP, new CurrencyRate("PHP"));
        rates.put(CURRENCY_PKR, new CurrencyRate("PKR"));
        rates.put(CURRENCY_PLN, new CurrencyRate("PLN"));
        rates.put(CURRENCY_PYG, new CurrencyRate("PYG"));
        rates.put(CURRENCY_QAR, new CurrencyRate("QAR"));
        rates.put(CURRENCY_RON, new CurrencyRate("RON"));
        rates.put(CURRENCY_RSD, new CurrencyRate("RSD"));
        rates.put(CURRENCY_RUB, new CurrencyRate("RUB"));
        rates.put(CURRENCY_RWF, new CurrencyRate("RWF"));
        rates.put(CURRENCY_SAR, new CurrencyRate("SAR"));
        rates.put(CURRENCY_SBD, new CurrencyRate("SBD"));
        rates.put(CURRENCY_SCR, new CurrencyRate("SCR"));
        rates.put(CURRENCY_SDG, new CurrencyRate("SDG"));
        rates.put(CURRENCY_SEK, new CurrencyRate("SEK"));
        rates.put(CURRENCY_SGD, new CurrencyRate("SGD"));
        rates.put(CURRENCY_SHP, new CurrencyRate("SHP"));
        rates.put(CURRENCY_SLL, new CurrencyRate("SLL"));
        rates.put(CURRENCY_SOS, new CurrencyRate("SOS"));
        rates.put(CURRENCY_SRD, new CurrencyRate("SRD"));
        rates.put(CURRENCY_STD, new CurrencyRate("STD"));
        rates.put(CURRENCY_SVC, new CurrencyRate("SVC"));
        rates.put(CURRENCY_SYP, new CurrencyRate("SYP"));
        rates.put(CURRENCY_SZL, new CurrencyRate("SZL"));
        rates.put(CURRENCY_THB, new CurrencyRate("THB"));
        rates.put(CURRENCY_TJS, new CurrencyRate("TJS"));
        rates.put(CURRENCY_TMT, new CurrencyRate("TMT"));
        rates.put(CURRENCY_TND, new CurrencyRate("TND"));
        rates.put(CURRENCY_TOP, new CurrencyRate("TOP"));
        rates.put(CURRENCY_TRY, new CurrencyRate("TRY"));
        rates.put(CURRENCY_TTD, new CurrencyRate("TTD"));
        rates.put(CURRENCY_TWD, new CurrencyRate("TWD"));
        rates.put(CURRENCY_TZS, new CurrencyRate("TZS"));
        rates.put(CURRENCY_UAH, new CurrencyRate("UAH"));
        rates.put(CURRENCY_UGX, new CurrencyRate("UGX"));
        rates.put(CURRENCY_USD, new CurrencyRate("USD"));
        rates.put(CURRENCY_UYU, new CurrencyRate("UYU"));
        rates.put(CURRENCY_UZS, new CurrencyRate("UZS"));
        rates.put(CURRENCY_VEF, new CurrencyRate("VEF"));
        rates.put(CURRENCY_VND, new CurrencyRate("VND"));
        rates.put(CURRENCY_VUV, new CurrencyRate("VUV"));
        rates.put(CURRENCY_WST, new CurrencyRate("WST"));
        rates.put(CURRENCY_XAF, new CurrencyRate("XAF"));
        rates.put(CURRENCY_XAG, new CurrencyRate("XAG"));
        rates.put(CURRENCY_XAU, new CurrencyRate("XAU"));
        rates.put(CURRENCY_XCD, new CurrencyRate("XCD"));
        rates.put(CURRENCY_XDR, new CurrencyRate("XDR"));
        rates.put(CURRENCY_XOF, new CurrencyRate("XOF"));
        rates.put(CURRENCY_XPF, new CurrencyRate("XPF"));
        rates.put(CURRENCY_YER, new CurrencyRate("YER"));
        rates.put(CURRENCY_ZAR, new CurrencyRate("ZAR"));
        rates.put(CURRENCY_ZMK, new CurrencyRate("ZMK"));
        rates.put(CURRENCY_ZMW, new CurrencyRate("ZMW"));
        rates.put(CURRENCY_ZWL, new CurrencyRate("ZWL"));
    }
}
