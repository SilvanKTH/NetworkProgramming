-- Assumes the database already exists. The spring app uses the database name `appservspringbank`,
-- create such a database before running this script. Run the script with the command
-- mysql appservspringbank < src/scripts/db/create-appservspringbank-mariadb.sql -p

--
-- Drop all tables;
--
DROP TABLE IF EXISTS `CURRENCY_SEQUENCE`;
DROP TABLE IF EXISTS `CURRENCY`;

--
-- Create for table `CURRENCY`
--

CREATE TABLE `CURRENCY` (
    `CURRENCY_ID` bigint(10) NOT NULL,
    `CURRENCY` varchar(10) NOT NULL,
    `EXCHANGE_RATE` float(10,5) NOT NULL,
    PRIMARY KEY (`CURRENCY_ID`)
);