package eu.pryds.misermeter;

import java.util.ArrayList;
import java.util.Random;

import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {
    public static final String DEBUG_STR = "MiserMeterDebug";
    Random rand = new Random();
    
    private ArrayList<AddressArrayAdapter.AddressItem> addressArray
            = new ArrayList<AddressArrayAdapter.AddressItem>();
    private AddressArrayAdapter adapter;
    
    private SparseArray<AddressArrayAdapter.BalanceConverter> balanceConverters
            = new SparseArray<AddressArrayAdapter.BalanceConverter>();
    
    private boolean runUpdateThreads = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up available converters:
        balanceConverters.put(BalanceConverter.CONV_BITCOINCHARTS, new BitcoinchartsConverter());
        balanceConverters.put(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER, new BitcoinaverageGlobaltickerConverter());
        
        //TODO: Set up (stored) addresses:
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
            public void run() {
                while (runUpdateThreads) {
                    Log.d(DEBUG_STR, "Address balance update thread: update started.");
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
                    updateThreadSleepRandom(1);
                }
                Log.d(DEBUG_STR, "Address balance update thread shutting down.");
            }
        }.start();
                
        // Launch exchange rate update thread
        new Thread() {
            public void run() {
                while (runUpdateThreads) {
                    Log.d(DEBUG_STR, "Exchange rate update thread: update started.");
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
                    updateThreadSleepRandom(60);
                }
                Log.d(DEBUG_STR, "Exchange rate update thread shutting down.");
            }
        }.start();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    public void onDestroy() {
        Log.d(DEBUG_STR, "Setting runUpdateThreads = false");
        runUpdateThreads = false;
        super.onDestroy();

    }
    
    /**
     * Sleeps somewhere random between minutesMinimum and minutesMinimum*2.
     * But stops waiting if updateThreads are requested to stop,
     * i.e. runUpdateThreads == false
     * @param minutesMinimum minutes to sleep
     */
    private void updateThreadSleepRandom(long minutesMinimum) {
        //sleeps somewhere between minutesMinimum and minutesMinimum*2
        //but stops waiting if 
        //do not use on UI thread!
        long waitForMillis = minutesMinimum * 60 * 1000;
        waitForMillis = waitForMillis + (long)(waitForMillis * rand.nextFloat());
        Log.d(DEBUG_STR, "Thread sleeping for " + (waitForMillis/1000.0) + " sec (asked: " + (minutesMinimum*60) + ").");
        
        long startTime = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        
        while (now < startTime + waitForMillis && runUpdateThreads) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            now = System.currentTimeMillis();
        }
    }
}
