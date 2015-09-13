/*
Navicat MySQL Data Transfer

Source Server         : testlocalhost
Source Server Version : 50517
Source Host           : localhost:3306
Source Database       : ms

Target Server Type    : MYSQL
Target Server Version : 50517
File Encoding         : 65001

Date: 2015-09-07 11:26:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ladder
-- ----------------------------
DROP TABLE IF EXISTS `ladder`;
CREATE TABLE `ladder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '阶梯元素主键',
  `type` int(11) DEFAULT NULL COMMENT '阶梯促销类型',
  `pricediscount` decimal(10,0) DEFAULT NULL COMMENT '阶梯促销价格为商品原始价格的折扣比',
  `numpercent` decimal(10,0) DEFAULT NULL COMMENT '阶梯促销数量占总商品的数量比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ladderpromotion
-- ----------------------------
DROP TABLE IF EXISTS `ladderpromotion`;
CREATE TABLE `ladderpromotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotionid` bigint(20) DEFAULT NULL COMMENT '促销id',
  `ladderid` bigint(20) DEFAULT NULL COMMENT '阶梯规则id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL,
  `orderid` bigint(20) DEFAULT NULL COMMENT '订单号',
  `pin` varchar(30) DEFAULT NULL COMMENT '用户名',
  `skuid` bigint(20) DEFAULT NULL COMMENT '商品id',
  `promotionID` bigint(20) DEFAULT NULL COMMENT '促销id',
  `ladderId` bigint(20) DEFAULT NULL COMMENT '促销阶梯id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `invalidate` datetime DEFAULT NULL COMMENT '过期时间',
  `address` varchar(100) DEFAULT NULL COMMENT '收货地址',
  `phone` int(11) DEFAULT NULL COMMENT '收货人手机',
  `name` varchar(20) DEFAULT NULL COMMENT '收货人姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for orderdelivery
-- ----------------------------
DROP TABLE IF EXISTS `orderdelivery`;
CREATE TABLE `orderdelivery` (
  `id` bigint(20) NOT NULL,
  `orderid` bigint(20) DEFAULT NULL COMMENT '订单号',
  `deliveryid` varchar(20) DEFAULT NULL COMMENT '快递单号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for orderstatus
-- ----------------------------
DROP TABLE IF EXISTS `orderstatus`;
CREATE TABLE `orderstatus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderid` bigint(20) DEFAULT NULL COMMENT ' 订单号',
  `status` tinyint(2) DEFAULT NULL COMMENT '订单转态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '促销id,主键',
  `skuid` bigint(20) DEFAULT NULL COMMENT '促销的商品id',
  `skunum` int(11) DEFAULT NULL COMMENT '促销的商品总数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for promotionsequence
-- ----------------------------
DROP TABLE IF EXISTS `promotionsequence`;
CREATE TABLE `promotionsequence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotionid` bigint(20) DEFAULT NULL COMMENT '促销id',
  `hasload` tinyint(1) DEFAULT NULL COMMENT '是否刷新到缓存',
  `nextorder` bigint(20) DEFAULT NULL COMMENT '下一个促销信息id',
  `startTime` datetime DEFAULT NULL COMMENT '促销开始时间',
  `endtime` datetime DEFAULT NULL COMMENT '促销结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sku
-- ----------------------------
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '商品名称',
  `adverst` varchar(100) DEFAULT NULL COMMENT '广告语',
  `inprice` decimal(10,0) DEFAULT NULL COMMENT '进货价',
  `outprice` decimal(10,0) DEFAULT NULL COMMENT '出货价',
  `yn` tinyint(1) DEFAULT NULL COMMENT '是否有效，1有效，0无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for userlogin
-- ----------------------------
DROP TABLE IF EXISTS `userlogin`;
CREATE TABLE `userlogin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(30) DEFAULT NULL COMMENT 'uuid随机生成唯一标号',
  `pin` varchar(30) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `pwd` varchar(64) DEFAULT NULL COMMENT ' 密码',
  `phone` int(11) DEFAULT NULL COMMENT '电话号码',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(2) DEFAULT NULL COMMENT '用户状态 1 正常，2 封号 3 恶意账号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
