package com.hualv.bigdata


import com.hualv.bigdata.flashText.Analyzer
import com.hualv.bigdata.flashText.util.DictUtils

import scala.collection.mutable.ArrayBuffer

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/27
  * Email: liuzhao@66law.cn
  */
object test {
  def main(args: Array[String]): Unit = {
    val keywords = getArray()
    val dict = DictUtils.loadDict(keywords)
    Analyzer.analyze("泡泡泡怎么办法离婚", dict).foreach(f => println(f))
  }

  def getArray() = {
    ArrayBuffer("拒绝", "见" ,"办法" ,"怎么办","么办法" ,"怎么" ,"离婚")
  }

  def getMap() = {
    Map("泡" -> 11, "泡泡" -> 111,"拒绝" -> 0, "见" -> 1 ,"办法" -> 0 ,"怎么办" -> 2,"么办法" -> 2 ,"怎么" -> 0 ,"离婚" -> 0)
  }
}
