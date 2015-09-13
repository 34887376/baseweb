package com.ms.dao.base.dao;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import base.test.base.util.JsonUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/**
 * mongon操作数据库基础类
 * 
 * @author zhoushanjie
 * 
 */
public class BaseMongoDAO {

    private Logger logger = Logger.getLogger(BaseMongoDAO.class);

    private MongoTemplate mongoTemplate;

    // 异步写，带重试，超时时间500ms
    private static WriteConcern writeConcernWithTimeOut = new WriteConcern(1, 500, false, true);

    public boolean insert(BasicDBObject basicDBObject, String collectionName) {
        try {
            valideInsertParam(basicDBObject);
            DBCollection collection = getCollection(collectionName);
            WriteResult result = collection.insert(basicDBObject);
            return processResult(result);
        } catch (Exception e) {
            logger.error("BaseMongoDAO.insert插入数据时发生异常，入参collectionName=" + collectionName + ";  basicDBObject=" + basicDBObject.toString(), e);
        }
        return false;
    }

    public boolean batchInsert(List<BasicDBObject> basicDBObjectList, String collectionName) {
        try {
            List<DBObject> insertList = new ArrayList<DBObject>();
            for (BasicDBObject basicDBObject : basicDBObjectList) {
                valideInsertParam(basicDBObject);
                insertList.add(basicDBObject);
            }
            DBCollection collection = getCollection(collectionName);
            WriteResult result = collection.insert(insertList, writeConcernWithTimeOut);
            return processResult(result);
        } catch (Exception e) {
            logger.error("BaseMongoDAO.batchInsert插入数据时发生异常，入参collectionName=" + collectionName + ";  basicDBObjectList=" + basicDBObjectList, e);
        }
        return false;
    }

    /**
     * 
     * //and、or多条件联合条件样例
     * //age条件(and条件)
     * BasicDBList condList = new BasicDBList();
     * BasicDBObject cond = new BasicDBObject();
     * cond.put("$gt",0);
     * cond.put("$lte",40);
     * BasicDBObject composeCod = new BasicDBObject();
     * composeCod.put("age", cond);
     * condList.add(composeCod);
     * 
     * //name条件
     * BasicDBObject nameCond = new BasicDBObject();
     * nameCond.put("name", "name");
     * condList.add(nameCond);
     * 
     * //查询条件组合（or条件）
     * BasicDBObject searchCond = new BasicDBObject();
     * searchCond.put("$or", condList);
     * 
     * 
     * 
     * @param conditionDBObject
     * @param targetDBObject
     * @param collectionName
     * @return
     */
    public boolean updateFirstRecord(BasicDBObject conditionDBObject, BasicDBObject targetDBObject, String collectionName) {
        try {
            DBCollection collection = getCollection(collectionName);
            WriteResult result = collection.update(conditionDBObject, targetDBObject, false, false, writeConcernWithTimeOut);
            return processResult(result);
        } catch (Exception e) {
            logger.error("BaseMongoDAO.updateFirstRecord更新数据时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + conditionDBObject
                    + ";  targetDBObject=" + targetDBObject, e);
        }
        return false;
    }

    /**
     * 
     * //and、or多条件联合条件样例
     * //age条件(and条件)
     * BasicDBList condList = new BasicDBList();
     * BasicDBObject cond = new BasicDBObject();
     * cond.put("$gt",0);
     * cond.put("$lte",40);
     * BasicDBObject composeCod = new BasicDBObject();
     * composeCod.put("age", cond);
     * condList.add(composeCod);
     * 
     * //name条件
     * BasicDBObject nameCond = new BasicDBObject();
     * nameCond.put("name", "name");
     * condList.add(nameCond);
     * 
     * //查询条件组合（or条件）
     * BasicDBObject searchCond = new BasicDBObject();
     * searchCond.put("$or", condList);
     * 
     * 
     * @param conditionDBObject
     * @param targetDBObject
     * @param collectionName
     * @return
     */
    public boolean updateAllRecord(BasicDBObject conditionDBObject, BasicDBObject targetDBObject, String collectionName) {
        try {
            DBCollection collection = getCollection(collectionName);
            WriteResult result = collection.update(conditionDBObject, targetDBObject, false, true, writeConcernWithTimeOut);
            return processResult(result);
        } catch (Exception e) {
            logger.error(
                    "BaseMongoDAO.updateAllRecord更新数据时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + JSON.serialize(conditionDBObject)
                            + ";  targetDBObject=" + targetDBObject, e);
        }
        return false;
    }

