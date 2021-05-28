/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.26 : Database - amazon
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`amazon` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `amazon`;

/*Table structure for table `amazon_account` */

DROP TABLE IF EXISTS `amazon_account`;

CREATE TABLE `amazon_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `cookies` varchar(3000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(320) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `amazon_account` */

/*Table structure for table `automation` */

DROP TABLE IF EXISTS `automation`;

CREATE TABLE `automation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `from_time` int(11) DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL,
  `price_option` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prime_only` bit(1) DEFAULT NULL,
  `purchase_quantity` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `quantity_option` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity_option_value` int(11) DEFAULT NULL,
  `seller_option` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `seller_option_value` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `to_time` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpkx6c1b9j6j50fbghjbt131l7` (`created_by`),
  KEY `FKggj3k5qr2bx525e0nnxurh7uw` (`modified_by`),
  KEY `FK1t69ap5sabei5ei1poeagx4yg` (`product_id`),
  CONSTRAINT `FK1t69ap5sabei5ei1poeagx4yg` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKggj3k5qr2bx525e0nnxurh7uw` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKpkx6c1b9j6j50fbghjbt131l7` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `automation` */

/*Table structure for table `automation_log` */

DROP TABLE IF EXISTS `automation_log`;

CREATE TABLE `automation_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `automation_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtd6mhogxi5fv85y2qthk4e3tt` (`automation_id`),
  CONSTRAINT `FKtd6mhogxi5fv85y2qthk4e3tt` FOREIGN KEY (`automation_id`) REFERENCES `automation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `automation_log` */

/*Table structure for table `line_notify` */

DROP TABLE IF EXISTS `line_notify`;

CREATE TABLE `line_notify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `expired` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjpobjlwqx5f3fip19rpd8kr84` (`created_by`),
  KEY `FKglihklx7kg5w6jjcf9lyfdcoo` (`modified_by`),
  CONSTRAINT `FKglihklx7kg5w6jjcf9lyfdcoo` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjpobjlwqx5f3fip19rpd8kr84` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `line_notify` */

/*Table structure for table `order` */

DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `price` double DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `amazon_account_id` int(11) DEFAULT NULL,
  `automation_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `seller_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpj4anwcyr6a29rjiky7a763mc` (`amazon_account_id`),
  KEY `FKmmcih7ric5hcr7v76iltt95du` (`automation_id`),
  KEY `FKknjaoi59nxmpxhr452bj95tgg` (`product_id`),
  KEY `FKgptriawkusaypa8wh6ncdikg3` (`seller_id`),
  CONSTRAINT `FKgptriawkusaypa8wh6ncdikg3` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`),
  CONSTRAINT `FKknjaoi59nxmpxhr452bj95tgg` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKmmcih7ric5hcr7v76iltt95du` FOREIGN KEY (`automation_id`) REFERENCES `automation` (`id`),
  CONSTRAINT `FKpj4anwcyr6a29rjiky7a763mc` FOREIGN KEY (`amazon_account_id`) REFERENCES `amazon_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `order` */

/*Table structure for table `password_reset_token` */

DROP TABLE IF EXISTS `password_reset_token`;

CREATE TABLE `password_reset_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `expired_date` datetime DEFAULT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5lwtbncug84d4ero33v3cfxvl` (`user_id`),
  CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `password_reset_token` */

/*Table structure for table `product` */

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `asin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `product` */

/*Table structure for table `product_seller` */

DROP TABLE IF EXISTS `product_seller`;

CREATE TABLE `product_seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` bigint(20) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `seller_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhik01vjjc2vhqpt0p9r2pcm4i` (`product_id`),
  KEY `FKoh3hu7fnhwhtl1cmnc9wql53p` (`seller_id`),
  CONSTRAINT `FKhik01vjjc2vhqpt0p9r2pcm4i` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKoh3hu7fnhwhtl1cmnc9wql53p` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `product_seller` */

/*Table structure for table `seller` */

DROP TABLE IF EXISTS `seller`;

CREATE TABLE `seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `seller` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_email` (`email`),
  KEY `idx_user_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `user` */

insert  into `user`(`id`,`created_date`,`modified_date`,`email`,`full_name`,`password`,`status`) values 
(1,'2021-04-07 15:13:32','2021-04-07 15:13:32','admin@gmail.com','Administrator','$2a$10$vOqG3iUCNfMjw0LPgZLHMuP3HDrT7D/uLjjWb52ujFHTqg.RJz.Ue','ACTIVE');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
