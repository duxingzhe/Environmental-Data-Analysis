library(RMySQL)
library(ggplot2)
conn<-dbConnect(MySQL(), dbname = "environment_record", username="root", password="", host="127.0.0.1", port=3306)
dbSendQuery(conn,'SET NAMES gbk')
sql<-"SELECT time, lowest_temperature, highest_temperature FROM environment_record.recorder;"
result<-dbSendQuery(conn, sql)
df<-dbFetch(result)
#作图
ggplot(data=df)+geom_point(aes(x=time, y=lowest_temperature,fill ="最低温度"),size=5,shape=21)+geom_point(aes(x=time, y=highest_temperature,fill ="最高温度"),size=5,shape=21)+geom_line(aes(x=time, y=lowest_temperature),color="green", group=1)+geom_line(aes(x=time, y=highest_temperature), color="red",group=1)+labs(x="时间", y="温度", title="广东省河源市2019年8月11日至9月10日温度情况",fill="")+scale_fill_brewer(type="seq",palette="Set2",limits=c('最高温度','最低温度'))+theme(plot.title = element_text(hjust = 0.5))
