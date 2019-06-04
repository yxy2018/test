package org.springside.examples.bootapi.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.XbRecordClassView;
import org.springside.examples.bootapi.domain.XbStudentRelationView;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface XbStudentRelationViewDao extends PagingAndSortingRepository<XbStudentRelationView, String>,JpaSpecificationExecutor<XbStudentRelationView> {
    @Query(value = "select count(1) from (" +
            " select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll1(String enrollDate1, String enrollDate2);
    @Query(value = "select count(1) from ( " +
            "select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `t`.`contact_phone` = ?3  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll2(String enrollDate1, String enrollDate2, String contactPhone);
    @Query(value = "select count(1) from ( " +
            "select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `t`.`student_name` = ?3  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll3(String enrollDate1, String enrollDate2,String studentName);
    @Query(value = "select count(1) from ( " +
            "select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `t`.`student_name` = ?3  " +
            "and `o`.`organ_id` = ?4  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll4(String enrollDate1, String enrollDate2,String studentName,String organId);
    @Query(value = "select count(1) from ( " +
            "select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `t`.`contact_phone` = ?3  " +
            "and `o`.`organ_id` = ?4  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll5(String enrollDate1, String enrollDate2,String contactPhone,String organId);
    @Query(value = "select count(1) from ( " +
            "select o.id " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `o`.`organ_id` = ?4  " +
            "and `o`.`enroll_date` >= ?1  " +
            "and `o`.`enroll_date` <= ?2  " +
            "group by `o`.`student_id`" +
            ") aa",nativeQuery = true)
    Long findAll6(String enrollDate1, String enrollDate2,String organId);

}
