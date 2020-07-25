package com.github.liuxun666.flashText

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/26
  * Email: liuzhao@66law.cn
  */
class Hit[T] extends Comparable[Hit[T]]{
  //Hit不匹配
  private val UNMATCH = 0x00000000
  //Hit完全匹配
  private val MATCH = 0x00000001
  //Hit前缀匹配
  private val PREFIX = 0x00000010
  //该HIT当前状态，默认未匹配
  private var hitState = UNMATCH

  //  //记录词典匹配过程中，当前匹配到的词典分支节点
  private var matchedDictionary: Dictionary[T] = _
  private var begin = 0
  private var end = 0

  /**
    * 判断是否完全匹配
    */
  def isMatch: Boolean = (this.hitState & MATCH) > 0

  /**
    * 设置为匹配状态
    */
  def setMatch(): Unit = {
    this.hitState = this.hitState | MATCH
  }
  /**
  * 判断是否是词的前缀
    */
  def isPrefix: Boolean = (this.hitState & PREFIX) > 0

  def setPrefix(): Unit = {
    this.hitState = this.hitState | PREFIX
  }

  /**
    * 判断是否是不匹配
    */
  def isUnmatch: Boolean = this.hitState == UNMATCH

  def setUnmatch(): Unit = {
    this.hitState = UNMATCH
  }

  def getMatchedDictionary: Dictionary[T] = matchedDictionary

  def setMatchedDictionary(matchedDictionary: Dictionary[T]): Unit = {
    this.matchedDictionary = matchedDictionary
  }

  def getBegin: Int = begin

  def setBegin(begin: Int): Unit = {
    this.begin = begin
  }

  def getEnd: Int = end

  def setEnd(end: Int): Unit = {
    this.end = end
  }

  override def compareTo(hit: Hit[T]): Int = {
    var tmp = this.hitState - hit.hitState
    if (tmp == 0) {
      tmp = this.getBegin - hit.getBegin
      if (tmp == 0) tmp = this.getEnd - hit.getEnd
    }
    tmp
  }
}
