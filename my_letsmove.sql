-- phpMyAdmin SQL Dump
-- version 4.1.7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 27, 2014 alle 12:12
-- Versione del server: 5.1.71-community-log
-- PHP Version: 5.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `my_letsmove`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `abbonamento`
--

CREATE TABLE IF NOT EXISTS `abbonamento` (
  `CodAbbonamento` varchar(6) COLLATE utf8_bin NOT NULL,
  `Nome` varchar(20) COLLATE utf8_bin NOT NULL,
  `Prezzo` double NOT NULL,
  `Durata` int(11) NOT NULL,
  PRIMARY KEY (`CodAbbonamento`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `abbonamento`
--

INSERT INTO `abbonamento` (`CodAbbonamento`, `Nome`, `Prezzo`, `Durata`) VALUES
('003', '3Giorni', 2.5, 3),
('007', 'Settimanale', 5, 7),
('030', 'Mensile', 20, 30),
('365', 'Annuale', 230, 365);

-- --------------------------------------------------------

--
-- Struttura della tabella `autobus`
--

CREATE TABLE IF NOT EXISTS `autobus` (
  `Targa` int(11) NOT NULL AUTO_INCREMENT,
  `CodDispositivo` int(11) NOT NULL,
  PRIMARY KEY (`Targa`),
  KEY `CodDispositivo` (`CodDispositivo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `credenziali`
--

CREATE TABLE IF NOT EXISTS `credenziali` (
  `CodAzienda` int(11) NOT NULL AUTO_INCREMENT,
  `Nome` varchar(30) COLLATE utf8_bin NOT NULL,
  `Indirizzo` varchar(30) COLLATE utf8_bin NOT NULL,
  `Citta` varchar(30) COLLATE utf8_bin NOT NULL,
  `Provincia` varchar(2) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodAzienda`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `dispositivo`
--

CREATE TABLE IF NOT EXISTS `dispositivo` (
  `CodDispositivo` int(11) NOT NULL AUTO_INCREMENT,
  `Nome` varchar(30) COLLATE utf8_bin NOT NULL,
  `Marca` varchar(30) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodDispositivo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `fermata`
--

CREATE TABLE IF NOT EXISTS `fermata` (
  `CodFermata` int(11) NOT NULL AUTO_INCREMENT,
  `NomeFermata` varchar(30) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodFermata`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=56 ;

--
-- Dump dei dati per la tabella `fermata`
--

INSERT INTO `fermata` (`CodFermata`, `NomeFermata`) VALUES
(30, 'Parcheggio Bellavista'),
(29, 'Donatello'),
(28, 'Stazione FS'),
(27, 'S. Paolo'),
(26, 'V. Bersezio'),
(25, 'P. Galimberti'),
(24, 'San Rocco'),
(22, 'C. Roma'),
(23, 'C. Francia'),
(21, 'V. Roma'),
(20, 'P. Torino'),
(31, 'Piazzale Cimitero'),
(32, 'C. Nizza'),
(33, 'V.le Mistral'),
(34, 'Piazza Italia'),
(35, 'C. Monviso'),
(36, 'C.G. Ferraris'),
(37, 'C. Vittorio'),
(38, 'V. Mediaglie d''Oro'),
(39, 'V. Avogadro'),
(40, 'V. Ghedini'),
(41, 'V. Fenoglio'),
(42, 'V. Scagliosi'),
(43, 'V. Mellana'),
(44, 'C. de Gasperi'),
(45, 'V. Riberi'),
(46, 'V. Bongioanni'),
(47, 'Crocetta'),
(48, 'itis'),
(49, 'Roata Canale'),
(50, 'Villaggio Colombero'),
(51, 'madonna dell olmo'),
(52, 'centallo'),
(53, 'via torino'),
(54, 'stazione centrale'),
(55, 'via genova');

-- --------------------------------------------------------

--
-- Struttura della tabella `linea`
--

CREATE TABLE IF NOT EXISTS `linea` (
  `CodLinea` varchar(5) COLLATE utf8_bin NOT NULL,
  `NomeLinea` varchar(30) COLLATE utf8_bin NOT NULL,
  `CodCapolinea` int(11) NOT NULL,
  `orariAndata` varchar(200) COLLATE utf8_bin NOT NULL,
  `orariRitorno` varchar(200) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodLinea`),
  KEY `CodFermata` (`CodCapolinea`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `linea`
--

INSERT INTO `linea` (`CodLinea`, `NomeLinea`, `CodCapolinea`, `orariAndata`, `orariRitorno`) VALUES
('SP', 'San Paolo', 25, '06:25;06:40;07:10;07:40;08:10;09:00;09:40;10:20;11:00;11:40;12:40;13:15;13:40;14:20;15:05;15:40;16:20;17:00;17:40;18:05;18:40;19:40', '06:45;06:55;07:35;08:05;08:35;09:20;10:00;10:40;11:20;12:00;13:35;14:00;14:40;15:25;16:00;16:40;17:20;18:00;18:25;19:00;20:00'),
('C', 'Centrale', 20, '08:20;08:45;09:20;09:45;10:25;11:00;12:40;13:20;14:20;15:00;15:40;16:50;18:45', '07:00;07:35;08:40;09:05;09:40;10:05;10:45;11:20;13:05;14:40;15:20;16:00;17:10;19:05'),
('1', 'Crocetta', 20, '06:10;07:15;08:05;08:30;09:00;10:00;10:50;11:30;12:10;13:10;13:40;14:40;15:20;16:10;16:25;17:05;17:40;18:05;19:05', '06:30;07:30;08:02;08:25;08:55;09:30;10:25;11:10;11:55;12:35;13:35;14:00;14:25;15:05;15:40;16:35;16:50;17:30;18:00;18:30;19:30'),
('D', 'Donatello', 20, '06:15;07:12;07:45;08:10;09:10;09:50;10:30;11:10;11:45;12:35;13:10;13:35;14:20;15:05;15:35;17:05;17:45;18:20;19:35', '06:35;07:40;08:00;08:35;09:30;10:10;10:50;11:30;13:45;14:00;14:40;15:25;16:00;17:25;18:05;18:40;19:55'),
('9', 'Villaggio Colombero', 28, '07:05;07:50;08:40;09:30', '07:03'),
('l1', 'linea2', 41, '01:00;01:58;02:58', '03:00;06:00'),
('8', 'centallo', 20, '07:00', '06:00'),
('m001', 'milano', 53, '13:59;14:12;14:19', '17:59;18:59;19:59');

-- --------------------------------------------------------

--
-- Struttura della tabella `percorre`
--

CREATE TABLE IF NOT EXISTS `percorre` (
  `Targa` int(11) NOT NULL,
  `CodLinea` varchar(5) COLLATE utf8_bin NOT NULL,
  `OraInizio` date NOT NULL,
  PRIMARY KEY (`Targa`,`CodLinea`),
  KEY `CodLinea` (`CodLinea`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Struttura della tabella `possiede`
--

CREATE TABLE IF NOT EXISTS `possiede` (
  `CodAcquisto` int(11) NOT NULL AUTO_INCREMENT,
  `CodDocumento` varchar(9) COLLATE utf8_bin NOT NULL,
  `CodAbbonamento` varchar(6) COLLATE utf8_bin NOT NULL,
  `DataInizio` date NOT NULL,
  PRIMARY KEY (`CodAcquisto`),
  KEY `CodDocumento` (`CodDocumento`,`CodAbbonamento`),
  KEY `CodAbbonamento` (`CodAbbonamento`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=22 ;

--
-- Dump dei dati per la tabella `possiede`
--

INSERT INTO `possiede` (`CodAcquisto`, `CodDocumento`, `CodAbbonamento`, `DataInizio`) VALUES
(21, 'a', '003', '2014-05-17'),
(18, 'smu', '003', '2014-05-17'),
(17, '22222222', '030', '2014-05-11'),
(16, '159', '365', '2014-05-11'),
(20, '013578357', '365', '2014-05-17'),
(13, '159', '003', '2014-05-01'),
(12, '159', '003', '2014-04-28');

-- --------------------------------------------------------

--
-- Struttura della tabella `tratta`
--

CREATE TABLE IF NOT EXISTS `tratta` (
  `CodFermata` int(11) NOT NULL,
  `CodLinea` varchar(5) COLLATE utf8_bin NOT NULL,
  `Successiva` int(11) DEFAULT NULL,
  PRIMARY KEY (`CodFermata`,`CodLinea`),
  KEY `CodLinea` (`CodLinea`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `tratta`
--

INSERT INTO `tratta` (`CodFermata`, `CodLinea`, `Successiva`) VALUES
(49, '9', 50),
(20, '9', 49),
(21, '9', 20),
(32, '9', 21),
(47, '1', 0),
(24, '1', 47),
(29, 'D', 0),
(28, 'D', 29),
(32, '1', 24),
(21, '1', 32),
(20, '1', 21),
(20, 'D', 28),
(33, 'SP', 0),
(27, 'SP', 33),
(26, 'SP', 27),
(32, 'SP', 26),
(25, 'SP', 32),
(23, 'C', 24),
(24, 'C', 0),
(32, 'C', 23),
(21, 'C', 32),
(20, 'C', 21),
(28, '9', 32),
(24, 'l1', 0),
(25, 'l1', 24),
(41, 'l1', 25),
(50, '9', 0),
(51, '8', 0),
(20, '8', 51),
(53, 'm001', 54),
(54, 'm001', 55),
(55, 'm001', 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `utente`
--

CREATE TABLE IF NOT EXISTS `utente` (
  `CodDocumento` varchar(9) COLLATE utf8_bin NOT NULL,
  `Username` varchar(20) COLLATE utf8_bin NOT NULL,
  `Password` varchar(64) COLLATE utf8_bin NOT NULL,
  `Salt` varchar(64) COLLATE utf8_bin NOT NULL,
  `Email` varchar(30) COLLATE utf8_bin NOT NULL,
  `Cognome` varchar(30) COLLATE utf8_bin NOT NULL,
  `Nome` varchar(30) COLLATE utf8_bin NOT NULL,
  `NumeroBiglietti` int(11) unsigned NOT NULL,
  `Indirizzo` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `Nascita` date DEFAULT NULL,
  `Localita` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `Prov` varchar(2) COLLATE utf8_bin DEFAULT NULL,
  `CAP` varchar(5) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodDocumento`),
  UNIQUE KEY `username` (`Username`),
  UNIQUE KEY `CodDocumento` (`CodDocumento`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dump dei dati per la tabella `utente`
--

INSERT INTO `utente` (`CodDocumento`, `Username`, `Password`, `Salt`, `Email`, `Cognome`, `Nome`, `NumeroBiglietti`, `Indirizzo`, `Nascita`, `Localita`, `Prov`, `CAP`) VALUES
('159', 'gigio', '20296dc349ae3d7e2be186450433eb44906891c8a9f601ba6e9084eed19e317c', '5731ceb7140f6c1', 'giacomo.migliore@gmail.com', 'gigio', 'gigio', 54, 'lol', '0000-00-00', 'ads', 'sa', '0'),
('111111111', 'jairo', '9cbd3849ee3af70dee701184d94a7214b5ac4c17688f9190535ecb0bc030e4b8', '2825cada5c40cde9', 'pabhk ', 'p', 'p', 15, 'p', '0000-00-00', 'p', 'p', 'p'),
('at6388765', 'Luca.morano', '7f62e412910401b772b1121307f8c6028a9fc01a8998c643d2ac8746608269d7', '3048fc821328c79d', 'luca@yahoo.it', 'morano', 'luca', 1, 'via roma 2', '1996-02-12', 'cuneo', 'cn', '12100'),
('a', 'a', '1edfaf7093e75db8807d32aab0bc2a77c2fb3da3622a169f658256b63308a888', '4d8b1a2128289aae', 'a', 'a', 'a', 15, 'a', '0000-00-00', 'aa', 'a', 'a'),
('smu', 'kamil', '3a14211cc636120a45a49f77355f34042274239c5c5ea49c29eee25d113a946c', '77eced2b6471ea84', 'kamil.smolen94@gmail.com', 'smolen', 'kamil', 8, 'piazza Borelli ', '1994-03-16', 'boves', 'cn', '12012'),
('22222222', 'paro', '238f77552af444cf80051ef13a22061e152143efb0c4e7aa943aa3af8a439600', '1f7f406b7ef95659', 'hfhdhd', 'parola', 'federico', 0, 'via Bella', '0000-00-00', 'Vignolo', 'CN', '0'),
('lllllllll', 'kamilyoo', 'a940e21a158e70b847f19a5da9724f70672459a566eb4abc1c5cf5918e67e8ab', '5bb706d91c298a12', 'blondyn@alice.it', 'smu', 'kam', 0, 'monginevro 14', '1994-03-16', 'torino', 'to', '10138'),
('aa1111111', 'Morano.luca', '2f6b8bfc1ee833c7672a4e35e3691f4d918d80e62a3798cb7402a3d47fbbc3ec', '7a7eb39dfe1467b', 'morano@gmail.com', 'morano', 'luca', 1, 'via roma', '1996-01-12', 'cuneo', 'cn', '12100'),
('ccccccccc', 'ciccio', '2556f8662a2c7625664d8881914c1401083e6a3b6b2b9f75c54b4b4bb0249c08', '10fe6402465e378c', 'andrea_tierno@live.it', 'tierno', 'andrea', 9, 'rivoira', '1994-07-06', 'rivoira', 'cn', '12012'),
('013578357', 'cava', '32f45e72cce196c67f150e4491c49c4cc23d0abd6371e77fe1e4fff541b8fb91', '462cd43d16a277f6', 'andreacavallera95@gmail.com', 'cavallera', 'andrea', 0, 'via pupu', '1995-01-31', 'taranrasca', 'cn', '12020'),
('123456709', 'giacomiglio', 'd183b91378a9d7c4a98d21770bf3be8018eb5caef76cdf138770393204403c8c', '74a7b07a39054eef', 'g.m@gmail.com', 'Migliore', 'Giacomo', 0, 'Via crocetta', '1995-09-18', 'Cn', 'Cn', '12100');

-- --------------------------------------------------------

--
-- Struttura della tabella `viaggio`
--

CREATE TABLE IF NOT EXISTS `viaggio` (
  `CodViaggio` int(11) NOT NULL AUTO_INCREMENT,
  `Data` datetime NOT NULL,
  `DurataConvalida` int(11) NOT NULL,
  `CodDocumento` varchar(9) COLLATE utf8_bin NOT NULL,
  `CodLinea` varchar(5) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`CodViaggio`),
  KEY `CodUtente` (`CodDocumento`),
  KEY `CodLinea` (`CodLinea`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=82 ;

--
-- Dump dei dati per la tabella `viaggio`
--

INSERT INTO `viaggio` (`CodViaggio`, `Data`, `DurataConvalida`, `CodDocumento`, `CodLinea`) VALUES
(45, '2014-05-12 10:40:56', 365, '159', 'C'),
(44, '2014-05-12 09:57:30', 365, '159', 'smu'),
(43, '2014-05-12 09:57:18', 365, '159', 'smu'),
(42, '2014-05-12 09:52:34', 365, '159', 'smu'),
(8, '2014-04-14 09:06:33', 90, 'a', '1'),
(9, '2014-04-14 09:06:53', 90, '159', '1'),
(10, '2014-04-14 09:19:17', 90, 'smu', '1'),
(11, '2014-04-14 16:46:50', 90, 'a', '1'),
(12, '2014-04-14 16:47:17', 90, '159', '1'),
(13, '2014-04-14 16:50:13', 90, '111111111', '1'),
(14, '2014-04-15 08:13:27', 90, 'a', '1'),
(15, '2014-04-15 08:47:44', 90, '111111111', '1'),
(16, '2014-04-15 08:49:36', 90, '159', '1'),
(17, '2014-04-15 09:44:49', 90, 'a', '1'),
(18, '2014-04-15 09:51:31', 90, 'abc', '1'),
(19, '2014-04-15 10:43:21', 90, '159', '2'),
(20, '2014-04-15 10:51:49', 90, '000000000', '1'),
(21, '2014-04-15 11:40:00', 90, 'a', '1'),
(22, '2014-04-15 12:06:06', 90, 'abc', '1'),
(23, '2014-05-01 13:14:53', 90, '159', '1'),
(36, '2014-05-11 16:59:45', 3, 'a', 'smu'),
(26, '2014-05-10 10:03:40', 365, '159', 'smu'),
(27, '2014-05-10 10:13:00', 365, '159', 'smu'),
(38, '2014-05-11 17:04:53', 365, '159', 'smu'),
(30, '2014-05-11 16:54:12', 3, 'a', 'smu'),
(31, '2014-05-11 16:54:44', 3, 'a', 'smu'),
(32, '2014-05-11 16:56:15', 365, '159', 'smu'),
(33, '2014-05-11 16:58:01', 365, '159', 'smu'),
(35, '2014-05-11 16:59:18', 3, 'a', 'smu'),
(37, '2014-05-11 17:04:25', 365, '159', 'smu'),
(39, '2014-05-11 20:10:42', 90, '159', 'smu'),
(40, '2014-05-11 00:00:00', 90, '159', 'smu'),
(41, '2014-05-11 00:00:00', 90, '159', 'smu'),
(46, '2014-05-12 10:41:44', 365, '159', 'smu'),
(47, '2014-05-12 10:55:31', 365, '159', 'smu'),
(48, '2014-05-12 10:57:24', 365, '159', 'C'),
(49, '2014-05-12 10:57:50', 90, 'a', 'C'),
(50, '2014-05-12 15:33:13', 90, 'a', 'smu'),
(51, '2014-05-12 19:58:53', 90, 'a', 'smu'),
(52, '2014-05-13 08:15:56', 90, 'a', 'smu'),
(53, '2014-05-17 09:06:58', 3, 'smu', 'fui'),
(54, '2014-05-17 09:34:06', 3, 'smu', 'fui'),
(55, '2014-05-17 09:39:28', 90, 'a', 'fui'),
(56, '2014-05-17 10:41:20', 90, 'ccccccccc', 'l3'),
(57, '2014-05-17 11:01:32', 365, '013578357', 'l3'),
(58, '2014-05-17 11:52:36', 90, 'a', 'C'),
(59, '2014-05-17 12:06:23', 365, '159', 'C'),
(60, '2014-05-17 12:26:49', 365, '159', 'C'),
(61, '2014-05-19 08:26:58', 3, 'a', 'C'),
(62, '2014-05-19 08:44:19', 3, 'a', 'SP'),
(63, '2014-05-19 08:45:11', 3, 'a', 'SP'),
(64, '2014-05-19 08:45:44', 3, 'smu', 'SP'),
(65, '2014-05-19 08:46:00', 3, 'smu', 'SP'),
(66, '2014-05-19 09:05:56', 3, 'smu', 'SP'),
(67, '2014-05-19 09:12:53', 3, 'smu', 'SP'),
(68, '2014-05-19 09:17:57', 3, 'smu', '1'),
(69, '2014-05-19 09:18:59', 3, 'smu', '1'),
(70, '2014-05-19 09:26:55', 3, 'smu', '1'),
(71, '2014-05-19 09:31:29', 3, 'smu', '1'),
(72, '2014-05-19 09:33:18', 90, 'ccccccccc', '1'),
(73, '2014-05-31 09:52:56', 12, 'a', '1'),
(74, '2014-05-31 09:53:50', 0, 'a', 'hrdh'),
(75, '2014-06-16 17:13:06', 90, 'smu', 'SP'),
(76, '2014-06-17 17:55:06', 90, 'smu', 'SP'),
(77, '2014-06-30 11:08:05', 90, 'smu', 'D'),
(78, '2014-07-02 09:05:52', 90, 'smu', 'SP'),
(79, '2014-07-02 10:26:34', 90, 'a', 'SP'),
(80, '2014-11-27 11:32:53', 90, 'lllllllll', 'C'),
(81, '2014-11-27 11:38:59', 90, 'smu', 'C');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
