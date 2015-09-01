package base.test.rpc.http;

import java.util.ArrayList;
import java.util.List;

public class RealtimePriceResult {

	private List<RealtimePrice> realtimePrices = new ArrayList<RealtimePrice>();
	
	public List<RealtimePrice> getRealtimePrices() {
		return realtimePrices;
	}

	public void setRealtimePrices(List<RealtimePrice> realtimePrices) {
		this.realtimePrices = realtimePrices;
	}

	public static class RealtimePrice {
		
		// 商品id
		private Long skuId;
		
		// 商品市场价
		private Double marketPrice;
		
		// 京东价
		private Double jdPrice;

		public Long getSkuId() {
			return skuId;
		}

		public void setSkuId(Long skuId) {
			this.skuId = skuId;
		}

		public Double getMarketPrice() {
			return marketPrice;
		}

		public void setMarketPrice(Double marketPrice) {
			this.marketPrice = marketPrice;
		}

		public Double getJdPrice() {
			return jdPrice;
		}

		public void setJdPrice(Double jdPrice) {
			this.jdPrice = jdPrice;
		}
		
	}
	
}
