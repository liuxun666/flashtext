package com.hualv.bigdata.flashText.util

import com.hualv.bigdata.flashText.Dictionary

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/27
  * Email: liuzhao@66law.cn
  */
object DictUtils {

  def loadDict(words: Seq[String]) = {
    val dic = Dictionary(0.toChar)
    words.filter(!_.isEmpty).foreach(word => dic.addWord(word.trim))
    dic
  }
}