    /**
     * 更新表中的某一个字段的值
     * 
     * @param conditionDBObject
     * @param targetDBObject
     * @param collectionName
     * @return
     */
    public boolean updateField(BasicDBObject conditionDBObject, BasicDBObject targetDBObject, String collectionName) {
        try {
            DBCollection collection = getCollection(collectionName);
            BasicDBObject condtion = new BasicDBObject();
            condtion.put("$set", targetDBObject);
            WriteResult result = collection.update(conditionDBObject, condtion, false, false, writeConcernWithTimeOut);
            return processResult(result);
        } catch (Exception e) {
            logger.error("BaseMongoDAO.updateFirstRecord更新数据时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + conditionDBObject
                    + ";  targetDBObject=" + targetDBObject, e);
        }
        return false;
    }

    /**
     * 查询记录
     * 
     * //and、or多条件联合条件样例
     * //age条件(and条件)
     * BasicDBList condList = new BasicDBList();
     * BasicDBObject cond = new BasicDBObject();
     * cond.put("$gt",0);
     * cond.put("$lte",40);
     * BasicDBObject composeCod = new BasicDBObject();
     * composeCod.put("age", cond);
     * condList.add(composeCod);
     * 
     * //name条件
     * BasicDBObject nameCond = new BasicDBObject();
     * nameCond.put("name", "name");
     * condList.add(nameCond);
     * 
     * //查询条件组合（or条件）
     * BasicDBObject searchCond = new BasicDBObject();
     * searchCond.put("$or", condList);
     * 
     * @param nameClass
     * @param conditionDBObject
     * @param collectionName
     * @return
     */
    public <E> List<E> queryRecord(Class<E> nameClass, BasicDBObject conditionDBObject, String collectionName) {

        List<E> resultList = new ArrayList<E>();
        try {
            DBCollection collection = getCollection(collectionName);
            DBCursor cursor = collection.find(conditionDBObject);

            while (cursor.hasNext()) {
                E result = JsonUtil.fromJson(cursor.next().toString(), nameClass);
                resultList.add(result);
            }
        } catch (Exception e) {
            logger.error("BaseMongoDAO.queryRecord查询数据时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + conditionDBObject + ";  nameClass="
                    + nameClass.getName(), e);
        }
        return resultList;
    }

