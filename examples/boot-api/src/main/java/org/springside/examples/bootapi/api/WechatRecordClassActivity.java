package org.springside.examples.bootapi.api;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springside.examples.bootapi.ToolUtils.DateUtil;
import org.springside.examples.bootapi.domain.*;
import org.springside.examples.bootapi.service.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/***
 * 记上课
 */
@Controller
@RequestMapping(value = "/recordClassWechat")
public class WechatRecordClassActivity {

	private static Logger logger = LoggerFactory.getLogger(WechatRecordClassActivity.class);
	@Autowired
	private XbStudentService studentService;
	@Autowired
	public XbCoursePresetService xbCoursePresetService;
	@Autowired
	private OrgansService organsService;
	@Autowired
	public XbCourseTypeService xbCourseTypeService;
	@Autowired
	public XbAttendClassService xbAttendClassService;
	@Autowired
	public XbStudentService xbStudentService;
	/*
	 * 查询按学员列表
	 * @return
	 */
	@RequestMapping("/accordingStudent")
	public String accordingStudent(@RequestParam(required = false) String data, ModelMap model, Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		if(null!=data){
			resultMap = com.alibaba.fastjson.JSONObject.parseObject(data,Map.class);
		}
		String classesName  = (String)resultMap.get("classesName");
		if(null!=classesName&&!classesName.equals("")){
			searhMap.put("LIKE_className",classesName);
		}
		Page<XbRecordClass> xbRecordClassPage = studentService.getRecordClassPage(pageable,searhMap);
		model.addAttribute("xbRecordClassPage",xbRecordClassPage);
		model.addAttribute("classesName",classesName);
		model.addAttribute("accordingcurrentzise",xbRecordClassPage.getSize());
		return "attendClass::accordingStudent";
	}

