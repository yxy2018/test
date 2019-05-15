package org.springside.examples.bootapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springside.examples.bootapi.ToolUtils.HttpServletUtil;
import org.springside.examples.bootapi.ToolUtils.common.modules.persistence.DynamicSpecifications;
import org.springside.examples.bootapi.ToolUtils.common.modules.persistence.SearchFilter;
import org.springside.examples.bootapi.domain.*;
import org.springside.examples.bootapi.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Service
public class XbStudentService {

	private static Logger logger = LoggerFactory.getLogger(XbStudentService.class);
	@Autowired
	private XbStudentDao studentDao;
	@Autowired
	private XbStudentRelationDao xbStudentRelationDao;
	@Autowired
	private XbClassroomDao xbClassroomDao;
	@Autowired
	private XbClassDao xbClassDao;
	@Autowired
	private XbRecordClassDao xbRecordClassDao;
	@Autowired
	private XbRecordClassViewDao xbRecordClassViewDao;
	@Autowired
	private XbSupplementFeeDao xbSupplementFeeDao;
	@Autowired
	private XbClassViewDao xbClassViewDao;
	@Autowired
	private XbStudentRelationViewDao xbStudentRelationViewDao;
	@Autowired
	private XbStudentRelationViewNewDao xbStudentRelationViewNewDaoDao;

