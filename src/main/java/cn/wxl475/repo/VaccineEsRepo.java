package cn.wxl475.repo;

import cn.wxl475.pojo.base.vaccine.Vaccine;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VaccineEsRepo extends ElasticsearchRepository<Vaccine, Long> {
}