    /**
     * 根据条件查询满足条件的记录数量conditionDBObject==null，则查询整个表的
     * 
     * //and、or多条件联合条件样例
     * //age条件(and条件)
     * BasicDBList condList = new BasicDBList();
     * BasicDBObject cond = new BasicDBObject();
     * cond.put("$gt",0);
     * cond.put("$lte",40);
     * BasicDBObject composeCod = new BasicDBObject();
     * composeCod.put("age", cond);
     * condList.add(composeCod);
     * 
     * //name条件
     * BasicDBObject nameCond = new BasicDBObject();
     * nameCond.put("name", "name");
     * condList.add(nameCond);
     * 
     * //查询条件组合（or条件）
     * BasicDBObject searchCond = new BasicDBObject();
     * searchCond.put("$or", condList);
     * 
     * 
     * @param conditionDBObject
     * @param collectionName
     * @return
     */
    public int countRecord(BasicDBObject conditionDBObject, String collectionName) {

        int recordNum = 0;
        try {
            DBCollection collection = getCollection(collectionName);

            if (conditionDBObject == null) {
                recordNum = collection.find().count();
            } else {
                recordNum = collection.find(conditionDBObject).count();
            }
        } catch (Exception e) {
            logger.error("BaseMongoDAO.countRecord查询数据条数时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + conditionDBObject, e);
        }
        return recordNum;
    }

    public boolean delRecord(BasicDBObject conditionDBObject, String collectionName) {
        try {
            DBCollection collection = getCollection(collectionName);
            WriteResult result = collection.remove(conditionDBObject, writeConcernWithTimeOut);
            return processResult(result);
        } catch (Exception e) {
            logger.error("BaseMongoDAO.delRecord删除数据时发生异常，入参collectionName=" + collectionName + ";  conditionDBObject=" + conditionDBObject, e);
        }
        return false;

    }

    /**
     * 
     * 查找数据sex1={name1};sex1={name2};sex1={name3};sex2={name1};sex2={name2};sex2={name3};sex3={name1};sex3={name2};sex3={name3};
     * String map = "function(){emit(this.sex,this.name);}";
     * 处理成sex1={name1,name2,name3};sex2={name1,name2,name3};sex3={name1,name2,name3};
     * String reduce="function(key,values){ var ret={sex:key,names:values}; return ret;}";
     * 
     * @param collectionName
     * @param mapFunction
     * @param reduceFunction
     * @param targetCollection
     * @param keyNameOfReduceValue
     */
    public void mapReduce(String collectionName, String mapFunction, String reduceFunction, String targetCollection, String keyNameOfReduceValue) {
        try {
            DBCollection collection = getCollection(collectionName);
            // 执行mapReduce命令
            MapReduceOutput result = collection.mapReduce(mapFunction, reduceFunction, targetCollection, OutputType.INLINE, null);
            logger.info("BaseMongoDAO.mapReduce返回的原始数据值，  result=" + result.getCommandResult().toString());
            // 获取mapreduce的结果
            String reduceResult = result.getCommandResult().get("results").toString();
            logger.info("BaseMongoDAO.mapReduce返回的有效数据值，  reduceResult=" + reduceResult);

            // 返回结果key=要处理的属性；value=聚合后的结果
            Map<String, String> mapReduceResult = new HashMap<String, String>();
            // 将返回结果用json序列化格式为[{ "_id" : null , "value" : { "sex" : null , "names" : [ "test3" , "test3" , "test3"]}} , { "_id" : "man" , "value" : { "sex" :
            // "man" , "names" : [ "name" , "test6" , "test6" , "test3" , "test3" , "test3" , "test3" , "test3" , "test3" , "test3" , "test3" , "test3" ,
            // "test3" , "test3" , "test3"]}}]
            List<Map<String, Object>> reduceMap = JsonUtil.fromJson(reduceResult, List.class);
            // 序列化后的串[{_id=null, value={sex=null, num=3.0}}, {_id=man, value={sex=man, num=15.0}}, {_id=woman, value={sex=woman, num=5.0}}, {_id=women,
            // value={name=test2, num=1.0}}]
            System.out.println(reduceMap);
            logger.info("BaseMongoDAO.mapReduce方法json反序列化后的值，  jsonToClassReduceMap=" + reduceMap);
            // 遍历mapReduce的结果
            for (Map<String, Object> mapInfo : reduceMap) {
                // 定义reduce出的key对应的collection中的值
                String keyValue = "null";
                for (Entry<String, Object> mapInfoEntry : mapInfo.entrySet()) {
                    // System.out.println("111map key=====" +mapInfoEntry.getKey());
                    // 获取key对应的collection中的值
                    if (mapInfoEntry.getKey().equals("_id")) {
                        keyValue = processNull(mapInfoEntry.getValue());
                    }
                    /**
                     * 这个判断主要是为了集中处理value={sex=man, num=15.0}这类键值
                     * 绕开对key=NULL(即value为NULL)这类键值对的处理；例如{_id=null, value={sex=null, num=3.0}}中的_id和sex;如果为空则不做任何处理
                     * 因为下面使用的是MAP的强转，不适宜基础类型
                     */
                    // 如果reduce的值不为空，而且不是基础类型则继续解析map
                    if (mapInfoEntry.getValue() != null && !isPrimitiveType(mapInfoEntry.getValue().getClass().getName())) {
                        // System.out.println("in  in 111map value=====" +mapInfoEntry.getValue());
                        LinkedHashMap<Object, Object> realInfoMapList = (LinkedHashMap<Object, Object>) mapInfoEntry.getValue();
                        for (Entry e : realInfoMapList.entrySet()) {
                            if (keyNameOfReduceValue.equals(e.getKey().toString())) {
                                mapReduceResult.put(keyValue, e.getValue().toString());
                            }
                            // System.out.println(e.getKey());
                            // System.out.println(e.getValue());
                        }
                        // 值不为NULL的基础类型的处理逻辑
                    } else if (mapInfoEntry.getValue() != null) {
                        if (keyNameOfReduceValue.equals(mapInfoEntry.getKey().toString())) {
                            mapReduceResult.put(keyValue, mapInfoEntry.getValue().toString());
                        }
                        // 值为NULL的处理，只打印日志
                    } else {
                        logger.error("BaseMongoDAO.mapReduce方法，查到的结果为NULL，不做任何处理" + mapInfoEntry.getValue());
                    }
                }
            }

            // 输出reduce处理完成后的结果集
            for (Entry<String, String> mapInfo : mapReduceResult.entrySet()) {
                logger.info("BaseMongoDAO.mapReduce方法聚合后的结果key=" + mapInfo.getKey() + "======" + mapInfo.getValue());
            }
        } catch (Exception e) {
            logger.error("BaseMongoDAO.mapReduce聚合数据时发生异常，入参collectionName=" + collectionName + ";  mapFunction=" + mapFunction + ";  reduceFunction="
                    + reduceFunction + ";  targetCollection=" + targetCollection + ";  keyNameOfReduceValue=" + keyNameOfReduceValue, e);
        }
    }

    /**
     * 获取表"collectionName"的主键id
     * 
     * @param collectionName
     * @return
     */
    public Long getIndexIdFromMongo(String collectionName) {

        long indexNum = 0;
        try {
            // 主键存储表名：index
            DBCollection collection = getCollection("TableName.INDEX_TABLE_NAME");
            // 获取某个应用对象存储表的表名
            BasicDBObject oldDB = new BasicDBObject("name", collectionName);
            BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("id", 1));
            DBObject reFields = new BasicDBObject();
            reFields.put("id", "id");

            // 自增主键id
            DBObject result = collection.findAndModify(oldDB, reFields, null, false, update, true, true);
            if (result.get("id") == null) {
                throw new RuntimeException("获取主键id异常，请重试！！！");
            }
            indexNum = Long.parseLong(result.get("id").toString());
            if (indexNum <= 0) {
                throw new RuntimeException("获取主键id异常，请重试！！！");
            }
        } catch (Exception e) {
            logger.error("BaseMongoDAO.countRecord查询数据条数时发生异常，入参collectionName=" + collectionName, e);
        }
        return indexNum;
    }