	@Transactional
	public Page<XbSupplementFee>  getXbSupplementFeeList(Pageable pageable, Map<String, Object> searchParams) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		if(!"超级管理员".equals(sysEmployee.sysRole.roleName)&&!"总校教务".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_xbStudent.organId",sysEmployee.organId);
		}
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbSupplementFee> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbSupplementFee.class);
		return xbSupplementFeeDao.findAll(spec,pageable);
	}

	@Transactional
	public List<XbSupplementFee>  getXbSupplementFeeList(Map<String, Object> searchParams) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		if(!"超级管理员".equals(sysEmployee.sysRole.roleName)&&!"总校教务".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_xbStudent.organId",sysEmployee.organId);
		}
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbSupplementFee> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbSupplementFee.class);
		return xbSupplementFeeDao.findAll(spec);
	}

	@Transactional
	public XbSupplementFee  getXbSupplementFee(String id) {
		return xbSupplementFeeDao.findOne(id);
	}

	public List<String> getClassIdFindByStudentName(String studentname){
		return xbStudentRelationDao.findAllStudentListByStudentName(studentname);
	}

    public List<XbStudentRelation> getxbStudentRelationList(Map<String, Object> searchParams){
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        searchParams.put("EQ_xbStudent.deleteStatus","1");
        Specification<XbStudentRelation> spec = DynamicSpecifications.bySearchFilter(
                filters.values(), XbStudentRelation.class);
        return xbStudentRelationDao.findAll(spec);
    }

	public List<XbStudentRelationView> getxbStudentRelationViewList(Map<String, Object> searchParams){
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		searchParams.put("EQ_xbStudent.deleteStatus","1");
		Specification<XbStudentRelationView> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudentRelationView.class);
		return xbStudentRelationViewDao.findAll(spec);
	}
	public List getAllStudentList(Date enroll_date){
		return xbStudentRelationDao.findAllStudentList(enroll_date);
	}
	public List getAllStudentListStartAndEnd(Date enroll_date,Date enrollEnd){
		return xbStudentRelationDao.findAllStudentListStartAndEnd(enroll_date,enrollEnd);
	}
	public List getAllStudentListNoDate(){
		return xbStudentRelationDao.findAllStudentListNoDate();
	}
	@Transactional
	public XbSupplementFee saveXbSupplementFee(XbSupplementFee xbSupplementFee) {
		xbSupplementFee.paymentDate = new Date();
		xbSupplementFee.orderNumber = UUID.randomUUID().toString();
		xbSupplementFee.paymentType = "0";
		return xbSupplementFeeDao.save(xbSupplementFee);
	}

	@Transactional
	public Page<XbStudent>  getXbStudentList(Pageable pageable, Map<String, Object> searchParams) {
		searchParams = HttpServletUtil.getRoleDate(searchParams);
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbStudent> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudent.class);
		return studentDao.findAll(spec,pageable);
	}

	@Transactional
	public Page<XbStudentRelationViewNew>  getXbStudentRelationViewNewList(Pageable pageable, Map<String, Object> searchParams) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		if(!"超级管理员".equals(sysEmployee.sysRole.roleName)&&!"总校教务".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_organId",sysEmployee.organId);
		}
		if("教师".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_teacherId",sysEmployee.id);
		}
		searchParams.put("EQ_xbStudent.deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbStudentRelationViewNew> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudentRelationViewNew.class);
		return xbStudentRelationViewNewDaoDao.findAll(spec,pageable);
	}
	public List<XbStudentRelationViewNew> getxbStudentRelationViewNewList(Map<String, Object> searchParams){
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		searchParams.put("EQ_xbStudent.deleteStatus","1");
		Specification<XbStudentRelationViewNew> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudentRelationViewNew.class);
		return xbStudentRelationViewNewDaoDao.findAll(spec);
	}
	@Transactional
	public XbStudentRelationViewNew  getXbStudentRelationViewNewByid(String id) {
		return xbStudentRelationViewNewDaoDao.findById(id);
	}
	@Transactional
	public List<XbStudentRelationViewNew>  getXbRelationList(Map<String, Object> searchParams) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		if(!"超级管理员".equals(sysEmployee.sysRole.roleName)&&!"总校教务".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_organId",sysEmployee.organId);
		}
		if("教师".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_teacherId",sysEmployee.id);
		}
		searchParams.put("EQ_xbStudent.deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbStudentRelationViewNew> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudentRelationViewNew.class);
		return xbStudentRelationViewNewDaoDao.findAll(spec);
	}

	@Transactional
	public Long findAllDataByClassCount(String classId) {
		return xbStudentRelationDao.findAllDataByClassCount(classId);
	}

	@Transactional
	public XbStudentRelationViewNew getXbStudentRelationView(String id) {
		return xbStudentRelationViewNewDaoDao.findOne(id);
	}

	@Transactional
	public XbStudentRelation getXbStudentRelation(String id) {
		return xbStudentRelationDao.findOne(id);
	}

	@Transactional
	public XbStudent saveXbStudent(XbStudent student) {
		return studentDao.save(student);
	}

	@Transactional
	public XbStudentRelation saveXbStudentRelation(XbStudentRelation studentRelation) {
		studentRelation.enrollDate = new Date();
		return xbStudentRelationDao.save(studentRelation);
	}

	@Transactional
	public void deleteXbStudent(String id) {
        XbStudent xbStudent = studentDao.findOne(id);
        xbStudent.deleteStatus = "0";
		List<XbSupplementFee> xbSupplementFeeList = xbSupplementFeeDao.findByStudentId(id);
		xbSupplementFeeDao.delete(xbSupplementFeeList);
		List<XbStudentRelation> xbStudentRelationList = xbStudentRelationDao.findByStudentId(id);
		xbStudentRelationDao.delete(xbStudentRelationList);
		List<XbRecordClass> xbRecordClassList = xbRecordClassDao.findByStudentId(id);
		xbRecordClassDao.delete(xbRecordClassList);
		studentDao.save(xbStudent);
	}

	@Transactional
	public XbStudent getXbStudent(String id) {
		return studentDao.findOne(id);
	}

	@Transactional
	public XbStudent getXbStudents(String id) {
		return studentDao.findAllById(id);
	}

	@Transactional
	public Page<XbClassroom>  getXbClassroomList(Pageable pageable,Map<String, Object> searchParams) {
		searchParams = HttpServletUtil.getRoleDate(searchParams);
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbClassroom> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbClassroom.class);
		return xbClassroomDao.findAll(spec,pageable);
	}

	@Transactional
	public Page<XbClass>  getXbClassList(Pageable pageable,Map<String, Object> searchParams) {
		searchParams = HttpServletUtil.getRoleDate(searchParams);
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbClass> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbClass.class);
		return xbClassDao.findAll(spec,pageable);
	}
	@Transactional
	public List<XbClassroom> findXbClassRoomListAll(){
		return xbClassroomDao.getXbClassroomByDeleteStatus("1");
	}
	@Transactional
	public List<XbClass> findXbClassListAll(Map<String, Object> searchParams){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		if(!"超级管理员".equals(sysEmployee.sysRole.roleName)&&!"总校教务".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_organId",sysEmployee.organId);
		}
		if("教师".equals(sysEmployee.sysRole.roleName)){
			searchParams.put("EQ_teacher.id",sysEmployee.id);
		}
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbClass> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbClass.class);
		return xbClassDao.findAll(spec);
	}
	@Transactional
	public List<XbClass> getXBclassListAll(Map<String, Object> searchParams) {
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbClass> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbClass.class);
		return xbClassDao.findAll(spec);
	}
	@Transactional
	public List<XbClassView> getXBclassViewListAll(Map<String, Object> searchParams) {
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbClassView> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbClassView.class);
		return xbClassViewDao.findAll(spec);
	}
	@Transactional
	public XbClassroom saveXbClassroom(XbClassroom classroom) {
		classroom.deleteStatus = "1";
		return xbClassroomDao.save(classroom);
	}

	@Transactional
	public void delete(String id) {
		XbClassroom xbClassroom = xbClassroomDao.findOne(id);
		xbClassroom.deleteStatus = "0";
		xbClassroomDao.save(xbClassroom);
		//xbClassroomDao.delete(id);
	}

	@Transactional
	public XbClassroom getXbClassroom(String id) {
		return xbClassroomDao.findOne(id);
	}

	@Transactional
	public XbClass saveXbClass(XbClass classes) {
		classes.deleteStatus = "1";
		return xbClassDao.save(classes);
	}

	@Transactional
	public void deleteClass(String id) {
		XbClass xbClass = xbClassDao.findOne(id);
		xbClass.deleteStatus="0";
		xbClassDao.save(xbClass);
		//xbClassDao.delete(id);
	}

	@Transactional
	public String finishClass(String id) {
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQ_classId",id);
		searchParams.put("GT_receivable",new BigDecimal("0"));
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbStudentRelation> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbStudentRelation.class);
		List<XbStudentRelation> lists =  xbStudentRelationDao.findAll(spec);
		if(lists.size()>0){
			return "还有未完成的学员不允许结课!";
		}else{
			Map<String, Object> searchParamss = new HashMap<>();
			searchParams.put("EQ_classId",id);
			Map<String, SearchFilter> filterss = SearchFilter.parse(searchParams);
			Specification<XbStudentRelation> specs = DynamicSpecifications.bySearchFilter(
					filters.values(), XbStudentRelation.class);
			List<XbStudentRelation> listss =  xbStudentRelationDao.findAll(spec);
			for (int i = 0; i < listss.size(); i++) {
				listss.get(i).studentStart = 4;//结课
				xbStudentRelationDao.save(listss.get(i));
			}
		}
		XbClass xbClass = xbClassDao.findOne(id);
		xbClass.isEnd="0";//0同步结束
		xbClass.classEndDate = new Date();
		xbClassDao.save(xbClass);
		return "同步结束成功";
	}

	@Transactional
	public void finishCourse(String id) {
		XbStudentRelation xbStudentRelation = xbStudentRelationDao.findOne(id);
		xbStudentRelation.studentStart=4;//1结课
		xbStudentRelationDao.save(xbStudentRelation);
	}

	@Transactional
	public XbClass getXbClass(String id) {
		return xbClassDao.findOne(id);
	}
	@Transactional
	public XbClass getXbClassById(String id){
		return xbClassDao.getById(id);
	}

	@Transactional
	public XbClassroom getXbClassroomById(String id){
		return xbClassroomDao.getById(id);
	}

	@Transactional
	public XbRecordClass getxbRecordClass(String id) {
		return xbRecordClassDao.findOne(id);
	}

	@Transactional
	public XbRecordClass saveXbRecordClass(XbRecordClass xbRecordClass){
		xbRecordClass.deleteStatus = "1";
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		xbRecordClass.operationEmployee = sysEmployee.employeeName;
		xbRecordClass.operationTime = new Date();
		return xbRecordClassDao.save(xbRecordClass);
	}

	@Transactional
	public XbRecordClass deleteXbRecordClass(String id){
		XbRecordClass xbRecordClass = xbRecordClassDao.findOne(id);
		xbRecordClass.deleteStatus = "0";
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		SysEmployee sysEmployee = (SysEmployee)request.getSession().getAttribute("sysEmployee");
		xbRecordClass.operationEmployee = sysEmployee.employeeName;
		xbRecordClass.operationTime = new Date();
		return xbRecordClassDao.save(xbRecordClass);
	}

	@Transactional
	public XbClassroom checkClassroomName(String classroomName){
		return xbClassroomDao.findAllByClassroomNameAndDeleteStatus(classroomName,"1");
	}
	@Transactional
	public List<XbStudent> checkStudentName(String name,String contactPhone){
		return studentDao.findAllByStudentNameAndContactPhoneAndDeleteStatus(name,contactPhone,"1");
	}
	@Transactional
	public XbClass checkClassesName(String name,String organId,String courseId,String teacherId){
		return xbClassDao.findAllByClassNameAndOrganIdAndCourseIdAndDeleteStatusAndTeacherId(name,organId,courseId,"1",teacherId);
	}

	@Transactional
	public Page<XbRecordClass>  getRecordClassPage(Pageable pageable,Map<String, Object> searchParams) {
		searchParams.put("EQ_deleteStatus","1");
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbRecordClass> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbRecordClass.class);
		return xbRecordClassDao.findAll(spec,pageable);
	}

	@Transactional
	public List getXbRecordClassdtoList(int page,int pagesize){
		return xbRecordClassDao.findRecordLists(page,pagesize);
	}
	@Transactional
	public Page<XbRecordClassView> getXbRecordClassdViewtoList(Pageable pageable,Map<String, Object> searchParams){
		searchParams = HttpServletUtil.getRoleDateForXbRecordClassdView(searchParams);
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbRecordClassView> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbRecordClassView.class);
		return xbRecordClassViewDao.findAll(spec,pageable);
	}

	@Transactional
	public List<XbRecordClassView> getXbRecordClassdViewtoList(Map<String, Object> searchParams){
		searchParams = HttpServletUtil.getRoleDateForXbRecordClassdView(searchParams);
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<XbRecordClassView> spec = DynamicSpecifications.bySearchFilter(
				filters.values(), XbRecordClassView.class);
		return xbRecordClassViewDao.findAll(spec);
	}

	@Transactional
	public int findRecordTotalCount(){
		return xbRecordClassDao.findRecordTotalCount();
	}
	public List<XbClass> findByXbStudentRalationId(String id){
		return xbClassDao.findByXbStudentRalationId(id);
	}

}
