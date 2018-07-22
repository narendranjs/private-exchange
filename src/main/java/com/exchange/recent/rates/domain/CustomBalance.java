package com.exchange.recent.rates.domain;

import java.math.BigDecimal;

public class CustomBalance {
	String currencyCode ;
	BigDecimal total;
	BigDecimal available;
	BigDecimal currentRate;
	BigDecimal initialRate;
	BigDecimal investedAmount;
	BigDecimal profit;
	BigDecimal totalValue;
	/**
	 * @return the totalValue
	 */
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	/**
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	/**
	 * @return the currentRate
	 */
	public BigDecimal getCurrentRate() {
		return currentRate;
	}
	/**
	 * @param currentRate the currentRate to set
	 */
	public void setCurrentRate(BigDecimal currentRate) {
		this.currentRate = currentRate;
	}
	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	/**
	 * @return the available
	 */
	public BigDecimal getAvailable() {
		return available;
	}
	/**
	 * @param available the available to set
	 */
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	/**
	 * @return the initialRate
	 */
	public BigDecimal getInitialRate() {
		return initialRate;
	}
	/**
	 * @param initialRate the initialRate to set
	 */
	public void setInitialRate(BigDecimal initialRate) {
		this.initialRate = initialRate;
	}
	/**
	 * @return the investedAmount
	 */
	public BigDecimal getInvestedAmount() {
		return investedAmount;
	}
	/**
	 * @param investedAmount the investedAmount to set
	 */
	public void setInvestedAmount(BigDecimal investedAmount) {
		this.investedAmount = investedAmount;
	}
	/**
	 * @return the profit
	 */
	public BigDecimal getProfit() {
		return profit;
	}
	/**
	 * @param profit the profit to set
	 */
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomBalance [currencyCode=" + currencyCode + ", available=" + available + ", currentRate="
				+ currentRate + ", initialRate=" + initialRate + ", investedAmount=" + investedAmount + ", profit="
				+ profit + ", totalValue=" + totalValue + "]";
	}
	
	
}
