package org.springside.examples.bootapi.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.XbClassroom;

import java.util.List;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface XbClassroomDao extends PagingAndSortingRepository<XbClassroom, String>,JpaSpecificationExecutor<XbClassroom> {
    XbClassroom getById(String id);
    List<XbClassroom> getXbClassroomByDeleteStatus(String deleteStatus);
    XbClassroom findAllByClassroomNameAndDeleteStatus(String classroomName,String deleteStatus);
}
