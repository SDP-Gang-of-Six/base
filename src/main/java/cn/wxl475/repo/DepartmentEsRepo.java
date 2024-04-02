package cn.wxl475.repo;

import cn.wxl475.pojo.base.department.Department;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DepartmentEsRepo extends ElasticsearchRepository<Department, Long> {
}
