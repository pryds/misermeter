package eu.pryds.misermeter;

import java.util.ArrayList;
import java.util.Random;

import eu.pryds.misermeter.AddAddressDialogFragment.AddAddressDialogListener;
import eu.pryds.misermeter.AddressArrayAdapter.AddressItem;
import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

import android.os.Bundle;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements AddAddressDialogListener {
    public static final String DEBUG_STR = "MiserMeterDebug";
    public static final String PREFS_NAME = "MiserMeterPrefs";
    private static final String PREFS_ADDRESS_COUNT = "addressCount";
    private static final String PREFS_ADDRESSTYPE_PREFIX = "itemAddressType";
    private static final String PREFS_ADDRESS_PREFIX = "itemAddress";
    private static final String PREFS_COMMENT_PREFIX = "itemComment";
    private static final String PREFS_CONVERTERTYPE_PREFIX = "itemConvertertype";
    
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
        
        // Set up (stored) addresses:
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        int addressCount = settings.getInt(PREFS_ADDRESS_COUNT, 0);
        for (int i = 0; i < addressCount; i++) {
            int addressType = settings.getInt(PREFS_ADDRESSTYPE_PREFIX + i, -1);
            String address = settings.getString(PREFS_ADDRESS_PREFIX + i, "");
            String comment = settings.getString(PREFS_COMMENT_PREFIX + i, "");
            int converterType = settings.getInt(PREFS_CONVERTERTYPE_PREFIX + i, -1);
            
            AddressItem item = null;
            switch (addressType) {
            case AddressItem.ADDRESSTYPE_BITCOIN:
                item = new BitcoinAddress(address, comment, balanceConverters.get(converterType));
                break; //TODO: breaks out of switch of of for?
            default:
                Log.e(DEBUG_STR, "Error: Trying to add unimplemented address type. Ignoring.");
            }
            if (item != null)
                addressArray.add(item);
        }
        
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add_address:
            AddAddressDialogFragment addAddress = new AddAddressDialogFragment();
            addAddress.show(getFragmentManager(), "addaddress");
            return true;
        case R.id.action_settings:
            Log.d(DEBUG_STR, "Settings option selected");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onDestroy() {
        Log.d(DEBUG_STR, "Setting runUpdateThreads = false");
        runUpdateThreads = false;
        super.onDestroy();

    }
    
    /**
     * Sleeps somewhere random between minutesMinimum and minutesMinimum*2.
     * But stops sleeping if update threads are requested to stop,
     * i.e. runUpdateThreads == false
     * Do not use on UI thread!
     * @param minutesMinimum minutes to sleep
     */
    private void updateThreadSleepRandom(long minutesMinimum) {
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

    @Override
    public void onReturnNewAddressValues(String address, String comment) {
        // TODO: make generic
        // Construct new AddressItem instance and add to array
        final AddressItem newAddress = new BitcoinAddress(address, comment,
                balanceConverters.get(BalanceConverter.CONV_BITCOINAVERAGE_GLOBALTICKER));
        addressArray.add(newAddress);
        adapter.notifyDataSetChanged();
        
        // Save AddressItem info in prefs
        addLastAddressItemToPrefs();
        
        // Update address balance
        new Thread() {
            public void run() {
                newAddress.updateFromFeed();
                        
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }
    
    private void addLastAddressItemToPrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        int newIndex = addressArray.size() - 1;
        
        editor.putInt(PREFS_ADDRESS_COUNT, addressArray.size());
        editor.putInt(PREFS_ADDRESSTYPE_PREFIX + newIndex, addressArray.get(newIndex).getAddressType());
        editor.putString(PREFS_ADDRESS_PREFIX + newIndex, addressArray.get(newIndex).getAddress());
        editor.putString(PREFS_COMMENT_PREFIX + newIndex, addressArray.get(newIndex).getComment());
        editor.putInt(PREFS_CONVERTERTYPE_PREFIX + newIndex, addressArray.get(newIndex).getConverter().getConverterType());
        editor.commit();
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
