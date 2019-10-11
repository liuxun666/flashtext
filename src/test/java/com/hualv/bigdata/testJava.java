package com.hualv.bigdata;

import com.hualv.bigdata.flashText.Analyzer;
import com.hualv.bigdata.flashText.Dictionary;
import com.hualv.bigdata.flashText.Word;
import com.hualv.bigdata.flashText.util.Compare;
import com.hualv.bigdata.flashText.util.DictUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author gg
 * @Email liuzhao@66law.cn
 * @Date 2019/10/11
 * @Version v0.1
 **/
public class testJava {
    public static void main(String[] args) {
        String[] keywords = new String[]{"拒绝", "办法", "怎么办", "么办法", "怎么", "离婚"};
        List<String> strings = Arrays.asList(keywords);
        Dictionary dict = DictUtils.loadDict(keywords);
        Word[] finded = Analyzer.analyze("怎么办法离婚", dict, true, Compare::BEGINFIRST);
        for (Word word : finded) {
            System.out.println(word);
        }
        Map<String, Integer> map = new HashMap();
        map.put("北京", 110000);
        map.put("成都", 510100);
        DictUtils.loadDict(strings);
        Dictionary<Integer> dict1 = DictUtils.loadDict(map);
        // 控制优先级的方法
        Word<Integer>[] words = Analyzer.analyze("明天飞北京", dict1, true, Compare::BEGINFIRST);
        for (Word<Integer> word : words) {
            System.out.println(word.info());
        }
    }
}
