package org.springside.examples.bootapi.api;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.websuites.core.response.IResult;
import com.websuites.core.response.Result;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springside.examples.bootapi.ToolUtils.DateUtil;
import org.springside.examples.bootapi.ToolUtils.HttpServletUtil;
import org.springside.examples.bootapi.ToolUtils.common.util.UtilTools;
import org.springside.examples.bootapi.domain.*;
import org.springside.examples.bootapi.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 教务中心-学员
 * Created by ZhangLei on 2018/12/15 0015
 */
@Controller
@RequestMapping(value="/jwcentercourse")
public class JwCenterCourseActivity {
    private static Logger logger = LoggerFactory.getLogger(AccountActivity.class);
    @Autowired
    public XbCourseService xbCourseService;
    @Autowired
    public XbSubjectService xbSubjectService;
    @Autowired
    public XbCourseTypeService xbCourseTypeService;
    @Autowired
    public OrgansService organsService;
    @Autowired
    public XbCoursePresetService xbCoursePresetService;
    /**
     * 跳转到课程列表
     * @return
     */
    @RequestMapping("/tocourse")
    public String toCourse(ModelMap model,
                           @RequestParam(required=false) String data,
                       Pageable pageable){
        Sort sort = new Sort(Sort.Direction.DESC, "xbCourse.createDate")
                .and(new Sort(Sort.Direction.DESC, "xbCourse.createTime"));
        Pageable findpage
                = new PageRequest(pageable.getPageNumber(), 10, sort);
        logger.info("跳转到课程列表主页data="+data);
        getXbCorusePresetListPage(model,findpage,data);
        Map<String,Object> searhtypeMap = new HashMap<>();
        List<XbCourseType> coursetypelist = xbCourseTypeService.findXbCourseTypeList(searhtypeMap);
        model.addAttribute("coursetypelist",coursetypelist);
        List<XbSubject> subjectlist = xbSubjectService.findXbSubjectList(searhtypeMap);
        model.addAttribute("subjectlist",subjectlist);
        Iterable<SysOrgans> organsList = organsService.getOrgansList();
        model.addAttribute("organsList",organsList);
        //查询开课数量
        Map<String,Object> tatemap = new HashMap<>();
        tatemap.put("EQ_xbCourse.state","0");
        List<XbCoursePreset> xbcstate = xbCoursePresetService.getXbCoursePresets(tatemap);
        model.addAttribute("xbcstatesize",xbcstate.size());
        return "course";
    }

    /**
     * 查询所有课时
     * @param model
     * @param pageable
     * @param data
     */
    private void getXbCorusePresetListPage(ModelMap model,
             Pageable pageable, String data){
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> searhMap = new HashMap<>();
        if(null!=data){
            resultMap = com.alibaba.fastjson.JSONObject.parseObject(data,searhMap.getClass());
        }
        //查询课程名
        String searhname = (String)resultMap.get("searhname");
        if(StringUtils.isNotEmpty(searhname)){
            searhMap.put("LIKE_xbCourse.courseName",searhname);
        }
        //查询校区
        String organId = (String)resultMap.get("organId");
        if(null==organId){
            organId = "0";
        }else if(!organId.equals("0")){
            searhMap.put("EQ_sysorgans.id",organId);
        }
        //查询课程类别
        String typeId = (String)resultMap.get("typeId");
        if(null==typeId){
            typeId = "0";
        }else if(!typeId.equals("0")){
            searhMap.put("EQ_xbCourse.xbcoursetype.id",typeId);
        }
        //授课模式
        String courtype = (String)resultMap.get("courtype");
        if(null==courtype){
            courtype = "-1";
        }else if(!courtype.equals("-1")){
            searhMap.put("EQ_xbCourse.type",courtype);
        }
        searhMap.put("EQ_deleteStatus","1");
        Page<XbCoursePreset> prelist =xbCoursePresetService.getXbCoursePresetList(pageable,searhMap);
        model.addAttribute("prelist",prelist);
        model.addAttribute("prelistsize",prelist.getSize());
        model.addAttribute("searhname",searhname);
        model.addAttribute("organId",organId);
        model.addAttribute("typeId",typeId);
        model.addAttribute("courtype",courtype);
    }

