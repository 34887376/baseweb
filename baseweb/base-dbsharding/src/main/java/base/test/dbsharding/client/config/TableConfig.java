package base.test.dbsharding.client.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import base.test.dbsharding.client.rule.RouteRule;


/**
 * 用于描述表的分拆情况，包括分库和分表方式
 *
 */
public class TableConfig implements InitializingBean {

    /**
     * 用于索引库的前缀
     */
    private String dbIndexPrefix = "";

    /**
     * 用于索引库的后缀
     */
    private String dbIndexSuffix = "";

    /**
     * 库的总数
     */
    private int dbCount;

    /**
     * 用于索引表的前缀
     */
    private String tbNamePrefix = "";

    /**
     * 用于索引表的后缀
     */
    private String tbNameSuffix = "";

    /**
     * 表的总数
     */
    private int tableCount;

    /**
     * 所有的库的索引数组
     */
    private String[] dbIndices;

    private String routeField;

    private String tabNameField;

    /**
     * 库名和表名是否需要通过填充达到名称长度的对齐
     */
    private boolean needPadToAlign = true;

    /**
     * 设置路由规则
     *
     * @param routeRule，路由规则
     */
    public void setRouteRule(RouteRule routeRule) {
        this.routeRule = routeRule;
    }

    public RouteRule getRouteRule() {
        return this.routeRule;
    }

    private RouteRule routeRule;

    /**
     * 获取库的索引数组
     *
     * @return String[], 索引的库的索引
     */
    public String[] getDbIndices() {
        return dbIndices;
    }

    public String[] getTableIndices() {
        return tableIndices;
    }

    /**
     * 所有的表的索引数组
     */
    private String[] tableIndices;

    public void setDbIndexPrefix(String dbIndexPrefix) {
        if(StringUtils.isBlank(dbIndexPrefix)) {
            this.dbIndexPrefix = "";
            return;
        }
        this.dbIndexPrefix = dbIndexPrefix;
    }


    public void setDbIndexSuffix(String dbIndexSuffix) {
        if(StringUtils.isBlank(dbIndexSuffix)) {
            this.dbIndexSuffix = "";
            return;
        }
        this.dbIndexSuffix = dbIndexSuffix;
    }

    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public void setTbNamePrefix(String tbNamePrefix) {
        if(StringUtils.isBlank(tbNamePrefix)) {
            this.tbNamePrefix = "";
            return;
        }
        this.tbNamePrefix = tbNamePrefix;
    }

    public void setTbNameSuffix(String tbNameSuffix) {
        if(StringUtils.isBlank(tbNameSuffix)) {
            this.tbNameSuffix = "";
        }
        this.tbNameSuffix = tbNameSuffix;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public String getRouteField() {
        return routeField;
    }

    public void setRouteField(String routeField) {
        this.routeField = routeField;
    }

    public String getTabNameField() {
        return tabNameField;
    }

    public void setTabNameField(String tabNameField) {
        this.tabNameField = tabNameField;
    }

    /**
     * 设置是否需要对库名和表名进行长度对齐.<br/>
     * 当库或表的个数只是一位数时,isNeedPadToAlign为true和false的意义一样;<br/>
     * 若库或表的个数超过一位数,eg:user被拆分成16个库256张表,isNeedPadToAlign为true时库名为user00-user15,表名为user_000-user_255;<br/>
     * isNeedPadToAlign为false时库名为user0-user15,表名为user_0-user_255.<br/>
     *
     * @param isNeedPadToAlign 库名或表名的索引的长度是否需要对齐到库或表的总数的位数.
     */
    public void setNeedPadToAlign(boolean isNeedPadToAlign) {
        this.needPadToAlign = isNeedPadToAlign;
    }

    public void afterPropertiesSet() throws Exception {
        if (dbCount <= 0) {
            throw new IllegalStateException("illegal dbCount,it must be >0!");
        }
        if (tableCount <= 0) {
            throw new IllegalStateException("illegal tableCount,it must be>0!");
        }
        if (routeRule == null) {
            throw new IllegalArgumentException("routeRule cann't be null!");
        }
        dbIndices = createIndices(dbIndexPrefix, dbIndexSuffix, dbCount);
        tableIndices = createIndices(tbNamePrefix, tbNameSuffix, tableCount);
    }

    /**
     * <p>创建索引数组，根据count的位数来填充0，由prefix+填充后的串+suffix<br/><br/>
     * example1: prefix="db_",suffix=null,count=10<br/>
     * return:["db_00","db_01","db_02"..."db_09"]<br/><br/>
     * example2: prefix=null,suffix="_node",count=4<br/>
     * return:["0_node","1_node","2_node","3_node"]
     * </p>
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @param count  总的索引条数
     * @return 索引数组new String[count]
     */
    private String[] createIndices(String prefix, String suffix, int count) {
        String[] indices = new String[count];
        int indexNumberSize = String.valueOf(count).length();
        for (int i = 0; i < count; i++) {
            String index = String.valueOf(i);
            if(needPadToAlign) {
                int padCount = indexNumberSize - index.length();
                while (padCount-- > 0) {
                    index = "0" + index;
                }
            }
            indices[i] = prefix + index + suffix;
        }
        return indices;
    }
}
