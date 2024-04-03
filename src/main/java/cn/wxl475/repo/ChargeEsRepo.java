package cn.wxl475.repo;

import cn.wxl475.pojo.base.Charge.Charge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChargeEsRepo extends ElasticsearchRepository<Charge, Long> {
}
