package org.springside.examples.bootapi.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ZhangLei on 2019/1/24 0024
 */
@Entity
@Table(name="XB_RECORD_CLASS_VIEWS")
@NamedQuery(name="XbRecordClassViews.findAll",query = "SELECT t FROM XbRecordClassViews t")
public class XbRecordClassViews {
    @Id
    @Column(name="id")
    public String id;
    @Column(name="student_relation_id")
    public String studentRelationId;
    @Column(name="attend_id")
    public String attendId;
    @Column(name="orgid")
    public String orgid;
    @Column(name="organ_name")
    public String organName;
    @Column(name="class_name")
    public String className;
    @Column(name="course_name")
    public String courseName;
    @Column(name="teacher_id")
    public String teacherId;
    @Column(name="employee_name")
    public String employeeName;
    @Column(name="course_type_name")
    public String courseTypeNname;
    @Column(name="periodnum")
    public BigDecimal periodnum;
    @Column(name="establish_num")
    public Integer establishNum;
    @Column(name="class_id")
    public String classId;
    @Column(name="record_time")
    public Date recordTime;
    @Column(name="student_start")
    public Integer studentStart;
    @Transient
    public BigDecimal totalReceivable;
    @Transient
    public long studentCount;
    @Transient
    public BigDecimal sknum;
    @Transient
    public BigDecimal qjnum;
    @Transient
    public BigDecimal kknum;
    @Transient
    public BigDecimal bknum;
}
