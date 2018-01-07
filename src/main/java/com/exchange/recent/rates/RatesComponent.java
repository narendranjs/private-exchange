/**
 * 
 */
package com.exchange.recent.rates;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bitstamp.dto.marketdata.BitstampTicker;
import org.knowm.xchange.bitstamp.service.BitstampAccountService;
import org.knowm.xchange.bitstamp.service.BitstampMarketDataServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.exchange.recent.rates.domain.CustomBalance;


/**
 * @author narendranjs
 *	
 */
@RestController
public class RatesComponent {
	
	@Value("${binance.apiKey}")
	private String binanceApiKey;
	
	@Value("${binance.secretKey}")
	private String binanceSecretKey;
	
	@Value("${binance.userId}")
	private String binanceUserId;
	
	@Value("${bitstamp.apiKey}")
	private String bitstampApiKey;
	
	@Value("${bitstamp.secretKey}")
	private String bitstampSecretKey;
	
	@Value("${bitstamp.userId}")
	private String bitstampUserId;
	
	
    @RequestMapping(value ="/greeting",method=RequestMethod.GET, produces="application/json")
    public List<CustomBalance> greeting() {
    	List<CustomBalance> binanceBalances = fetchBinanceDetails();
    	List<CustomBalance> bitStampBalances = fetchBitStampDetails();
    	List<CustomBalance> balances = new ArrayList<CustomBalance>();
    	
    	balances.addAll(binanceBalances);
    	balances.addAll(bitStampBalances);
    	CustomBalance  balance = new CustomBalance();
    	balance.setCurrencyCode("TOTAL");
    	BigDecimal total = printTotalValue(balances).setScale(2, RoundingMode.HALF_EVEN);
    	balance.setTotalValue(total);
    	System.out.println("Total : "+total);
    	balances.add(balance);
        return balances;
    }

