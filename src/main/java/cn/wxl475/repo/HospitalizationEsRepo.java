package cn.wxl475.repo;

import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HospitalizationEsRepo extends ElasticsearchRepository<Hospitalization, Long> {
}