    /**
     * 跳转到新建课程
     * @return
     */
    @RequestMapping("/goaddcourse")
    public String goAddCourse(HttpServletRequest req,
            @RequestParam(value="courseId",defaultValue = "") String courseId,
            @RequestParam(value="presetid",defaultValue = "") String presetid,
            @RequestParam(value="type",defaultValue = "newadd") String type,
                          ModelMap model){
        logger.info("跳转到新建课程");
        //查询所有课程类别
        Map<String,Object> typesearmap = new HashMap<>();
        List<XbCourseType> coursetypelist = xbCourseTypeService.findXbCourseTypeList(typesearmap);
        model.addAttribute("coursetypelist",coursetypelist);
        //查询所有科目
        Map<String,Object> subjectsearmap = new HashMap<>();
        List<XbSubject> subjectlist = xbSubjectService.findXbSubjectList(subjectsearmap);
        model.addAttribute("subjectlist",subjectlist);
        //查询所有校区
        Map<String,Object> sorgsearmap = new HashMap<>();
        List<SysOrgans> sorganList = organsService.getOrgansListAll(sorgsearmap);
        model.addAttribute("sorganList",sorganList);
        model.addAttribute("xbCourse",null);
        model.addAttribute("pre",null);
        if(!presetid.equals("")){
            XbCourse xbc = xbCourseService.findById(courseId);
            model.addAttribute("xbCourse",xbc);
            XbCoursePreset pre = xbCoursePresetService.getXbCoursePreset(presetid);
            if(xbc.chargingMode.equals("0")){
                BigDecimal bde = new BigDecimal(pre.periodNum);
                pre.money=pre.money.multiply(bde);
            }
            model.addAttribute("pre",pre);

        }
        if(type.equals("update")){
            model.addAttribute("typename","修改课程");
        }else if(type.equals("add")){
            model.addAttribute("typename","新增定价标准");
        }else{
            model.addAttribute("typename","新建课程");
        }
        return "newLesson";
    }

    /**
     * 录入课程表和指定校区表
     * @param dataentity
     * @param resp
     */
    @RequestMapping("/savecourse")
    @SystemControllerLog(descrption = "新建课程",actionType = "1")
    public void saveCourse(@RequestParam String dataentity,
                           @RequestParam String typename,
                           HttpServletResponse resp){
        logger.info("新建课程");
        IResult rs = new Result();
        XbCourse xbcourse = com.alibaba.fastjson.JSONObject.parseObject(dataentity,XbCourse.class);

        com.alibaba.fastjson.JSONObject obj= com.alibaba.fastjson.JSONObject.parseObject(dataentity);//获取jsonobject对象
        List<Map<String,String>> list=(List)obj.getJSONArray("xbcoursepresetlist");//获取的结果集合转换成数组
        if(typename.equals("新建课程")){
             rs = designatedCampus(list,xbcourse);
        }else if(typename.equals("修改课程")){
            rs =  designatedCampusEdit(list,xbcourse);
        }else if(typename.equals("新增定价标准")){
            rs =  designatedCampusAdd(list,xbcourse);
        }else {
            rs.setErrorCode("0");
            rs.setErrorMessage("未获取到需要执行的保存类型,请联系管理员");

        }
        JSONObject jsonObject = new JSONObject();
        if(rs.isSuccessful()){
            jsonObject.put("status","0");
        }else{
            jsonObject.put("status","1");
        }
        jsonObject.put("msg", rs.getErrorMessage());
        HttpServletUtil.reponseWriter(jsonObject,resp);
        logger.info("新建结束");
    }

