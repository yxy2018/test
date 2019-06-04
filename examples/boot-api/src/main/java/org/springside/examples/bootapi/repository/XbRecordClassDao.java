package org.springside.examples.bootapi.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.XbRecordClass;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface XbRecordClassDao extends PagingAndSortingRepository<XbRecordClass, String>,JpaSpecificationExecutor<XbRecordClass> {
    @Query(value = " select x.attend_id \"attendId\",\n" +
            "c.class_name \"className\",\n" +
            "s.course_name \"courseName\",\n" +
            "e.employee_name \"employeeName\",\n" +
            "t.course_type_name \"courseTypeName\",\n" +
            "x.record_time \"recordTime\"\n" +
            ",count(x.deduct_period) as periodnum\n" +
            ",count(case when x.state='0' then 0 else null end) as sknum\n" +
            ",count(case when x.state='1' then 0 else null end) as qjnum\n" +
            ",count(case when x.state='2' then 0 else null end) as kknum\n" +
            ",count(case when x.state='3' then 0 else null end) as bknum\n" +
            " from xb_record_class x  \n" +
            "left join xb_class c on c.id = x.attend_id\n" +
            "left join xb_course s on s.id = c.course_id\n" +
            "left join sys_employee e on e.id = c.teacher_id\n" +
            "left join xb_course_type t on t.id = s.course_type_id\n" +
            " where x.delete_status = '1' group by x.attend_id,c.class_name,s.course_name,e.employee_name,t.course_type_name,x.record_time limit ?1,?2",nativeQuery = true)
    List findRecordLists(int page,int pagesize);


    @Query(value = " select count(*) from (select x.*  from xb_record_class x   GROUP BY x.attend_id,x.record_time)as a ",nativeQuery = true)
    int findRecordTotalCount();

    List<XbRecordClass> findByStudentId(String studentId);


    @Query(value = "SELECT a.* " +
            "FROM xb_record_class AS a, xb_student_relation AS b, xb_course AS c " +
            "WHERE a.student_relation_id = b.id " +
            "AND b.course_id = c.id " +
            "and a.delete_status = 1 " +
            "and a.student_id = ?1 " +
            "and c.course_type_id = ?2 " +
            "order by record_time desc",nativeQuery = true)
    List getRecordClassPage1(String studentId,String courseTypeId);
    @Query(value = "SELECT a.* " +
            "FROM xb_record_class AS a, xb_student_relation AS b " +
            "WHERE a.student_relation_id = b.id " +
            "and a.delete_status = 1 " +
            "and a.student_id = ?1 " +
            "and b.course_id = ?2 " +
            "order by a.record_time desc",nativeQuery = true)
    List getRecordClassPage2(String studentId,String courseId);



    @Query(value = " SELECT count(`c`.`student_id`) " +
            "FROM `xb_record_class` `c` " +
            "WHERE (`c`.`state` = '0' || `c`.`state` = '3') " +
            "AND `c`.`delete_status` = '1' " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    long findstudentCount(String attendId, Date recordTime);
    @Query(value = " SELECT count(`c`.`student_id`) " +
            "FROM `xb_record_class` `c` " +
            "WHERE `c`.`state` = '0' " +
            "AND `c`.`delete_status` = '1' " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    BigDecimal findSknum(String attendId, Date recordTime);
    @Query(value = " SELECT count(`c`.`student_id`) " +
            "FROM `xb_record_class` `c` " +
            "WHERE `c`.`state` = '1' " +
            "AND `c`.`delete_status` = '1' " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    BigDecimal findQjnum(String attendId, Date recordTime);

    @Query(value = " SELECT count(`c`.`student_id`) " +
            "FROM `xb_record_class` `c` " +
            "WHERE `c`.`state` = '2'  " +
            "AND `c`.`delete_status` = '1' " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    BigDecimal findKknum(String attendId, Date recordTime);
    @Query(value = " SELECT count(`c`.`student_id`) " +
            "FROM `xb_record_class` `c` " +
            "WHERE `c`.`state` = '3' " +
            "AND `c`.`delete_status` = '1' " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    BigDecimal findBknum(String attendId, Date recordTime);
    @Query(value = " SELECT sum(r.total_receivable/r.total_period_num*c.deduct_period) " +
            "FROM `xb_record_class` `c` LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id " +
            "WHERE  `c`.`delete_status` = '1' " +
            "AND r.student_start in ('0','2') " +
            "AND `c`.`attend_id` = ?1 " +
            "AND `c`.`record_time` = ?2 ",nativeQuery = true)
    BigDecimal findTotalReceivable(String attendId, Date recordTime);




    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "LEFT JOIN `sys_organs` `l` ON `d`.`organ_id` = `l`.`id`\n" +
            "LEFT JOIN `sys_employee` `e` ON `e`.`id` = `d`.`teacher_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "AND c.record_time >= ?1\n" +
            "AND r.student_start in ('0','2')\n" +
            "AND c.record_time <= ?2 ",nativeQuery = true)
    BigDecimal findAll1(String s1, String s2);
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "LEFT JOIN `sys_organs` `l` ON `d`.`organ_id` = `l`.`id`\n" +
            "LEFT JOIN `sys_employee` `e` ON `e`.`id` = `d`.`teacher_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "and e.employee_name =?3\n" +
            "AND r.student_start in ('0','2')\n" +
            "AND c.record_time >= ?1\n" +
            "AND c.record_time <= ?2 ",nativeQuery = true)
    BigDecimal findAll2(String s1, String s2,String s3);
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "LEFT JOIN `sys_organs` `l` ON `d`.`organ_id` = `l`.`id`\n" +
            "LEFT JOIN `sys_employee` `e` ON `e`.`id` = `d`.`teacher_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "and e.id =?3\n" +
            "AND r.student_start in ('0','2')\n" +
            "AND c.record_time >= ?1\n" +
            "AND c.record_time <= ?2 ",nativeQuery = true)
    BigDecimal findAll22(String s1, String s2,String s3);
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "LEFT JOIN `sys_organs` `l` ON `d`.`organ_id` = `l`.`id`\n" +
            "LEFT JOIN `sys_employee` `e` ON `e`.`id` = `d`.`teacher_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "and l.id = ?3\n" +
            "AND r.student_start in ('0','2')\n" +
            "AND c.record_time >= ?1\n" +
            "AND c.record_time <= ?2 ",nativeQuery = true)
    BigDecimal findAll3(String s1, String s2,String s3);
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "LEFT JOIN `sys_organs` `l` ON `d`.`organ_id` = `l`.`id`\n" +
            "LEFT JOIN `sys_employee` `e` ON `e`.`id` = `d`.`teacher_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "and l.id = ?3\n" +
            "and e.employee_name=?4\n" +
            "AND r.student_start in ('0','2')\n" +
            "AND c.record_time >= ?1\n" +
            "AND c.record_time <= ?2 ",nativeQuery = true)
    BigDecimal findAll4(String s1, String s2 ,String s3, String s4);
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "AND r.student_start in ('0','2')" ,nativeQuery = true)
    BigDecimal findAll5();
    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "AND c.record_time >= ?1\n" +
            "AND r.student_start in ('0','2')",nativeQuery = true)
    BigDecimal findAll6(String s1);

    @Query(value = " SELECT\n" +
            "\t\tsum(\n" +
            "\t\t\t(\n" +
            "\t\t\t\tr.total_receivable / r.total_period_num * c.deduct_period\n" +
            "\t\t\t)\n" +
            "\t\t)\n" +
            "FROM\n" +
            "\t`xb_record_class` `c`\n" +
            "LEFT JOIN xb_student_relation r ON r.id = c.student_relation_id\n" +
            "LEFT JOIN `xb_class` `d` ON `d`.`id` = `c`.`attend_id`\n" +
            "WHERE\n" +
            "\t`c`.`delete_status` = '1'\n" +
            "AND c.record_time <= ?2\n "+
            "AND r.student_start in ('0','2')",nativeQuery = true)
    BigDecimal findAll7(String s1);

}