    public BasicDBObject converToMGObject(Object obj, Class<?> nameOfClass) {
        BasicDBObject basicDBObject = new BasicDBObject();
        try {
            Field[] fileds = nameOfClass.getDeclaredFields();

            if (fileds != null && fileds.length > 1) {
                for (Field field : fileds) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (fieldName.equals("serialVersionUID")) {
                        continue;
                    }
                    String fieldValue = field.get(obj) == null ? null : field.get(obj).toString();
                    if (field.getType().getName().equals("java.util.Date")) {
                        if (fieldValue != null) {
                            fieldValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(field.get(obj));
                        }
                    }
                    if (fieldValue != null) {
                        basicDBObject.put(fieldName, fieldValue);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return basicDBObject;
    }

    /*
     * 用于复杂对象转换 e.g {key1:value1,key2:{key21:value21,key22:value22,...},key3:[value31,value32,...],...}
     * @param obj 需要转化的对象
     */
    public BasicDBObject converToMGObject(Object obj) {

        String jsonStr = JsonUtil.toJson(obj);

        BasicDBObject dbObject = (BasicDBObject) JSON.parse(jsonStr);

        return dbObject;
    }

    private DBCollection getCollection(String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new RuntimeException("表名不能为空！！！");
        }
        if (mongoTemplate == null) {
            throw new RuntimeException("数据库链接为空！！！");
        }
        DBCollection coll = mongoTemplate.getCollection(collectionName);
        return coll;
    }

    /**
     * 对插入的字段进行唯一性校验，校验的标准为主键
     * 
     * @param basicDBObject
     */
    private void valideInsertParam(BasicDBObject basicDBObject) {
        if (basicDBObject.get("_id") == null || StringUtils.isBlank(basicDBObject.get("_id").toString())) {
            throw new RuntimeException("插入记录的主键‘_id’字段不能为空！！！");
        }
    }

    /**
     * 对空处理
     * 
     * @param str
     * @return
     */
    private String processNull(Object str) {
        return str == null ? "null" : str.toString();
    }

    /**
     * 判断是否为原始类型
     * 
     * @param nameOfClass
     * @return
     */
    private boolean isPrimitiveType(String nameOfClass) {

        if (nameOfClass.equals("java.lang.Boolean")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Character")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Byte")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Short")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Integer")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Long")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Float")) {
            return true;
        } else if (nameOfClass.equals("java.lang.Double")) {
            return true;
        } else if (nameOfClass.equals("java.lang.String")) {
            return true;
        }
        return false;
    }

    /**
     * 处理插入、更新返回结果
     * 
     * @param result
     * @return
     */
    private boolean processResult(WriteResult result) {
        if (result == null)
            return false;
        Object a = result.getField("ok");
        if (a == null) {
            return false;
        }
        // 返回的为"1.0"
        Double pdoub = Double.parseDouble(a.toString());
        int pInt = pdoub.intValue();
        if (pInt >= 1) {
            return true;
        }
        return false;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