    /**
     * 编辑校区
     */
    private IResult designatedCampusEdit(List<Map<String,String >> list,XbCourse xbcourse){
        for(Map map:list){
            if( checkCourseTypeAndNameAndSchoolByOne(xbcourse.courseName,xbcourse.courseTypeId,(String)map.get("organIds"),xbcourse.id)){
                XbCourseType xbct = xbCourseTypeService.findXbCourseTypeById(xbcourse.courseTypeId);
                SysOrgans syso = organsService.findOrganbyId((String)map.get("organIds"));
                return UtilTools.makerErsResults("课程名称【"+xbcourse.courseName+"】 课程类别【"+xbct.getCourseTypeName()+"】 校区【"+syso.organName+"】已存在,不能重复");
            }
        }
        for(Map map:list){
            //开始存课程
            Date date  = new Date();
            XbCourse findxbc= xbCourseService.findById(xbcourse.id);
            xbcourse.setCreateDate(findxbc.getCreateDate());
            xbcourse.setCreateTime(findxbc.getCreateTime());
            xbcourse.deleteStatus = "1";
            XbCourse entity =  xbCourseService.saveXbCourse(xbcourse);
            //开始存课时
            XbCoursePreset pre = new XbCoursePreset();
            pre.setId((String)map.get("id"));
            pre.setCourseId(entity.getId());
            pre.deleteStatus ="1";
            pre.setOrganIds((String)map.get("organIds") );
            int periodNum = Integer.parseInt((String)map.get("periodNum"));
            double money = Double.parseDouble((String)map.get("money"));//金额
            pre.setPeriodNum(periodNum);//课时
            String charginMode = entity.chargingMode;
            if(charginMode.equals("0")){
                BigDecimal bdmoney = new BigDecimal(money);
                BigDecimal bdperiodNum = new BigDecimal(periodNum);
                pre.setMoney(bdmoney.divide(bdperiodNum, 4, RoundingMode.HALF_UP));
            }else if(charginMode.equals("2")){
                pre.setMoney(BigDecimal.valueOf(money));
            }else{
                pre.setMoney(BigDecimal.valueOf(money));
            }
            XbCoursePreset rs= xbCoursePresetService.saveXbCoursePreset(pre);
            if(StringUtils.isEmpty(rs.getId())){
                return UtilTools.makerErsResults("编辑课程信息失败");
            }
        }
        return UtilTools.makerSusResults("编辑课程信息成功");
    }
    /**
     * save课程
     */
    private IResult designatedCampus(List<Map<String,String >> list,XbCourse xbcourse){
        //开始存课程
        Date date  = new Date();
        xbcourse.setCreateDate(DateUtil.getDateStr(date));
        xbcourse.setCreateTime(DateUtil.getTimeStr(date));
        //验证
        for(Map map:list){
            if( checkCourseTypeAndNameAndSchoolByOne(xbcourse.courseName,xbcourse.courseTypeId,(String)map.get("organIds"),"")){
                XbCourseType xbct = xbCourseTypeService.findXbCourseTypeById(xbcourse.courseTypeId);
                SysOrgans syso = organsService.findOrganbyId((String)map.get("organIds"));
                return UtilTools.makerErsResults("课程名称【"+xbcourse.courseName+"】课程类别【"+xbct.getCourseTypeName()+"】校区【"+syso.organName+"】已存在,不能重复");
            }
        }
        XbCourse xbcnew = new XbCourse();
        BeanUtils.copyProperties(xbcourse,xbcnew);
        xbcnew.deleteStatus = "1";
        XbCourse entity =  xbCourseService.saveXbCourse(xbcnew);
        for(Map map:list){
            //开始存课时
            XbCoursePreset pre = new XbCoursePreset();
            pre.setCourseId(entity.getId());
            pre.deleteStatus ="1";
            pre.setOrganIds((String)map.get("organIds") );
            int periodNum = Integer.parseInt((String)map.get("periodNum"));
            double money = Double.parseDouble((String)map.get("money"));//金额
            pre.setPeriodNum(periodNum);//课时
            String charginMode = entity.chargingMode;
            if(charginMode.equals("0")){
                BigDecimal bdmoney = new BigDecimal(money);
                BigDecimal bdperiodNum = new BigDecimal(periodNum);
                pre.setMoney(bdmoney.divide(bdperiodNum, 4, RoundingMode.HALF_UP));
            }else if(charginMode.equals("2")){
                pre.setMoney(BigDecimal.valueOf(money));
            }else{
                pre.setMoney(BigDecimal.valueOf(money));
            }
            XbCoursePreset rs= xbCoursePresetService.saveXbCoursePreset(pre);
            if(StringUtils.isEmpty(rs.getId())){
                return UtilTools.makerErsResults("保存课程信息失败");
            }
        }
        return UtilTools.makerSusResults("保存课程信息成功");
    }
    /**
     * 新增定价标准
     */
    private IResult designatedCampusAdd(List<Map<String,String >> list,XbCourse xbcourse){
        for(Map map:list){
            //开始存课时
            XbCoursePreset pre = new XbCoursePreset();
            pre.setCourseId(xbcourse.getId());
            pre.deleteStatus ="1";
            pre.setOrganIds((String)map.get("organIds") );
            int periodNum = Integer.parseInt((String)map.get("periodNum"));
            double money = Double.parseDouble((String)map.get("money"));//金额
            pre.setPeriodNum(periodNum);//课时
            String charginMode = xbcourse.chargingMode;
            if(charginMode.equals("0")){
                BigDecimal bdmoney = new BigDecimal(money);
                BigDecimal bdperiodNum = new BigDecimal(periodNum);
                pre.setMoney(bdmoney.divide(bdperiodNum, 4, RoundingMode.HALF_UP));
            }else if(charginMode.equals("2")){
                pre.setMoney(BigDecimal.valueOf(money));
            }else{
                pre.setMoney(BigDecimal.valueOf(money));
            }
            XbCoursePreset rs= xbCoursePresetService.saveXbCoursePreset(pre);
            if(StringUtils.isEmpty(rs.getId())){
                return UtilTools.makerErsResults("新增定价标准信息失败");
            }
        }
        return UtilTools.makerSusResults("新增定价标准信息成功");
    }
    /**
     * 保存课程 验证 课程名 课程类别 校区唯一性
     * @param course_name
     * @param course_type_id
     * @param organ_ids
     * @return
     */
    private boolean checkCourseTypeAndNameAndSchoolByOne(String course_name,String course_type_id,String organ_ids,String course_id){
        List list = new ArrayList();
        Map<String,Object> searmap = new HashMap<>();
        searmap.put("EQ_xbCourse.courseName",course_name);
        searmap.put("EQ_xbCourse.courseTypeId",course_type_id);
        searmap.put("EQ_organIds",organ_ids);
        if(StringUtils.isNotEmpty(course_id)){
            searmap.put("NEQ_xbCourse.id",course_id);
        }
        List<XbCoursePreset> prelist =xbCoursePresetService.getXbCoursePresets(searmap);
        if(prelist!=null && prelist.size()>0){
            return true;
        }
        return false;
    }
    /**
     * String 转List
     * @param result
     * @param key
     * @param t
     * @return
     */
    public static List  parseArray(String result,String key,Class t){
        com.alibaba.fastjson.JSONObject obj= com.alibaba.fastjson.JSONObject.parseObject(result);//获取jsonobject对象
        com.alibaba.fastjson.JSONArray arr=obj.getJSONArray(key);//获取的结果集合转换成数组
        String js= com.alibaba.fastjson.JSONObject.toJSONString(arr, SerializerFeature.WriteClassName);//将array数组转换成字符串
        List  collection = com.alibaba.fastjson.JSONArray.parseArray(js, t);//把字符串转换成集合
            return collection;
    }
    @PostMapping("/savecoursetype")
    @SystemControllerLog(descrption = "新建课程类别",actionType = "1")
    public void saveCourseType(@RequestBody XbCourseType xbcoursetype,HttpServletResponse resp){
        logger.info("新建课程");
        xbcoursetype.deleteStatus = "1";
        XbCourseType entity =  xbCourseTypeService.saveXbCourseType(xbcoursetype);
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isEmpty(entity.getId())){
            jsonObject.put("msg", "新建课程类别成功");
            jsonObject.put("status","0");
            jsonObject.put("cousrtype",com.alibaba.fastjson.JSONObject.toJSON(entity));
        }else{
            jsonObject.put("status","1");
            jsonObject.put("msg", "新建课程类别失败");
        }
        logger.info("新建课程返回json参数="+jsonObject.toString());
        HttpServletUtil.reponseWriter(jsonObject,resp);
        logger.info("新建课程结束");
    }
    @PostMapping("/savesubject")
    @SystemControllerLog(descrption = "新建课程科目",actionType = "1")
    public void saveSubject(@RequestBody XbSubject xbsubject,HttpServletResponse resp){
        logger.info("新建课程");
        xbsubject.setState("0");
        XbSubject entity =  xbSubjectService.saveXbSubject(xbsubject);
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isEmpty(entity.getId())){
            jsonObject.put("msg", "新建科目成功");
            jsonObject.put("status","0");
            jsonObject.put("cousrtype",com.alibaba.fastjson.JSONObject.toJSON(entity));
        }else{
            jsonObject.put("status","1");
            jsonObject.put("msg", "新建科目失败");
        }
        logger.info("新建科目返回json参数="+jsonObject.toString());
        resp.setContentType("text/html;charset=UTF-8");
        try {
            resp.getWriter().println(jsonObject.toJSONString());
            resp.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("新建课程结束");
    }

    /**
     * 删除课程
     * @param resp
     */
    @RequestMapping("/removecourse")
    @SystemControllerLog(descrption = "删除课程",actionType = "3")
    public String removeCourse(@RequestParam(required=false) String id,HttpServletResponse resp,ModelMap model,
                               @PageableDefault(sort = {"xbCourse.createDate","xbCourse.createTime"},
                                       direction = Sort.Direction.DESC) Pageable pageable){
        logger.info("删除课程");
        try {
            Map<String,Object> xbCoursesearhMap = new HashMap<>();
            //逻辑删除课时
            XbCoursePreset xbp = xbCoursePresetService.getXbCoursePreset(id);
            xbp.deleteStatus = "0";
            xbCoursePresetService.saveXbCoursePreset(xbp);
            //查询该对应的课程还有多少课时 如果=1的话就把主表也逻辑删除
            Map<String,Object> getnumsearmap = new HashMap<>();
            getnumsearmap.put("EQ_courseId",xbp.courseId);
            getnumsearmap.put("EQ_deleteStatus","1");
            List<XbCoursePreset> list = xbCoursePresetService.getXbCoursePresets(getnumsearmap);
            if(list.size()<1){
                //逻辑删除课程
                XbCourse xbc = xbCourseService.findById(xbp.getCourseId());
                xbc.deleteStatus = "0";
                xbCourseService.saveXbCourse(xbc);
            }
            xbCoursesearhMap.put("EQ_deleteStatus","1");
            Page<XbCoursePreset> prelist = xbCoursePresetService.getXbCoursePresetList(pageable,xbCoursesearhMap);
            model.addAttribute("prelist",prelist);
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("删除课程结束");
        return "course::courselist";
    }
    /**
     * 课程上架下架
     * @param resp
     */
    @RequestMapping("/statecourse")
    public String stateCourse(@RequestParam(required=false) String id,@RequestParam String state,
           HttpServletResponse resp, ModelMap model,@PageableDefault(sort = {"xbCourse.createDate","xbCourse.createTime"},
            direction = Sort.Direction.DESC) Pageable pageable){
        logger.info("课程上架下架");
        try {
            Map<String,Object> xbCoursesearhMap = new HashMap<>();
            //逻辑删除课程
            XbCourse xbc = xbCourseService.findById(id);
            xbc.state = state;
            xbCourseService.saveXbCourse(xbc);
            xbCoursesearhMap.put("EQ_deleteStatus","1");
            Page<XbCoursePreset> prelist = xbCoursePresetService.getXbCoursePresetList(pageable,xbCoursesearhMap);
            model.addAttribute("prelist",prelist);
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("课程上架下架结束");
        return "course::courselist";
    }
    /**
     * 删除类别
     * @param id
     * @param resp
     * @param model
     * @return
     */
    @RequestMapping("/removecoursetype")
    @SystemControllerLog(descrption = "删除课程类别",actionType = "3")
    public String removeCourseType(@RequestParam String id,HttpServletResponse resp,ModelMap model){
        logger.info("删除课程类别");
        XbCourseType xbt = xbCourseTypeService.findXbCourseTypeById(id);
        xbt.deleteStatus = "0";
        xbCourseTypeService.saveXbCourseType(xbt);
        Map<String,Object> searmap = new HashMap<String,Object>();
        model.addAttribute("coursetypelist",xbCourseTypeService.findXbCourseTypeList(searmap));
        logger.info("删除课程类别结束");
        return "course::coursetypefralist";
    }

    /**
     * 删除科目
     * @param id
     * @param resp
     * @param model
     * @return
     */
    @RequestMapping("/removesubject")
    @SystemControllerLog(descrption = "删除课程科目",actionType = "3")
    public String removeSubject(@RequestParam String id,HttpServletResponse resp,ModelMap model){
       XbSubject xbs =  xbSubjectService.findById(id);
       xbs.deleteStatus = "0";
       xbSubjectService.saveXbSubject(xbs);
       Map<String,Object> searmap =  new HashMap<>();
       model.addAttribute("subjectlist",xbSubjectService.findXbSubjectList(searmap));
        logger.info("新建课程结束");
        return "course::subjectfralist";
    }
    @RequestMapping("/checkLayOrder")
    public void checkLayOrder(@RequestParam String layorder, HttpServletResponse resp){
        try {
            String code = "1000";
            XbCourseType xbcoursetype = xbCourseTypeService.checkTypeLayorder(Integer.parseInt(layorder));
            if(null!=xbcoursetype){
                code = "1001";
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status","1");
            jsonObject.put("code",code);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(jsonObject.toJSONString());
            resp.getWriter().close();
        } catch (IOException e) {
            logger.info(e.toString());
        }
    }
}
