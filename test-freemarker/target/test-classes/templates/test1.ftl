<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>

<table>
    <tr>
         <td>序号</td>
         <td>姓名</td>
         <td>年龄</td>
         <td>金额</td>
    </tr>
    <#if stus??>
    <#list stus as stu>
    <tr>
          <td>${stu_index+1}</td>
         <td <#if stu.name=='小明'>style="background: chartreuse" </#if>${stu.name}</td>
         <td>${stu.age}</td>
         <td>${stu.money}</td>

    </tr>
    </#list>
     </#if>

    输出stu1的学生信息:</br>
    姓名:  ${(stuMap['stu1'].name)!''}</br>
    年龄:   ${(stuMap['stu1'].age)!''}</br>

    输出stu1的学生信息:</br>
    姓名: ${(stuMap.stu1.name)!''}</br>
    年龄: ${(stuMap.stu1.age)!''}</br>


    学生的大小:
      ${stus?size}</br>

    遍历输出两个学生信息:</br>
    <table>
        <tr>
            <td>序号</td>
            <td>姓名</td>
            <td>年龄</td>
            <td>金额</td>
            <td>出生日期</td>
        </tr>
        <#list stuMap?keys as k>
             <tr>
                 <td>${k_index+1}</td>
                 <td>${stuMap[k].name}</td>
                 <td>${stuMap[k].age}</td>
                 <td>${stuMap[k].money}</td>
             </tr>
        </#list>
    </table>

</table>
</body>
</html>