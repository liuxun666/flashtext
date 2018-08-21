package com.hualv.bigdata.flashText.util

/**
  * Created by:
  * User: liuzhao
  * Date: 2018/7/26
  * Email: liuzhao@66law.cn
  */
object ArrayUtils {
  def binarySearch[T](array: Seq[T], value: T )(implicit ordering: Ordering[T]): Int = {
    var left: Int = 0
    var right: Int = array.length - 1
    while (left <= right) {
      val mid = right + left >>> 1
      val comp = ordering.compare(array(mid), value)
      if (comp == 0)
        return mid
      else if (comp > 0)
        right = mid - 1
      else if (comp < 0)
        left = mid + 1
    }
    -1
  }

}
