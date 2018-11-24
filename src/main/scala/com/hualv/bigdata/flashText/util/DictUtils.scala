package com.hualv.bigdata.flashText.util

import com.hualv.bigdata.flashText.Dictionary

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/27
  * Email: liuzhao@66law.cn
  */
object DictUtils {

  def loadDict(words: Array[String]) = {
    val dic = Dictionary(0.toChar)
    words.filter(!_.isEmpty).foreach(word => dic.addWord(word.trim))
    dic
  }

  def loadDict[T](words: Map[String, T]) = {
    val dic = Dictionary[T](0.toChar)
    dic.addWord(words)
    dic
  }

}