	/*
	 * 跳转到记上课列表getRecordClassList
	 * @return
	 */
	@RequestMapping("/getRecordClassList")
	public String getRecordClassList(@RequestParam(required = false) String data, ModelMap model,
									 @PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		Page<XbClass> classPage = studentService.getXbClassList(pageable,searhMap);
		Map<String,Object> searhtypeMap = new HashMap<>();
		model.addAttribute("classPage",classPage);
		return "wechat_timetableMore";
	}
	/*
	 * 跳转到记上课列表下拉加载.....
	 * @return
	 */
	@RequestMapping("/getRecordClassList_reloading")
	public String getRecordClassListReloading(@RequestParam(required = false) String studentname, ModelMap model,
									 @PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		if(StringUtils.isNotEmpty(studentname)){
			List<String> classidlist =  studentService.getClassIdFindByStudentName(studentname);
			if(classidlist.size()>0){
				searhMap.put("IN_id",studentService.getClassIdFindByStudentName(studentname));
			}else{
				searhMap.put("IN_id","1111111111222222222333333333311");//表示空
			}
		}
		Page<XbClass> classPage = studentService.getXbClassList(pageable,searhMap);
		Map<String,Object> searhtypeMap = new HashMap<>();
		model.addAttribute("classPage",classPage);
		model.addAttribute("studentname",studentname);
		return "wechat_timetableMore::WECHAT_RECORDCLASSLIST_SPANID_FRAGMENT";
	}
	/*
	 * 跳转到记上课列表下拉加载.....
	 * @return
	 */
	@RequestMapping("/getRecordClassList_reloading_one")
	public String getRecordClassListReloadingOne(@RequestParam(required = false) String studentname, ModelMap model,
											  @PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		if(StringUtils.isNotEmpty(studentname)){
			List<String> classidlist =  studentService.getClassIdFindByStudentName(studentname);
			if(classidlist.size()>0){
				searhMap.put("IN_id",studentService.getClassIdFindByStudentName(studentname));
			}else{
				searhMap.put("IN_id","1111111111222222222333333333311");//表示空
			}
		}
		Page<XbClass> classPage = studentService.getXbClassList(pageable,searhMap);
		Map<String,Object> searhtypeMap = new HashMap<>();
		model.addAttribute("classPage",classPage);
		model.addAttribute("studentname",studentname);
		return "wechat_timetableMore::WECHAT_RECORDCLASSLIST_SPANID_FRAGMENT_one";
	}
	/**
	 * 跳转到日程表
	 * @return
	 */
	@RequestMapping("/toTimeTable")
	public String toTimeTable(ModelMap model,@PageableDefault(value = 20) Pageable pageable){
		Map<String,Object> searhMap = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dda = sdf.format(new Date());
		searhMap.put("EQ_startDateTime",dda);//周日历
		Page<XbAttendClass> xbAttendList = xbAttendClassService.findXbAttendClassPageAll(pageable,searhMap);
		model.addAttribute("xbAttendList",xbAttendList);
		model.addAttribute("dda",dda);
		return "wechat_timetable";
	}
	/**
	 * 查询所有的排课信息
	 */
	@RequestMapping("/findXbAttendClassPageListAll")
	public String findXbAttendClassPageListAll(@RequestParam String dda,HttpServletResponse resp,
											   ModelMap model,@PageableDefault(value = 20) Pageable pageable,String data) throws Exception {
		Map<String,Object> resultMap = new HashMap<>();
		/*if(null!=data){
			resultMap = com.alibaba.fastjson.JSONObject.parseObject(data,resultMap.getClass());
		}*/
		//Map<String,Object> searhMap = parameterAssemblyByfindXbAttendClassPageAll(model,resultMap);
		Map<String,Object> searhMap = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
		Date date = sdf.parse(dda);
		SimpleDateFormat sdfsear = new SimpleDateFormat("yyyy-MM-dd");
		searhMap.put("EQ_startDateTime",sdfsear.format(date));//周日历
		Page<XbAttendClass> xbAttendList = xbAttendClassService.findXbAttendClassPageAll(pageable,searhMap);
		/*for(XbAttendClass xbattendclass : xbAttendList){
			Map<String,Object> searchParamsview = new HashMap<>();
			searchParamsview.put("EQ_classId",xbattendclass.classId);
			List<XbStudentRelationView> studentlist = studentService.getxbStudentRelationViewList(searchParamsview);
			xbattendclass.ydstudentnum = studentlist.size();
			xbattendclass.sdstudentnum = xbAttendClassService.getSdstudentnum(xbattendclass.classId,xbattendclass.startDateTime);
		}*/
		model.addAttribute("xbAttendList",xbAttendList);
		model.addAttribute("dda",dda);
		return "wechat_timetable::xbAttendListFra";
		//model.addAttribute("xbAttendListsize",xbAttendList.getSize());
	}
	private Map<String,Object> parameterAssemblyByfindXbAttendClassPageAll(ModelMap model,Map<String,Object> resultMap){
		Map<String,Object> searhMap = new HashMap<>();
		String type = (String)resultMap.get("type");//下拉查询
		if(StringUtils.isEmpty(type)){
			type = "class_search";
		}
		String searhname = (String)resultMap.get("searhname");//下拉查询
		if(StringUtils.isNotEmpty(searhname)){
			if(type.equals("class_search")){
				searhMap.put("LIKE_xbclass.className",searhname);
			}else if(type.equals("less_search")){
				searhMap.put("LIKE_xbclass.xbCourse.courseName",searhname);
			}else if(type.equals("teacher_search")){
				searhMap.put("LIKE_sysemployee.employeeName",searhname);
			}else if(type.equals("class_room_search")){
				searhMap.put("LIKE_xbclassroom.classroomName",searhname);
			}else if(type.equals("student_inclass_search")){
				Map<String,Object> xsrnmap = new HashMap<>();
				xsrnmap.put("LIKE_xbStudent.studentName",searhname);
				List<XbStudentRelationViewNew> nlist = xbStudentService.getxbStudentRelationViewNewList(xsrnmap);
				if(nlist.size()>0){
					XbStudentRelationViewNew xsr =nlist.get(0);
					searhMap.put("EQ_classId",xsr.classId);
				}

			}

		}
		//授课模式
		String courtype = (String)resultMap.get("courtype");
		if(null==courtype){
			courtype = "-1";
		}else if(!courtype.equals("-1")){
			searhMap.put("EQ_wayOfTeaching",courtype);
		}
		//授课模式
		String courseTypeId = (String)resultMap.get("courseTypeId");
		if(null==courseTypeId){
			courseTypeId = "0";
		}else if(!courseTypeId.equals("0")){
			searhMap.put("EQ_xbclass.xbCourse.xbcoursetype.id",courseTypeId);
		}
		//班级
		String classId_combobox = (String)resultMap.get("classId_combobox");
		if(null==classId_combobox){
			classId_combobox = "0";
		}else if(!classId_combobox.equals("0")){
			searhMap.put("EQ_classId",classId_combobox);
		}
		//课程
		String courseId_combobox = (String)resultMap.get("courseId_combobox");
		if(null==courseId_combobox){
			courseId_combobox = "0";
		}else if(!courseId_combobox.equals("0")){
			searhMap.put("EQ_teacherId",courseId_combobox);
		}
		//开课开始日期
		String startDateTimeBegin = (String)resultMap.get("startDateTimeBegin");
		if(StringUtils.isEmpty(startDateTimeBegin)){
			startDateTimeBegin = DateUtil.weekDateFirstDay();
			searhMap.put("GTE_startDateTime",startDateTimeBegin);
		}else{
			searhMap.put("GTE_startDateTime",startDateTimeBegin);
		}

		//开课结束日期
		String startDateTimeEnd = (String)resultMap.get("startDateTimeEnd");
		if(StringUtils.isEmpty(startDateTimeEnd)){
			startDateTimeEnd = DateUtil.weekDateLastDay();
			searhMap.put("LTE_startDateTime",startDateTimeEnd);
		}else{
			searhMap.put("LTE_startDateTime",startDateTimeEnd);
		}
		model.addAttribute("searhname",searhname);
		model.addAttribute("type",type);
		model.addAttribute("courtype",courtype);
		model.addAttribute("courseTypeId",courseTypeId);
		model.addAttribute("classId_combobox",classId_combobox);
		model.addAttribute("courseId_combobox",courseId_combobox);
		model.addAttribute("startDateTimeBegin",startDateTimeBegin);
		model.addAttribute("startDateTimeEnd",startDateTimeEnd);
        /*//授课模式
        String subjectId = (String)resultMap.get("subjectId");
        if(null==subjectId){
            subjectId = "0";
        }else if(!subjectId.equals("0")){
            searhMap.put("EQ_subjectId",subjectId);
        }*/
		// model.addAttribute("subjectId",subjectId);
		String sysorgId_combobox = (String)resultMap.get("sysorgId_combobox");
		if(null==sysorgId_combobox) {
			sysorgId_combobox = "0";
		}
		model.addAttribute("sysorgId_combobox",sysorgId_combobox);
		return searhMap;
	}
	/*
	 * 跳转到记上课记录列表
	 * @return
	 */
	@RequestMapping("/getRecordClassRecordListByClass")
	public String getRecordClassRecordListByClass(@RequestParam(required = false) String data, ModelMap model,
											@PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(3);
		list.add(4);
		searhMap.put("NEQINT_studentStart",list);
		Page<XbRecordClassView> recordLists = studentService.getXbRecordClassdViewtoList(pageable,searhMap);
		int totalElements = studentService.findRecordTotalCount();
		int size = pageable.getPageSize();
		int number = pageable.getPageNumber();
		int totalPages = totalElements/size;
		if(totalPages==0){
			number = 1;
		}
		totalPages = totalPages + 1;
		model.addAttribute("recordLists",recordLists);
        List<XbRecordClassView> recordList = studentService.getXbRecordClassdViewtoList(searhMap);
        BigDecimal totalPeriodnum = new BigDecimal("0");
        BigDecimal totalReceivables = new BigDecimal("0");
        for (int i = 0; i < recordList.size(); i++) {
            BigDecimal periodnum = recordList.get(i).periodnum;
            totalPeriodnum = totalPeriodnum.add(periodnum);
            String rece = recordList.get(i).totalReceivable;
            rece = rece.replaceAll(",","");
            totalReceivables = totalReceivables.add(new BigDecimal(rece));
        }
        model.addAttribute("totalPeriodnum",totalPeriodnum);
        model.addAttribute("totalReceivables",totalReceivables);
		return "wechat_classRecord";
	}
	@RequestMapping("/getRecordClassRecordListByClassReloding")
	public String getRecordClassRecordListByClassReloding(@RequestParam(required = false) String startDateTimeBegin,@RequestParam(required = false) String startDateTimeEnd, ModelMap model,
												  @PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if(null!=startDateTimeBegin&&!startDateTimeBegin.equals("")){
                startDateTimeBegin = startDateTimeBegin+" 00:00:00";
                searhMap.put("GTE_recordTime",sdf.parse(startDateTimeBegin));
            }
            if(null!=startDateTimeEnd&&!startDateTimeEnd.equals("")){
                startDateTimeEnd = startDateTimeEnd+" 23:59:59";
                searhMap.put("LTE_recordTime",sdf.parse(startDateTimeEnd));
            }
			List<Integer> list = new ArrayList<Integer>();
			list.add(1);
			list.add(3);
			list.add(4);
			searhMap.put("NEQINT_studentStart",list);
            Page<XbRecordClassView> recordLists = studentService.getXbRecordClassdViewtoList(pageable,searhMap);
            int totalElements = studentService.findRecordTotalCount();
            int size = pageable.getPageSize();
            int number = pageable.getPageNumber();
            int totalPages = totalElements/size;
            if(totalPages==0){
                number = 1;
            }
            totalPages = totalPages + 1;
            model.addAttribute("recordLists",recordLists);
            List<XbRecordClassView> recordList = studentService.getXbRecordClassdViewtoList(searhMap);
            BigDecimal totalPeriodnum = new BigDecimal("0");
            BigDecimal totalReceivables = new BigDecimal("0");
            for (int i = 0; i < recordList.size(); i++) {
                BigDecimal periodnum = recordList.get(i).periodnum;
                totalPeriodnum = totalPeriodnum.add(periodnum);
                String rece = recordList.get(i).totalReceivable;
                rece = rece.replaceAll(",","");
                totalReceivables = totalReceivables.add(new BigDecimal(rece));
            }
            model.addAttribute("totalPeriodnum",totalPeriodnum);
            model.addAttribute("totalReceivables",totalReceivables);
        }catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
        return "wechat_classRecord::classRecordFra";
    }
	@RequestMapping("/getRecordClassRecordListByClassReloding2")
	public String getRecordClassRecordListByClassReloding2(@RequestParam(required = false) String startDateTimeBegin,@RequestParam(required = false) String startDateTimeEnd, ModelMap model,
														  @PageableDefault(value = 10) Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if(null!=startDateTimeBegin&&!startDateTimeBegin.equals("")){
				startDateTimeBegin = startDateTimeBegin+" 00:00:00";
				searhMap.put("GTE_recordTime",sdf.parse(startDateTimeBegin));
			}
			if(null!=startDateTimeEnd&&!startDateTimeEnd.equals("")){
				startDateTimeEnd = startDateTimeEnd+" 23:59:59";
				searhMap.put("LTE_recordTime",sdf.parse(startDateTimeEnd));
			}
			List<Integer> list = new ArrayList<Integer>();
			list.add(1);
			list.add(3);
			list.add(4);
			searhMap.put("NEQINT_studentStart",list);
			Page<XbRecordClassView> recordLists = studentService.getXbRecordClassdViewtoList(pageable,searhMap);
			int totalElements = studentService.findRecordTotalCount();
			int size = pageable.getPageSize();
			int number = pageable.getPageNumber();
			int totalPages = totalElements/size;
			if(totalPages==0){
				number = 1;
			}
			totalPages = totalPages + 1;
			model.addAttribute("recordLists",recordLists);
			List<XbRecordClassView> recordList = studentService.getXbRecordClassdViewtoList(searhMap);
			BigDecimal totalPeriodnum = new BigDecimal("0");
			BigDecimal totalReceivables = new BigDecimal("0");
			for (int i = 0; i < recordList.size(); i++) {
				BigDecimal periodnum = recordList.get(i).periodnum;
				totalPeriodnum = totalPeriodnum.add(periodnum);
				String rece = recordList.get(i).totalReceivable;
				rece = rece.replaceAll(",","");
				totalReceivables = totalReceivables.add(new BigDecimal(rece));
			}
			model.addAttribute("totalPeriodnum",totalPeriodnum);
			model.addAttribute("totalReceivables",totalReceivables);
		}catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
		return "wechat_classRecord::classRecordFra2";
	}
	/*
	 * 跳转到记上课
	 * @return
	 */
	@RequestMapping("/classEdit")
	public String classEdit(@RequestParam(required = false) String classesId, ModelMap model,@PageableDefault(value = 1000) Pageable pageable){
		Map<String,Object> searhMap = new HashMap<>();
		if(null!=classesId){
			searhMap.put("LIKE_classId",classesId);
		}
		List<Integer> studentStartList = new ArrayList();
		studentStartList.add(1);
		studentStartList.add(4);
		studentStartList.add(3);
		searhMap.put("NEQINT_studentStart",studentStartList);
		XbClass classes = studentService.getXbClass(classesId);
		Page<XbStudentRelationViewNew> classPage = studentService.getXbStudentRelationViewNewList(pageable,searhMap);
		model.addAttribute("classPage",classPage);
		model.addAttribute("classes",classes);
		return "wechat_attendClass";
	}

	/**
	 * 查询学员记录列表，根据班级id和上课时间查询
	 * @param classId
	 * @param model
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/accordingStudentRecord")
	public String accordingStudentRecord(@RequestParam(required = false) String classId,
							@RequestParam(required = false) String recordTime, ModelMap model, Pageable pageable) {
		try {
			Map<String,Object> searhMap = new HashMap<>();
			/*if(null!=classId){
				searhMap.put("LIKE_classId",classId);
			}*/
			XbClass classes = studentService.getXbClass(classId);
			searhMap.put("EQ_attendId",classId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			searhMap.put("EQ_recordTime",sdf.parse(recordTime));
			Page<XbRecordClass> classPage = studentService.getRecordClassPage(pageable,searhMap);
			model.addAttribute("classPage",classPage);
			model.addAttribute("classes",classes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
		return "wechat_accordingStudent_record";
	}
	/*@PostMapping("/save/recordClass")
	public void recordClass(@RequestBody List<XbRecordClass> xbRecordClassList, HttpServletResponse resp, Pageable pageable) {
		try {
			for (XbRecordClass xbRecordClass : xbRecordClassList) {
				String studentId = xbRecordClass.studentId;
				BigDecimal deductPeriod = xbRecordClass.deductPeriod;
				String classId = xbRecordClass.classId;
				String organId = xbRecordClass.organId;
				String courseId = xbRecordClass.courseId;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				xbRecordClass.recordTime = sdf.parse(xbRecordClass.recordTimeTemp);
				Map<String,Object> searhMap = new HashMap<>();
				Map<String,Object> courseMap = new HashMap<>();
				if(null!=classId){
					searhMap.put("EQ_classId",classId);
					searhMap.put("EQ_studentId",studentId);
				}
				courseMap.put("EQ_courseId",courseId);
				courseMap.put("EQ_organIds",organId);
				BigDecimal money = new BigDecimal(0);
				List<XbCoursePreset> xbCoursePresetList = xbCoursePresetService.getXbCoursePresets(courseMap);
				if(xbCoursePresetList.size()>0){
					String chargingMode = xbCoursePresetList.get(0).xbCourse.chargingMode;
					money = xbCoursePresetList.get(0).money;
					if(chargingMode.equals("2")){
						money = money.divide(new BigDecimal(xbCoursePresetList.get(0).periodNum),2,BigDecimal.ROUND_HALF_UP);
					}
				}
				Page<XbStudentRelationViewNew> xbStudentRelations = studentService.getXbStudentRelationViewNewList(pageable,searhMap);
				String id = xbStudentRelations.getContent().get(0).id;
				XbStudentRelation xbStudentRelation = studentService.getXbStudentRelation(id);
						//Integer periodNum = xbStudentRelation.periodNum;
				BigDecimal bigDecimal = xbStudentRelation.periodNum;//剩余课时
				BigDecimal receivable = xbStudentRelation.receivable;//剩余学费
				//BigDecimal bigDecimal = new BigDecimal(periodNum.toString());
				money = deductPeriod.multiply(money);//课时*单课时金额=总扣除金额
				receivable = receivable.subtract(money);
				bigDecimal = bigDecimal.subtract(deductPeriod);
				//xbStudentRelation.periodNum = Integer.parseInt(bigDecimal.toString());
				xbStudentRelation.periodNum = bigDecimal;
				xbStudentRelation.receivable = receivable;
				studentService.saveXbStudentRelation(xbStudentRelation);
				studentService.saveXbRecordClass(xbRecordClass);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status","1");
			jsonObject.put("msg", "编辑成功");
			logger.info("编辑机构返回json参数="+jsonObject.toString());
			resp.setContentType("text/html;charset=UTF-8");
			resp.getWriter().println(jsonObject.toJSONString());
			resp.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
	}*/
	@PostMapping("/save/recordClass")
	@SystemControllerLog(descrption = "保存记上课",actionType = "1")
	public void recordClass(@RequestBody List<XbRecordClass> xbRecordClassList, HttpServletResponse resp, Pageable pageable) {
		try {
			for (XbRecordClass xbRecordClass : xbRecordClassList) {
				if(null!=xbRecordClass.state&&!xbRecordClass.state.equals("4")&&null!=xbRecordClass.deductPeriod){
					if(!(xbRecordClass.state.equals("0")&&xbRecordClass.deductPeriod.compareTo(new BigDecimal("0"))==0)){
						BigDecimal deductPeriod = xbRecordClass.deductPeriod;
						String studentRelationId = xbRecordClass.studentRelationId;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						xbRecordClass.recordTime = sdf.parse(xbRecordClass.recordTimeTemp);
						XbStudentRelation xbStudentRelation = studentService.getXbStudentRelation(studentRelationId);
						BigDecimal bigDecimal = xbStudentRelation.periodNum;
						BigDecimal totalPeriodNum = xbStudentRelation.totalPeriodNum;
						BigDecimal totalReceivable = xbStudentRelation.totalReceivable;
						if(bigDecimal.compareTo(new BigDecimal("0"))!=0){
							BigDecimal receivable = xbStudentRelation.receivable;
							BigDecimal money = totalReceivable.divide(totalPeriodNum,2,RoundingMode.HALF_UP).multiply(deductPeriod);
							receivable = receivable.subtract(money);
							bigDecimal = bigDecimal.subtract(deductPeriod);
							//xbStudentRelation.periodNum = Integer.parseInt(bigDecimal.toString());
							if(bigDecimal.compareTo(new BigDecimal("0"))<0){
								bigDecimal = new BigDecimal("0");
							}
							if(receivable.compareTo(new BigDecimal("0"))<0){
								receivable = new BigDecimal("0");
							}
							xbStudentRelation.periodNum = bigDecimal;
							xbStudentRelation.receivable = receivable;
							xbRecordClass.deductMoney = money;
							studentService.saveXbRecordClass(xbRecordClass);
							studentService.saveXbStudentRelation(xbStudentRelation);
						}
					}
				}
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status","1");
			jsonObject.put("msg", "记上课成功");
			logger.info("编辑机构返回json参数="+jsonObject.toString());
			resp.setContentType("text/html;charset=UTF-8");
			resp.getWriter().println(jsonObject.toJSONString());
			resp.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
	}
	/*
	 * 跳转到修改记上课
	 * @return
	 */
	@RequestMapping("/recordClassUpdate")
	public String clasrecordClassUpdatesEdit(@RequestParam(required = false) String classesId,@RequestParam(required = false) String recordTime, ModelMap model, Pageable pageable){
		try {
			Map<String, Object> searchParams = new HashMap<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			searchParams.put("EQ_attendId",classesId);
			searchParams.put("EQ_recordTime",sdf.parse(recordTime));
			Page<XbRecordClass> xbRecordClassPage = studentService.getRecordClassPage(pageable,searchParams);
			List<XbRecordClass> xbRecordClassList = xbRecordClassPage.getContent();
			XbClass classes = studentService.getXbClass(classesId);
			//List<XbStudentRelationViewNew> classPage = studentService.getXbRelationList(searhMap);
			model.addAttribute("classPage",xbRecordClassList);
			model.addAttribute("classSize",xbRecordClassList.size());
			model.addAttribute("classes",classes);
			model.addAttribute("recordTime",recordTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
		return "recordClassEdit";
	}
	/*
	 * 查询上课记录按班级
	 * @return
	 */
	@RequestMapping("/getRecordClassListByClass")
	public String getRecordClassListByClass(@RequestParam(required = false) String data, ModelMap model, Pageable pageable){
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> searhMap = new HashMap<>();
		if(null!=data){
			resultMap = com.alibaba.fastjson.JSONObject.parseObject(data,Map.class);
		}
		String classesName  = (String)resultMap.get("classesName");

		List recordLists = studentService.getXbRecordClassdtoList(pageable.getOffset(),pageable.getPageSize());
		int totalElements = studentService.findRecordTotalCount();
		int number = pageable.getPageNumber();
		int size = pageable.getPageSize();
		int totalPages = totalElements/size;
		if(totalPages==0){
			number = 1;
		}
		totalPages = totalPages + 1;
		model.addAttribute("recordLists",recordLists);
		model.addAttribute("recordcurrentzise",size);
		model.addAttribute("number",number);
		model.addAttribute("totalPages",totalPages);
		model.addAttribute("totalElements",totalElements);
		return "attendClass::accordingClass";
	}

}
