package base.test.dbsharding.client.datasource;

import java.util.Map;

import javax.sql.DataSource;

/**
 *
 *
 */
public interface MultipleDataSourcesService {

    /**
     * ��ȡ���е����Դ
     *
     * @return ���е����Դ�Ĳ��ɱ伯��
     */
    Map<String, DataSource> getDataSources();
}
