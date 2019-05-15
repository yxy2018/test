package org.springside.examples.bootapi.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// JPA实体类的标识
@Entity
public class SysOrgans {

    // JPA 主键标识, 策略为由数据库生成主键
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator="idGenerator")
    public String id;
    @NotEmpty(message="名称不能为空")
    public String organName;
    public Integer organType;
    @NotEmpty(message="地址不能为空")
    public String adds;
    @NotEmpty(message="电话1不能为空")
    public String phone1;
    public String phone2;
    public Integer campusType;
    public String parentId;
    public Integer layOrder;
    public String deleteStatus;

    public SysOrgans() {

    }

    public SysOrgans(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
