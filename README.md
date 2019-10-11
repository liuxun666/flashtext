# flashtext
flashtext find keyword in text

## scala :
```scala
val keywords = Array("拒绝","办法" ,"怎么办","么办法" ,"怎么" ,"离婚")
val dict = DictUtils.loadDict(keywords)
Analyzer.analyze("怎么有办法离婚", dict)(Compare.ENDFIRST).foreach(println)

val map = Map("北京" -> 110000, "成都" -> 510100)
val dict2 = DictUtils.loadDict(map)
Analyzer.analyze("明天飞北京")(Compare.ENDFIRST).foreach(println)

```
  
    
## java:
```java
String[] keywords = new  String[]{"拒绝","办法" ,"怎么办","么办法" ,"怎么" ,"离婚"};
Dictionary dict = DictUtils.loadDict(keywords);
Word[] finded = Analyzer.analyze("怎么有办法离婚", dict, true);
Map<String, Integer> map = new HashMap();
map.put("北京", 110000);
map.put("成都", 510100);
Dictionary dict = DictUtils.loadDict(map);
// 控制排序方式
Word<Integer>[] finded = Analyzer.analyze("明天飞北京", dict, true, (w1, w2) -> w1.end() - w2.end());  

```
