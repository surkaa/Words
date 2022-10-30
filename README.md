# 单词本
技术Recycler ListAdapter Room BufferedReader

## 介绍

这是一个可以记单词的app. 用户每次看到单词的英文后,若能回忆正确单词释义, 则该单词的回忆次数加一, 当回忆次数达到一定量后, 用户对该单词的记忆强度明显得到提升. 记忆本来就是不断重复不断回忆来提升大脑对事物的印象.

## Recycler

添加了ListAdapter的适配器

## Room

使用了Entity Dao Database三件套使用了Room

## BufferedReader

使用InputStreamReader读取了context.getAssets()下的资产文件
