package eu.pryds.misermeter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
    
    public int getConverterType() {
        return CONV_BITCOINAVERAGE_GLOBALTICKER;
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
