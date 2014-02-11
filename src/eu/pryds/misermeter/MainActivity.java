package eu.pryds.misermeter;

import java.util.ArrayList;
import java.util.Random;

import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.os.Bundle;
import android.app.Activity;
import android.util.SparseArray;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private ArrayList<AddressArrayAdapter.AddressItem> addressArray
            = new ArrayList<AddressArrayAdapter.AddressItem>();
    private AddressArrayAdapter adapter;
    
    private SparseArray<AddressArrayAdapter.BalanceConverter> balanceConverters
            = new SparseArray<AddressArrayAdapter.BalanceConverter>();
    
    private boolean runUpdateThreads = true; //TODO: Set to false when app is shut down
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up available converters:
        balanceConverters.put(BalanceConverter.CONV_BITCOINCHARTS, new BitcoinchartsConverter());
        balanceConverters.put(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER, new BitcoinaverageGlobaltickerConverter());
        
        
        addressArray.add(new BitcoinAddress("1DBUPW5iJq13Ufb2TkiXbFPBGZzGP2ao2", "bitcoin address 1", balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER)));
        addressArray.add(new BitcoinAddress("1AXELoQwPgBeLs5UB9hRQoKR6HMnK4mU54", "Bitcoin address 2", balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER)));
        addressArray.add(new BitcoinAddress("19a6z2UjoePW923uS4uTyaCeXqGvYBx7Pf", "Bitcoin address 3", balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER)));
        addressArray.add(new BitcoinAddress("13EyH24DhHSdoNdXYTVQJMDvWQihpEhGqk", "Bitcoin address 4", balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER)));
        addressArray.add(new BitcoinAddress("1Hj6nKoPYhp8PtmxSZkZ9MWLpyW8C74Su9", "Bitcoin address 5", balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER)));
        
        
        
        
        // Set up ArrayAdapter that puts data from addressVector into ListView:
        // Ref: http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews
        adapter = new AddressArrayAdapter(this, 
                android.R.layout.simple_list_item_1, addressArray);
        
        ListView listView = (ListView) findViewById(R.id.address_list);
        listView.setAdapter(adapter);
        
        
        // Launch address balance update thread
        new Thread() {
            public void run() { //TODO: Add log messages. updates seem not to take place
                while (runUpdateThreads) {
                    for (int i = 0; i < addressArray.size(); i++) {
                        addressArray.get(i).updateFromFeed();
                        
                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    //sleep between updates:
                    //but it's the responsibility of the AddressItem to make
                    //sure that the feed is not polled more than necessary/allowed
                    threadSleepRandom(1);
                }
            }
        }.start();
                
        // Launch exchange rate update thread
        new Thread() {
            public void run() {
                while (runUpdateThreads) {
                    for (int i = 0; i < balanceConverters.size(); i++) {
                        int key = balanceConverters.keyAt(i);
                        balanceConverters.get(key).updateFromFeed();
                        
                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    //sleep between updates:
                    //but it's the responsibility of the BalanceConverter to make
                    //sure that the feed is not polled more than necessary/allowed
                    threadSleepRandom(60);
                }
            }
        }.start();
        
        
        
        
        
        
        double btc = 0.0001;
        BitcoinchartsConverter btcr = new BitcoinchartsConverter();
        
        TextView t = (TextView) findViewById(R.id.textbox);
        t.setText("BTC: " + formatBTC(btc) + "\nDKK: " + formatLocal(btcr.convertValue(btc, BalanceConverter.CURRENCY_DKK)));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    private static String formatBTC(double btc) {
        return String.format("%.8f", btc);
    }
    
    private static String formatLocal(double localCurrency) {
        return String.format("%.2f", localCurrency);
    }
    
    private void threadSleepRandom(int minutesMinimum) {
        //sleep somewhere between minutesMinimum and minutesMinimum*2
        //do not use on UI thread!
        int millis = minutesMinimum * 60 * 1000;
        Random r = new Random();
        try {
            Thread.sleep(millis + (long)(millis * r.nextFloat()) );
        } catch (Exception e) {
        }
    }
}
