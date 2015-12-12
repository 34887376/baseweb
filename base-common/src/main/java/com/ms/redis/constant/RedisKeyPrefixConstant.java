package com.ms.redis.constant;

public class RedisKeyPrefixConstant {
	
	//第一个开始促销的rediskey前缀
	public static final String START_PROMOTION_INDEX="startPromotionIndexKey"; 
	
	//第一个开始促销的rediskey有效时间(单位为秒)
	public static final int START_PROMOTION_TIME=60 * 30;
	
	//促销综合信息的前缀
	public static final String PROMOTION_INFO_INDEX="promotionInfoContentPrefixKey"; 
	
	//促销的前缀有效时间
	public static final int PROMOTION_INFO_INDEX_TIME=60 * 30;
	
	//促销的前缀
	public static final String PROMOTION_PREFIXE="promotionContentPrefixKey"; 
	
	//促销的前缀有效时间
	public static final int PROMOTION_PREFIXE_TIME=60 * 30;
	
	//促销序列redis存储前缀
	public static final String PROMOTION_SEQUENCE_PROMOTIONID_PRIFIXE="promotionSequence_promotionid_content"; 
	
	//促销序列redis存储有效时间
	public static final int PROMOTION_SEQUENCE_PROMOTIONID_TIME=60 * 30;
	
	//促销序列redis存储前缀
	public static final String PROMOTION_SEQUENCE_ID_PRIFIXE="promotionSequence_id_content"; 
	
	//促销序列redis存储有效时间
	public static final int PROMOTION_SEQUENCE_ID_TIME=60 * 30;
	
	//商品信息redis存储前缀
	public static final String SKU_PRIFIXE="sku_content"; 
	
	//商品信息redis存储有效时间
	public static final int SKU_TIME=60 * 30;

	
	//阶梯规则信息redis存储前缀
	public static final String LADDER_PRIFIXE="ladder_content"; 
	
	//阶梯规则信息redis存储有效时间
	public static final int LADDER_TIME=60 * 30;
	
	//阶梯促销信息redis存储前缀
	public static final String LADDER_PROMOTION_PRIFIXE="ladderPromotion_content"; 
	
	//阶梯促销信息redis存储有效时间
	public static final int LADDER_PROMOTION_TIME=60 * 30;
}
