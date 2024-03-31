package cn.wxl475.repo;

import cn.wxl475.pojo.base.Medicine;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MedicineEsRepo extends ElasticsearchRepository<Medicine,Long> {
}