	private List<CustomBalance> fetchBitStampDetails() {
		List<CustomBalance> balances = new ArrayList<CustomBalance>();
		
		Exchange bitStampExchange = getBitStampExchange();
		BitstampAccountService accountService = (BitstampAccountService) bitStampExchange.getAccountService();
		
		Exchange tickerExchange = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
		try {
			Wallet wallet = accountService.getAccountInfo().getWallet();
			balances.addAll(processBitStampWallet(wallet, (BitstampExchange) tickerExchange));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("BitStamp : "+printTotalValue(balances));
		return balances;
	}

	private Collection<? extends CustomBalance> processBitStampWallet(Wallet wallet, BitstampExchange tickerExchange) throws IOException {
		BitstampMarketDataServiceRaw bitstampMarketDataServiceRaw = (BitstampMarketDataServiceRaw) tickerExchange.getMarketDataService();
		List<CustomBalance> balances = new ArrayList<CustomBalance>();
		Map<Currency, Balance> map = wallet.getBalances();
		Set<CurrencyPair> entryset = tickerExchange.getExchangeMetaData().getCurrencyPairs().keySet();
		BitstampTicker bitStampTicker = null;
		for(Map.Entry<Currency, Balance> entry : map.entrySet()){
			Balance balance = entry.getValue();
			BigDecimal total =  balance.getTotal();
			if(total.intValue() != 0){
				for (CurrencyPair cp : entryset) {
			        if (cp.base.getCurrencyCode().equals(balance.getCurrency().getCurrencyCode())) {
			        	bitStampTicker =  bitstampMarketDataServiceRaw.getBitstampTicker(CurrencyPair.XRP_USD);
//			        	System.out.println("XRP_USD "+bitStampTicker.getLast() +" -  " +bitStampTicker);
			            break;
			        }
			    }
				CustomBalance customBalance = convertToCustomBalance(balance);
				customBalance.setCurrentRate(null!=bitStampTicker ? bitStampTicker.getLast() : BigDecimal.valueOf(0));
				customBalance.setTotalValue(customBalance.getAvailable().multiply(customBalance.getCurrentRate()).setScale(2, RoundingMode.HALF_EVEN));
				balances.add(customBalance);
			}
		}
		return balances;
	}

	private List<CustomBalance> fetchBinanceDetails() {
		Exchange binance = getBinance();
    	BinanceAccountService accountService = (BinanceAccountService) binance.getAccountService();
    	Exchange tickerExchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
    	MarketDataService marketDataService = tickerExchange.getMarketDataService();
    	List<CustomBalance> balances = new ArrayList<CustomBalance>();
    	try {
			Wallet wallet =  accountService.getAccountInfo().getWallet();
			balances.addAll(processWallet(wallet, (BinanceMarketDataServiceRaw) marketDataService, (BinanceExchange) tickerExchange));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("Binance "+printTotalValue(balances));
		return balances;
	}

	private BigDecimal printTotalValue(List<CustomBalance> balances) {
		BigDecimal totalValue = BigDecimal.valueOf(0).setScale(8);
		for (CustomBalance balance : balances) {
			if (!StringUtils.equals("BTC", balance.getCurrencyCode())) {
				totalValue = totalValue.add(balance.getTotalValue());
			}
		} 
		return totalValue;
	}

	private List<CustomBalance> processWallet(Wallet wallet, BinanceMarketDataServiceRaw dataService, BinanceExchange binance) throws IOException {
		Map<Currency, Balance> map = wallet.getBalances();
		List<CustomBalance> resultList = new ArrayList<CustomBalance>();
		BinanceTicker24h binanceTicker24h = null;
		BinanceTicker24h btcTicker = dataService.ticker24h(new CurrencyPair(Currency.BTC, Currency.USDT));
		Set<CurrencyPair> entryset = binance.getExchangeMetaData().getCurrencyPairs().keySet();
		for(Map.Entry<Currency, Balance> entry : map.entrySet()){
			Balance balance = entry.getValue();
			BigDecimal total =  balance.getTotal();
			if(!StringUtils.equals(total.toString(),"0E-8")){
				for (CurrencyPair cp : entryset) {
			        if (cp.base.getCurrencyCode().equals(balance.getCurrency().getCurrencyCode())) {
//			        	System.out.print(cp.base.getCurrencyCode()+"_"+cp.counter.getCurrencyCode());
			        	CurrencyPair currencyPair = null;
						if (StringUtils.equals(cp.base.getCurrencyCode(), "BTC")) {
							currencyPair = new CurrencyPair(cp.base, Currency.USDT);
						} else {
							currencyPair = new CurrencyPair(cp.base, Currency.BTC);
						}
//						System.out.println(currencyPair);
			            binanceTicker24h =  dataService.ticker24h(currencyPair);
//			            System.out.println("  "+binanceTicker24h.getLastPrice() +" -  " +binanceTicker24h.getCurrencyPair());
			            break;
			        }
			    }
				CustomBalance customBalance = convertToCustomBalance(balance);
				customBalance.setCurrentRate(null!=binanceTicker24h ? binanceTicker24h.getLastPrice() : BigDecimal.valueOf(1));
				if(StringUtils.equals(customBalance.getCurrencyCode(), "BTC")){
					customBalance.setTotalValue(customBalance.getAvailable().multiply(btcTicker.getLastPrice())
							.setScale(2, RoundingMode.HALF_EVEN));
				} else {
					customBalance.setTotalValue(customBalance.getAvailable().multiply(customBalance.getCurrentRate())
							.setScale(8, RoundingMode.HALF_EVEN));
					customBalance.setTotalValue(customBalance.getTotalValue().multiply(btcTicker.getLastPrice())
							.setScale(2, RoundingMode.HALF_EVEN));
				}
				resultList.add(customBalance);
			}
		}
		
		return resultList;
	}

	private CustomBalance convertToCustomBalance(Balance balance) {
		CustomBalance customBalance = new CustomBalance();
		customBalance.setAvailable(balance.getAvailable());
		customBalance.setCurrencyCode(balance.getCurrency().getCurrencyCode());
		customBalance.setTotal(balance.getTotal());
		return customBalance;
	}

	private Exchange getBinance() {
		ExchangeSpecification binance =  new BinanceExchange().getDefaultExchangeSpecification();
    	binance.setUserName(binanceUserId);
    	binance.setApiKey(binanceApiKey);
    	binance.setSecretKey(binanceSecretKey);
    	return ExchangeFactory.INSTANCE.createExchange(binance);
	}
	
	private Exchange getBitStampExchange(){
		ExchangeSpecification bitStamp = new BitstampExchange().getDefaultExchangeSpecification();
		bitStamp.setUserName(bitstampUserId);
		bitStamp.setApiKey(bitstampApiKey);
		bitStamp.setSecretKey(bitstampSecretKey);
		return ExchangeFactory.INSTANCE.createExchange(bitStamp);
	}
}
