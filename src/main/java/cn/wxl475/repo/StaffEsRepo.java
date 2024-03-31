package cn.wxl475.repo;

import cn.wxl475.pojo.base.Staff;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StaffEsRepo extends ElasticsearchRepository<Staff, Long> {
}
