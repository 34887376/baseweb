package com.ms.service.backStage.sku.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.sku.face.ISkuDAO;
import com.ms.domain.sku.bo.SkuBO;
import com.ms.domain.sku.dao.SkuDAO;
import com.ms.service.backStage.sku.face.IBackSkuService;

/**
 * 商品管理服务
 * @author zhoushanjie
 *
 */
public class BackSkuServiceImpl implements IBackSkuService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private ISkuDAO iSkuDAO;

	public boolean addSku(SkuBO skuBO) {
		try {
			if(skuBO==null){
				return false;
			}
			
			SkuDAO skuDAO = new SkuDAO();
			skuDAO.setName(skuBO.getName());
			skuDAO.setNum(skuBO.getNum());
			skuDAO.setAdverst(skuBO.getAdverst());
			skuDAO.setImgUrl(skuBO.getImgUrl());
			skuDAO.setInprice(skuBO.getInprice());
			skuDAO.setOutprice(skuBO.getOutprice());
			skuDAO.setYn(skuBO.isYn());
			long skuId = iSkuDAO.addSku(skuDAO);
			if(skuId>0){
				return true;
			}
		} catch (Exception e) {
			logger.error("SkuManagerServiceImpl.addSku添加商品时发生异常！！！", e);
		}
		return false;
	}

	public boolean updateSkuInfo(SkuBO skuBO) {
		try {
			if(skuBO==null){
				return false;
			}
			
			SkuDAO skuDAO = new SkuDAO();
			skuDAO.setId(skuBO.getId());
			skuDAO.setName(skuBO.getName());
			skuDAO.setNum(skuBO.getNum());
			skuDAO.setAdverst(skuBO.getAdverst());
			skuDAO.setImgUrl(skuBO.getImgUrl());
			skuDAO.setInprice(skuBO.getInprice());
			skuDAO.setOutprice(skuBO.getOutprice());
			skuDAO.setYn(skuBO.isYn());
			boolean isOk = iSkuDAO.updateSkuInfo(skuDAO);
			return isOk;
		} catch (Exception e) {
			logger.error("SkuManagerServiceImpl.updateSkuInfo更新商品时发生异常！！！", e);
		}
		return false;
	}

	public boolean delSkuInfo(Long id) {
		
		try {
			if(id==null || id<1){
				return false;
			}
			boolean isOk = iSkuDAO.delSkuInfo(id);
			return isOk;
			
		} catch (Exception e) {
			logger.error("SkuManagerServiceImpl.delSkuInfo删除商品时发生异常！！！", e);
		}
		return false;
	}

	public SkuBO querySkuById(Long skuId) {
		SkuBO skuBO = new SkuBO();
		try {
			if(skuId==null || skuId<1){
				return skuBO;
			}
			
			SkuDAO skuDAO = new SkuDAO();
			skuDAO.setId(skuId);
			skuDAO = iSkuDAO.querySkuById(skuId);
			if(skuDAO==null){
				return skuBO;
			}
			skuBO.setName(skuDAO.getName());
			skuBO.setNum(skuDAO.getNum());
			skuBO.setAdverst(skuDAO.getAdverst());
			skuBO.setImgUrl(skuDAO.getImgUrl());
			skuBO.setInprice(skuDAO.getInprice());
			skuBO.setOutprice(skuDAO.getOutprice());
			skuBO.setId(skuDAO.getId());
			skuBO.setYn(skuDAO.isYn());
			return skuBO;
			
		} catch (Exception e) {
			logger.error("SkuManagerServiceImpl.querySkuById查询商品时发生异常！！！", e);
		}
		return skuBO;
	}

	public List<SkuBO> querySkuListByIds(List<Long> skuIds) {
		return null;
	}
	
	public List<SkuBO> querySkuListByPageNum(int page,int pageSize) {
		List<SkuBO> skuBOList = new ArrayList<SkuBO>();
		try {
			if(page<0 || pageSize<0){
				return skuBOList;
			}
			List<SkuDAO> skuList = iSkuDAO.querySkuListByPageNum(1, pageSize);
			
			if(CollectionUtils.isEmpty(skuList)){
				return skuBOList;
			}
			for(SkuDAO skuDAO : skuList){
				SkuBO skuBO = new SkuBO();
				skuBO.setId(skuDAO.getId());
				skuBO.setName(skuDAO.getName());
				skuBO.setNum(skuDAO.getNum());
				skuBO.setAdverst(skuDAO.getAdverst());
				skuBO.setImgUrl(skuDAO.getImgUrl());
				skuBO.setInprice(skuDAO.getInprice());
				skuBO.setOutprice(skuDAO.getOutprice());
				skuBO.setYn(skuDAO.isYn());
				skuBOList.add(skuBO);
				
			}

			return skuBOList;
			
		} catch (Exception e) {
			logger.error("SkuManagerServiceImpl.querySkuListByPageNum查询商品信息时发生异常！！！", e);
		}
		return skuBOList;
		
	}
	
	public void setiSkuDAO(ISkuDAO iSkuDAO) {
		this.iSkuDAO = iSkuDAO;
	}

}
