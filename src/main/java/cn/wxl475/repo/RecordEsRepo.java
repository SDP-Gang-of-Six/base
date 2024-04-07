package cn.wxl475.repo;

import cn.wxl475.pojo.base.Record.Record;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RecordEsRepo extends ElasticsearchRepository<Record, Long> {
}
