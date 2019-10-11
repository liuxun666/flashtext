package com.hualv.bigdata.flashText

import java.util.concurrent.ConcurrentHashMap

import com.hualv.bigdata.flashText.util.ArrayUtils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/26
  * Email: liuzhao@66law.cn
  */
case class Dictionary[T](private var nodeChar: Char = '0') extends Comparable[Dictionary[T]] with Ordered[Dictionary[T]]{
  //公用字典表，存储汉字
//  private val charMap = new ConcurrentHashMap[Character, T](16, 0.95f)
  private val charMap = mutable.HashMap[Character, T]()
  //数组大小上限
  private val ARRAY_LENGTH_LIMIT = 3
  //数组方式存储结构
  private var childrenArray: mutable.ArrayBuffer[Dictionary[T]] = _
  //Map存储结构
  private var childrenMap: ConcurrentHashMap[Character, Dictionary[T]] = _
  //当前节点存储的Segment数目
  //storeSize <=ARRAY_LENGTH_LIMIT ，使用数组存储， storeSize >ARRAY_LENGTH_LIMIT ,则使用Map存储
  private var storeSize = 0
  //当前Dict状态，表示从根节点到当前节点的路径表示一个完整的词
  private var nodeState: Boolean = _

  private def getNodeChar: Character = this.nodeChar

  private def hasNextNode() = this.storeSize > 0

  def getValue() = this.charMap(getNodeChar)

  /**
    *
    * @param charArray
    * @param begin
    * @return
    */
  def `match`(charArray: Array[Char], begin: Int): Hit[T] = `match`(charArray, begin, null)

  /**
    * 匹配词段
    *
    * @param charArray
    * @param begin
    * @param searchHit
    * @return Hit
    */
  def `match`(charArray: Array[Char], begin: Int, searchHit: Hit[T]): Hit[T] = {
    var _searchHit = searchHit
    //如果hit为空，新建
    if (_searchHit == null) {
      _searchHit = new Hit[T]
      //设置hit的起始文本位置
      _searchHit.setBegin(begin)
    }else{
      //否则要将HIT状态重置
      _searchHit.setUnmatch()
    }
    //设置hit的当前处理位置
    _searchHit.setEnd(begin)
    val keyChar = charArray(begin)

    var ds: Dictionary[T] = null
    //引用实例变量为本地变量，避免查询时遇到更新的同步问题
    val dss = this.childrenArray
    val dictMap = this.childrenMap
    if(dss != null){
      //在数组中查找
      val  keyDict = Dictionary[T](keyChar)
      val  position = ArrayUtils.binarySearch(dss,  keyDict)
      if(position >= 0){
        ds = dss(position)
      }
    }else if(dictMap != null){
      ds = dictMap.get(keyChar)
    }
    //找到DictSegment，判断词的匹配状态
    if(ds != null){
      //搜索最后一个char
      if(ds.nodeState){
        //添加HIT状态为完全匹配
        _searchHit.setMatch()
        //记录当前位置的DictSegment
        _searchHit.setMatchedDictionary(ds)
      }
      if(ds.hasNextNode()){
        //添加HIT状态为前缀匹配
        _searchHit.setPrefix()
        //记录当前位置的DictSegment
        _searchHit.setMatchedDictionary(ds)
      }
    }
    _searchHit
  }

  /**
    * 加载词典
    *
    * @param word
    */
  def addWord(word: String): Unit = addWord(word.toCharArray, null.asInstanceOf[T], 0, word.length)

  def addWord(words: Map[String, T]): Unit = {
    words.foreach(f => addWord(f._1.toCharArray, f._2, 0, f._1.length))
  }

  /**
    * 禁用一个词
    * @param word
    */
  def disableWord(word: String): Unit = addWord(word.toCharArray, null.asInstanceOf[T], 0, word.length , false)


  /**
    * 加载词典
    *
    * @param word
    * @param begin
    * @param length
    * @param state
    */
  private def addWord(word: Array[Char], value: T, begin: Int, length: Int, state: Boolean = true): Unit = {
    //获取字典表中的词语对象
    val beginChar = word(begin)
    if(!charMap.contains(beginChar)){
      //字典中没有该字，则将其添加入字典
      charMap.put(beginChar, null.asInstanceOf[T])
    }
    //搜索当前节点的存储，查询对应keyChar的ds，如果没有则创建
    val ds = lookforDict(beginChar)
    if(ds != null){
      //处理keyChar对应的segment
      if(length > 1){
        //词还没有完全加入词典树
        ds.addWord(word, value, begin + 1, length - 1, state)
      }else if (length == 1){
        //已经是词的最后一个char,设置当前节点状态为true，
        //true表明一个完整的词，false表示从词典中屏蔽当前词
        ds.nodeState = state
        ds.charMap.put(beginChar, value)
      }
    }

  }

  /**
    * 查找本节点下对应的keyChar的dict
    *
    * @param keyChar
    * @return
    */
  private def lookforDict(keyChar: Character) = {
    var ds: Dictionary[T] = null

    if(this.storeSize <= ARRAY_LENGTH_LIMIT){
      var dss = getChildrenArray()
      val keyDict = Dictionary[T](keyChar)
      val position = ArrayUtils.binarySearch(dss, keyDict)
      if(position >= 0){
        ds = dss(position)
      }

      //遍历数组后没有找到对应的segment
      if(ds == null){
        ds = keyDict
        if(this.storeSize < ARRAY_LENGTH_LIMIT){
          //数组容量未满，使用数组存储
          dss += ds
          this.storeSize += 1
          this.childrenArray = dss.sorted
        }else{
          //数组容量已满，切换Map存储
          val dictMap = getChildrenMap()
          //将数组中的segment迁移到Map中
          dss.foreach(f => dictMap.put(f.getNodeChar, f))
          //存储新的segment
          dictMap.put(keyChar, ds)
          //segment数目+1 ，  必须在释放数组前执行storeSize++ ， 确保极端情况下，不会取到空的数组
          this.storeSize += 1
          //释放当前的数组引用
          this.childrenArray = null
        }
      }
    }else{
      //获取Map容器，如果Map未创建,则创建Map
      val segmentMap = getChildrenMap()
      //搜索Map
      ds = segmentMap.get(keyChar)
      if(ds == null){
        //构造新的segment
        ds = Dictionary(keyChar)
        segmentMap.put(keyChar , ds)
        //当前节点存储segment数目+1
        this.storeSize += 1
      }
    }
    ds

  }

  /**
    * 获取数组容器
    * 线程同步方法
    */
  private def getChildrenArray() = {
    this.synchronized{
      if(this.childrenArray == null){
        this.childrenArray = ArrayBuffer[Dictionary[T]]()
      }
    }
    this.childrenArray
  }

  /**
    * 获取Map容器
    * 线程同步方法
    */
  private def getChildrenMap() = {
    this synchronized {
      if (childrenMap == null) childrenMap = new ConcurrentHashMap[Character, Dictionary[T]](6, 0.8f)
    }
    childrenMap
  }

  /**
    * 实现Comparable接口
    *
    * @param o
    * @return int
    */
  override def compareTo(o: Dictionary[T]): Int = {
    if(this == null){
      -1
    }else if(o == null){
      1
    }else{
      //对当前节点存储的char进行比较
      this.nodeChar.compareTo(o.nodeChar)
    }

  }

  override def compare(that: Dictionary[T]): Int = compareTo(that)

  override def equals(o: scala.Any): Boolean = o match {
    case segment: Dictionary[T] => compare(segment) == 0
    case _ => false
  }
}


