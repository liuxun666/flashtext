package com.hualv.bigdata.flashText

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
    *   eg:<br>
    *   val dict = DictUtils.loadDict(Seq("大家", "大家好"))<br>
    *   Analyzer.analyze("大家好", dict)) <br>
    *   # return: Array(Word(大家好,0,2))<br>
    *   Analyzer.analyze("大家好", dict, false)) <br>
    *   # return: Array(Word(大家,0,1), Word(大家好,0,2))<br>
    * @param text 需要查找的文本
    * @param dictionary 词典
    * @param isSmart 是否智能匹配(默认：true)，true: 按起始位置和长度优先返回结果，不允许词之间重叠，false：返回最细粒度的结果
    * @return
    */
  def analyze[T](text: String, dictionary: Dictionary[T], isSmart: Boolean = false): Array[Word] = {
    val charArray = text.toCharArray
    var cursor = 0
    val words = mutable.ArrayBuffer[Word]()
    val hits = mutable.Buffer[Hit[T]]()
    var hit: Hit[T] = null
    while (cursor < charArray.length){
      //先处理所有前缀匹配的词
      while (hits.nonEmpty){
        hit = hits.remove(0)
        if(hit.getEnd < charArray.length - 1){
          hit = hit.getMatchedDictSegment.`match`(charArray, hit.getEnd + 1, hit)
          if(hit.isMatch){
            val w = Word(charArray.slice(hit.getBegin, hit.getEnd + 1).mkString, hit.getBegin, hit.getEnd + 1, hit.getMatchedDictSegment.getValue())
            //如果智能匹配，并且当前词和上一个词重叠了，比较两个词的大小再决定取哪一个
            if(isSmart){
              if(words.isEmpty){
                words += w
              }else{
                //当前词和上一个词重叠了
                if(w.fullContain(words.last) || w.overlap(words.last)){
                  //当前词大于上一个词，把上一个词删去，将新词加入, 否则不加入当前匹配到的词
                  if(w.compareTo(words.last) >= 0 ){
                    words.remove(words.length - 1)
                    words += w
                  }
                }else words += w
              }
            }else{
              words += w
            }
          }
          //前缀匹配，加入前缀匹配列表
          if (hit.isPrefix){
            hits += hit
          }
          if(hit.isUnmatch){
            hit = null
          }
        }
      }
      //当前没有前缀匹配时，重新匹配
      hit = dictionary.`match`(charArray, cursor)
      if(hit.isMatch){
        val w = Word(charArray.slice(hit.getBegin, hit.getEnd + 1).mkString, hit.getBegin, hit.getEnd + 1, hit.getMatchedDictSegment.getValue())
        if(isSmart){
          if(words.isEmpty){
            words += w
          }else{
            //当前词和上一个词重叠了
            if(w.fullContain(words.last) || w.overlap(words.last)){
              //当前词大于上一个词，把上一个词删去，将新词加入
              if(w.compareTo(words.last) >= 0 ){
                words.remove(words.length - 1)
                words += w
              }
            }else words += w
          }
        }else{
          words += w
        }
      }
      if (hit.isPrefix){
        //前缀匹配上
        hits += hit
      }
      if(hit.isUnmatch){
        hit = null
      }
      cursor += 1
    }
    words.toArray
  }

}

 case class Word(word: String, begin: Int, end: Int, info: Any = null) extends Comparable[Word] {
  val length: Int = end - begin
   //TODO allow custom compare func

  override def compareTo(t: Word): Int = {
    if(this.begin < t.begin) 1
    else if(this.begin == t.begin){
      if(this.end > t.end) 1
      else if (this.end == t.end) 0
      else -1
    }else -1
  }

   /**
     * 比较两个词：起始位置优先，再长度优先
     * @param t
     * @return
     */
  def beginFirst(t: Word): Int = {
    if(this.begin < t.begin) 1
    else if(this.begin == t.begin){
      if(this.end > t.end) 1
      else if (this.end == t.end) 0
      else -1
    }else -1
  }

   /**
     * 比较两个词：结束位置优先，再长度优先
     * @param t
     * @return
     */
  def endFirst(t: Word): Int = {
    if(this.end > t.end) 1
    else if(this.end == t.end){
      if(this.begin < t.begin) 1
      else if( this.begin == this.begin) 0
      else -1
    }else -1
  }

   /**
     * 比较两个词：长度优先，再起始位置优先
     * @param t
     * @return
     */
  def lengthFirst(t: Word): Int = {
    if(this.fullContain(t) || this.overlap(t)){
      //重叠
      this.length.compareTo(t.length)
    }else this.begin.compareTo(t.begin)
  }

  override def equals(o: scala.Any): Boolean = {
    if(canEqual(o)){
      val _o = o.asInstanceOf[Word]
      if(_o == null) false
      else if(this == _o) true
      else if(this.begin == _o.begin && this.end == _o.end) true
      else false
    } else false
  }

  override def hashCode(): Int = (begin * 37) + (end * 31) + ((begin * end) % (end - begin + 1)) * 11

  override def canEqual(that: Any): Boolean = that.isInstanceOf[Word]

  def fullContain(that: Word) = {
    this.begin <= that.begin && this.end >= that.end
  }

  def overlap(that: Word) = {
    this.begin < that.end && this.end >= that.end
  }
}
