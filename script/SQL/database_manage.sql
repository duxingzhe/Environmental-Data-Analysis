create database environment_record;

use environment_record;

CREATE TABLE `recorder` (
  `time` varchar(45) NOT NULL,
  `province` varchar(45) NOT NULL,
  `city` varchar(45) NOT NULL,
  'weather' varchar(45) NOT NULL,
  `highest_temperature` varchar(45) NOT NULL,
  `lowest_temperature` varchar(45) NOT NULL,
  `air_quality` varchar(45) NOT NULL,
  `PM25` varchar(45) NOT NULL,
  `SO2` varchar(45) NOT NULL,
  `NO2` varchar(45) NOT NULL,
  `CO` varchar(45) NOT NULL,
  `warning_type` varchar(45) DEFAULT NULL,
  `warning_level` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `environment_record`.`recorder`
(`time`,
`province`,
`city`,
`highest_temperature`,
`lowest_temperature`,
`air_quality`,
`PM25`,
`SO2`,
`NO2`,
`CO`,
`warning_type`,
`warning_level`)
VALUES
("2019/08/11",
"广东",
"河源",
"小雨",
"34",
"26",
"22",
"22",
"8",
"21",
"1.0",
"暴雨",
"橙色");

alter table environment_record CHARACTER SET utf8mb4;

alter table recorder CHARACTER SET utf8mb4;

ALTER TABLE `environment_record`.`recorder` 
ADD COLUMN `weather` VARCHAR(45) NOT NULL AFTER `city`;

UPDATE `environment_record`.`recorder`
SET
`weather` = '小雨'
WHERE `time`='2019/08/11'

ALTER TABLE `environment_record`.`recorder`
CHANGE COLUMN `province` `province` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `city` `city` VARCHAR(45) NOT NULL ;

ALTER TABLE `environment_record`.`recorder` 
CHANGE COLUMN `warning_type` `warning_type` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
CHANGE COLUMN `warning_level` `warning_level` VARCHAR(45) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ;
