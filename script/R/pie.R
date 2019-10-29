library(RMySQL)

#读取数据
conn<-dbConnect(MySQL(), dbname = "environment_record", username="root", password="", host="127.0.0.1", port=3306)
dbSendQuery(conn,'SET NAMES gbk')
sql<-"SELECT air_quality FROM environment_record.recorder;"
result<-dbSendQuery(conn, sql)
df<-dbFetch(result)
airqualit<-df$air_quality

good<-0
moderate<-0
unhealthyForSensitive<-0
unhealthy<-0
veryUnhealthy<-0
hazardous<-0

#循环获取数据
for (i in 1:length(airquality)){if (airquality[i] < 50) {good<-good+1
  } else if (airquality[i] < 100) {moderate<-moderate+1
  } else if (airquality[i] < 150) {unhealthyForSensitive<-unhealthyForSensitive+1
  } else if (airquality[i] < 200) {unhealthy<-unhealthy+1
  } else if (airquality[i] < 300) {veryUnhealthy<-veryUnhealthy+1
  } else if (airquality[i] >= 300){hazardous<-hazardous+1
  }
}

x <- c(good, moderate, unhealthyForSensitive,unhealthy,veryUnhealthy,hazardous)
labels <- c("good", "moderate", "unhealthyForSensitive", "unhealthy", "veryUnhealthy", "hazardous")
piepercent<- round(100*x/sum(x), 1)

# Plot the chart.
pie(x, labels = piepercent, main = "河源市2019年8月11日至9月10日空气质量",col = rainbow(length(x)))
legend("right", c("good", "moderate", "unhealthyForSensitive", "unhealthy", "veryUnhealthy", "hazardous"), cex = 0.8, fill = rainbow(length(x)))
