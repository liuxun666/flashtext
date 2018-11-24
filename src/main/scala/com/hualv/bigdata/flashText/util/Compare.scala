package com.hualv.bigdata.flashText.util

import com.hualv.bigdata.flashText.Word

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/8/15
  * Email: liuzhao@66law.cn
  */
object Compare extends Enumeration{
  val BEGINFIRST: (Word, Word) => Int = beginFirst
  val ENDFIRST: (Word, Word) => Int = endFirst
  val LENGTHFIRST: (Word, Word) => Int = lengthFirst
  /**
    * 比较两个词：起始位置优先，再长度优先
    * @param t
    * @return
    */
  def beginFirst(t: Word, t1: Word): Int = {
    if(t.begin < t1.begin) 1
    else if(t.begin == t1.begin){
      if(t.end > t1.end) 1
      else if (t.end == t1.end) 0
      else -1
    }else -1
  }

  /**
    * 比较两个词：结束位置优先，再长度优先
    * @param t
    * @return
    */
  def endFirst(t: Word, t1: Word): Int = {
    if(t.end > t1.end) 1
    else if(t.end == t1.end){
      if(t.begin < t1.begin) 1
      else if( t.begin == t1.begin) 0
      else -1
    }else -1
  }

  /**
    * 比较两个词：长度优先，再起始位置优先
    * @param t
    * @return
    */
  def lengthFirst(t: Word, t1: Word): Int = {
    if(t.overlap(t1)){
      //重叠
      t.length.compareTo(t1.length)
    }else t.begin.compareTo(t1.begin)
  }
}
