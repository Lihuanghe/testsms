# testsms
短信网关测试客户端-命令行工具

# 命令行工具
```
java -jar testsms-0.1.jar -c "./cfg.cfg" -sid cmpp -tel 13800138000  -txt  河北短信记录查询功能

usage: Options
 -attime <arg>   At_Time . yyMMddhhmmsstnnp . 20220401150159032+
 -c <arg>        config File
 -dcs <arg>      msg-fmt
 -h              help info
 -msgsrc <arg>   msgsrc
 -raw <arg>      send raw splited hex user-data , encode : dcs,ud,dcs,ud.
                 ex. 8,0500037702016cb3531777......
 -sid <arg>      serverUrl id config File
 -spcode <arg>   spcode
 -tel <arg>      telephone
 -txt <arg>      SMS Content
 -wait <arg>     wait time to exit

```

# 配置文件cfg.cfg

cmpp=cmpp://127.0.0.1:17890?username=test01&password=1qaz2wsx&version=32&spcode=10086&msgsrc=901783&serviceid=40037

smpp=smpp://127.0.0.1:18890?username=test01&password=1qaz2wsx&version=51&spcode=10086&addzero=1

sgip=sgip://127.0.0.1:16890?username=test01&password=1qaz2wsx&nodeid=322323&spcode=10086&corpid=901783

smgp=smgp://127.0.0.1:19890?username=test01&password=1qaz2wsx&version=48&serviceid=322323&spcode=10086&msgsrc=901783

# 发送二进制短信内容
```

java -jar testsms-0.1.jar -c "./cfg.cfg" -sid cmpp -tel 13800138000 -raw 0,05000310020144454c4956524432323034,8,0500031002026cb3531777ed4fe18bb0

```
