package eu.pryds.misermeter;

import android.util.SparseArray;
import eu.pryds.misermeter.AddressArrayAdapter.BalanceConverter;

public class BitcoinchartsConverter implements AddressArrayAdapter.BalanceConverter {
    SparseArray<Double> rates;
    
    public BitcoinchartsConverter() {
        rates = new SparseArray<Double>();
        rates.put(BalanceConverter.CURRENCY_USD, Double.valueOf(848.68));
        rates.put(BalanceConverter.CURRENCY_DKK, Double.valueOf(5029.00));
        rates.put(BalanceConverter.CURRENCY_EUR, Double.valueOf(638.73));
        
    }
    
    public double convertValue(double fromValue, int toCurrency) {
        Double value = rates.get(toCurrency);
        if (value == null)
            return 0.0;
        return fromValue * value.doubleValue();
    }

    public void updateFromFeed() {
        // TODO Auto-generated method stub
        // remember: only once per 15 minutes!
    }
}
