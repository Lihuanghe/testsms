# testsms
短信网关测试客户端-命令行工具

# 命令行工具
```
java -jar testsms-0.0.1-SNAPSHOT.jar -c "./cmpp.cfg" -sid cmpp -tel 18703815655  -txt  河北短信记录查询功能

usage: Options
 -attime <arg>   At_Time
 -c <arg>        config File
 -dcs <arg>      msg-fmt
 -h              help info
 -msgsrc <arg>   msgsrc
 -sid <arg>      server id in config File
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


