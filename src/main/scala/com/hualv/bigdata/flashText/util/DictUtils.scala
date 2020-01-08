package com.hualv.bigdata.flashText.util

import java.util
import java.util.function.Consumer

import com.hualv.bigdata.flashText.Dictionary


/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/27
  * Email: liuzhao@66law.cn
  */
object DictUtils {

  def loadDict(words: Array[String]) = {
    val dic = Dictionary[Null](0.toChar)
    words.filter(!_.isEmpty).foreach(word => dic.addWord(word.trim))
    dic
  }

  def loadDict(words: util.List[String]) = {
    val dic = Dictionary[Null](0.toChar)
    words.forEach(new Consumer[String] {
      override def accept(t: String): Unit = dic.addWord(t.trim)
    })
    dic
  }

  def loadDict[T](words: Map[String, T]) = {
    val dic = Dictionary[T](0.toChar)
    dic.addWord(words)
    dic
  }

  def loadDict[T](words: util.Map[String, T]) = {
    val dic = Dictionary[T](0.toChar)
    dic.addWord(words)
    dic
  }

}
