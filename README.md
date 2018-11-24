# flashtext
flashtext find keyword in text

## scala :

  val keywords = Array("拒绝","办法" ,"怎么办","么办法" ,"怎么" ,"离婚")
  
  val dict = DictUtils.loadDict(keywords)
  
  Analyzer.analyze("怎么有办法离婚", dict)(Compare.ENDFIRST).foreach(f => println(f))

## java:

  String[] keywords = new  String[]{"拒绝","办法" ,"怎么办","么办法" ,"怎么" ,"离婚"};
  
  Dictionary dict = DictUtils.loadDict(keywords);
  
  Word[] finded = Analyzer.analyze("怎么有办法离婚", dict, true, Compare.BEGINFIRST());
  
