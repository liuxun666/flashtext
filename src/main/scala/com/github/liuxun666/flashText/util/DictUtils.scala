package com.github.liuxun666.flashText.util

import java.util
import java.util.function.Consumer

import com.github.liuxun666.flashText.Dictionary


/**
  * 字典工具类，方便创建字典 <br>
  * Created by:
  * User: liuzhao
  * Date: 2018/7/27
  * Email: liuzhao@66law.cn
  */
object DictUtils {

  /**
    * 加载数组为词典
    * @param words 数组
    * @return Dictionary 字典
    */
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
