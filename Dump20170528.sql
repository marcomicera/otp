-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bank
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `dongle_key` varchar(255) NOT NULL,
  `dongle_counter` varchar(255) NOT NULL,
  `large_window_on` varchar(255) NOT NULL,
  `large_window_otp` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('bortanzi.filippo','6Zz÷ÂAÇdõÓÏb#bÏ','8ôQ7<*Ï¹Ú(RÁd«\nf¶£Épc?ô¸Ñ','65èÒãÕI=ßß6	é','i;+î*\0à}]èÃt]dO',NULL),('ciccio_tognoli','trßíxT¸q²½Iç','ø;Í+C7Ãk»¦;ÕM\"¾6XêR3PAêY','ÕêÚ{v/oá}ç½','i;+î*\0à}]èÃt]dO',NULL),('claudia-de-santis','±Ûåü}¨í.=ÜÑß','r¼ª»ß´9PO¨ÝOBgùI÷?øÕpÔn9¨åô&','¼ß¼F9ö»Xè\'ô','i;+î*\0à}]èÃt]dO',NULL),('giorgio_mariani_71','»F |&\'¸3Àâ«','F¯ñªÄiüIgUöØÒ\nuõ)VJ8â°ÛbÉ§ù','](mwÍu~¢Avò','i;+î*\0à}]èÃt]dO',NULL),('giovanni.scalzi','# aEÄtëºÃ]4ÆäºB','9;çs ³+2»9¦R[è­`	çïhs@3©','\\l:FêóAöfÏ1','i;+î*\0à}]èÃt]dO',NULL),('giovanni283','µÂ_i° 4VJ4\0','×ñwÓ¿Á¤.ñ÷ª·Lê°äRÅ','§¤ti03	k×:zX','i;+î*\0à}]èÃt]dO',NULL),('milianti16','þæZö¤\'ï{2åêk,NI÷?øÕpÔn9¨åô&','ñ\ZÜúHñ)ÈÏñÀXHÜüúYð£ó)k<n²','¹GK0Ê!VÕ\\îö','i;+î*\0à}]èÃt]dO',NULL),('sandr0231','(7±àÏ­ÕÄ¨§Å\Zr','ÁÂwÜ¤Vw`$~u°Ë£:üO×éïK^¬áÌ×','Í ý/ÿaðj.¾','i;+î*\0à}]èÃt]dO',NULL),('stefanbotti','][eQL´%MCÿkîG','kn[ÜãÄ]§hîG;ª3kd0­«ù¸lÏs','3&Úè~cõ(M@n','i;+î*\0à}]èÃt]dO',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'bank'
--

--
-- Dumping routines for database 'bank'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-28 20:20:53
