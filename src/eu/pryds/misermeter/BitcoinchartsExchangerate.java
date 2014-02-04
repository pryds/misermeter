package eu.pryds.misermeter;

import java.util.HashMap;

public class BitcoinchartsExchangerate {
    HashMap<String, Double> currencies;
    
    public BitcoinchartsExchangerate() {
        currencies = new HashMap<String, Double>();
        currencies.put("USD", Double.valueOf(848.68));
        currencies.put("DKK", Double.valueOf(5029.00));
        currencies.put("EUR", Double.valueOf(638.73));
        
    }
    
    public double convertValue(double fromValue, String toCurrency) {
        if (!currencies.containsKey(toCurrency))
            return 0;
        return fromValue * currencies.get(toCurrency);
    }
}
