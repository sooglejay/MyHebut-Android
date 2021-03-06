package com.myhebut.utils;

import com.myhebut.entity.Course;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HebutUtil {

    /**
     * 数字对应章节
     */
    public static Map<String, String> initSections(String subject) {
        Map<String, String> sectionMap = new LinkedHashMap<String, String>();
        if (subject.equals("0")) {
            sectionMap.put("0", "绪论");
            sectionMap.put("1", "哲学第一章");
            sectionMap.put("2", "哲学第二章");
            sectionMap.put("3", "哲学第三章");
            sectionMap.put("4", "政治经济学第四章");
            sectionMap.put("5", "政治经济学第五章");
            sectionMap.put("6", "科学社会主义第六章");
            sectionMap.put("7", "科学社会主义第七章");
        } else if (subject.equals("1")) {
            sectionMap.put("1", "马克思主义中国化两大理论成果");
            sectionMap.put("2", "新民主主义革命理论");
            sectionMap.put("3", "社会主义改造理论");
            sectionMap.put("4", "社会主义建设道路初步探索的理论成果");
            sectionMap.put("5", "建设中国特色社会主义总依据");
            sectionMap.put("6", "社会主义本质和总任务");
        }  else if(subject.equals("2")){
            sectionMap.put("0", "社会主义改革开放理论");
            sectionMap.put("1", "建设中国特色社会主义经济");
            sectionMap.put("2", "建设中国特色社会主义政治");
            sectionMap.put("3", "建设中国特色社会主义文化");
            sectionMap.put("4", "建设社会主义和谐社会");
            sectionMap.put("5", "建设社会主义生态文明");
            sectionMap.put("6", "实现祖国完全统一的理论");
            sectionMap.put("7", "中国特色社会主义外交和国际战略");
            sectionMap.put("8", "建设中国特色社会主义的根本目的和依靠力量理论");
            sectionMap.put("9", "中国特色社会主义领导核心理论");
        } else {
            sectionMap.put("0", "开篇的话");
            sectionMap.put("1", "综述 风云变幻的八十年");
            sectionMap.put("2", "第一章");
            sectionMap.put("3", "第二章");
            sectionMap.put("4", "第三章");
            sectionMap.put("5", "中篇综述");
            sectionMap.put("6", "第四章");
            sectionMap.put("7", "第五章");
            sectionMap.put("8", "第六章");
            sectionMap.put("9", "第七章");
        }
        return sectionMap;
    }


    /**
     * 获得当前时间
     */
    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        return time;
    }

    /**
     * 将数字时间转换为汉字
     */
    public static String parseDay(int day) {
        switch (day) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";

            default:
                return day + "";
        }
    }

    /**
     * 筛选今天的课程
     */
    public static List<Course> selectToday(List<Course> courses) {
        List<Course> todayCourses = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            String day = parseDay(course.getDay());
            // 获取今天的课程
            if (dateFormat.format(date).indexOf(day) != -1) {
                todayCourses.add(course);
            }
        }
        return todayCourses;
    }


    /**
     * 获得当前时间,用于保存文件
     */
    public static String getDate4Save(){
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String time = dateFormat.format(date);
        return time;
    }

}
