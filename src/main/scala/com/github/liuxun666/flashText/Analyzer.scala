package com.github.liuxun666.flashText

import java.util.function.BiFunction

import com.github.liuxun666.flashText.util.Compare

import scala.collection.mutable

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/26
  * Email: liuzhao@66law.cn
  */
object Analyzer {

  /**
    * 查找一段为了本包含词典中哪些词，以及位置信息<br>
    * eg:<br>
    * val dict = DictUtils.loadDict(Seq("大家", "大家好"))<br>
    *   Analyzer.analyze("大家好", dict)) <br>
    * # return: Array(Word(大家好,0,2))<br>
    *   Analyzer.analyze("大家好", dict, false)) <br>
    * # return: Array(Word(大家,0,1), Word(大家好,0,2))<br>
    *
    * @param text        需要查找的文本
    * @param dictionary  词典
    * @param isSmart     是否智能匹配(默认：true)，true: 按起始位置和长度优先返回结果，不允许词之间重叠，false：返回最细粒度的结果
    * @param compareFunc 处理优先级的函数，默认是起始位置优先
    * @return
    */
  def analyze[T](text: String, dictionary: Dictionary[T], isSmart: Boolean, compareFunc: (Word[_], Word[_]) => Int): Array[Word[T]] = {
    implicit val aa = compareFunc
    val charArray = text.toCharArray
    var cursor = 0
    val words = mutable.ArrayBuffer[Word[T]]()
    val hits = mutable.Buffer[Hit[T]]()
    var hit: Hit[T] = null
    while (cursor < charArray.length) {
      //先处理所有前缀匹配的词
      while (hits.nonEmpty) {
        hit = hits.remove(0)
        if (hit.getEnd < charArray.length - 1) {
          hit = hit.getMatchedDictionary.`match`(charArray, hit.getEnd + 1, hit)
          if (hit.isMatch) {
            val w = Word(charArray.slice(hit.getBegin, hit.getEnd + 1).mkString, hit.getBegin, hit.getEnd + 1, hit.getMatchedDictionary.getValue())
            //如果智能匹配，并且当前词和上一个词重叠了，比较两个词的大小再决定取哪一个
            if (isSmart) {
              if (words.isEmpty) {
                words += w
              } else {
                //当前词和上一个词重叠了
                if (w.overlap(words.last)) {
                  //当前词大于上一个词，把上一个词删去，将新词加入, 否则不加入当前匹配到的词
                  if (w > words.last) {
                    words.remove(words.length - 1)
                    words += w
                  }
                } else words += w
              }
            } else {
              words += w
            }
          }
          //前缀匹配，加入前缀匹配列表
          if (hit.isPrefix) {
            hits += hit
          }
          if (hit.isUnmatch) {
            hit = null
          }
        }
      }
      //当前没有前缀匹配时，重新匹配
      hit = dictionary.`match`(charArray, cursor)
      if (hit.isMatch) {
        val w = Word(charArray.slice(hit.getBegin, hit.getEnd + 1).mkString, hit.getBegin, hit.getEnd + 1, hit.getMatchedDictionary.getValue())
        if (isSmart) {
          if (words.isEmpty) {
            words += w
          } else {
            //当前词和上一个词重叠了
            if (w.overlap(words.last)) {
              //当前词大于上一个词，把上一个词删去，将新词加入
              if (w > words.last) {
                words.remove(words.length - 1)
                words += w
              }
            } else words += w
          }
        } else {
          words += w
        }
      }
      if (hit.isPrefix) {
        //前缀匹配上
        hits += hit
      }
      if (hit.isUnmatch) {
        hit = null
      }
      cursor += 1
    }
    words.toArray
  }

  def analyze[T](text: String, dictionary: Dictionary[T], isSmart: Boolean): Array[Word[T]] = {
    analyze(text, dictionary, isSmart, Compare.BEGINFIRST)
  }

  def analyze[T](text: String, dictionary: Dictionary[T]): Array[Word[T]] = {
    analyze(text, dictionary, true, Compare.BEGINFIRST)
  }

  def analyze[T](text: String, dictionary: Dictionary[T], compareFunc: (Word[_], Word[_]) => Int): Array[Word[T]] = {
    analyze(text, dictionary, true, compareFunc)
  }

  def analyze[T](text: String, dictionary: Dictionary[T], isSmart: Boolean, compareFunc: BiFunction[Word[_], Word[_], Int]): Array[Word[T]] = {
    // convert java lambda function to scala function
    analyze(text, dictionary, isSmart, (w1, w2) => compareFunc(w1, w2))
  }
}

case class Word[T](word: String, begin: Int, end: Int, info: T = null)(implicit compareFunc: (Word[_], Word[_]) => Int) extends Ordered[Word[_]] {
  val length: Int = end - begin

  def compare(t: Word[_]): Int = compareFunc(this, t)

  override def equals(o: Any): Boolean = {
    if (canEqual(o)) {
      val _o = o.asInstanceOf[Word[_]]
      if (_o == null) false
      else if (this == _o) true
      else if (this.begin == _o.begin && this.end == _o.end) true
      else false
    } else false
  }


  override def hashCode(): Int = (begin * 37) + (end * 31) + ((begin * end) % (end - begin + 1)) * 11

  override def canEqual(that: Any): Boolean = that.isInstanceOf[Word[_]]

  def overlap(that: Word[_]): Boolean = {
    this.begin < that.end && this.end > that.begin
  }

}
