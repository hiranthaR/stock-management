-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 27, 2018 at 02:54 PM
-- Server version: 10.1.37-MariaDB-1
-- PHP Version: 7.2.9-1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stock-db`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `admin_id` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `level` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`admin_id`, `name`, `username`, `password`, `level`) VALUES
(1, 'Admin', 'admin', 'admin', 19),
(3, 'Test Low Power Admin', 'hiruu', 'hiruu', 15);

-- --------------------------------------------------------

--
-- Table structure for table `cheques`
--

CREATE TABLE `cheques` (
  `invoice_id` int(4) NOT NULL,
  `bank` varchar(20) NOT NULL,
  `branch` varchar(30) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `cheque_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `title` varchar(5) NOT NULL,
  `customer_name` varchar(60) NOT NULL,
  `rank` int(1) NOT NULL,
  `address` varchar(100) NOT NULL,
  `image_url` varchar(30) NOT NULL,
  `telephone` varchar(10) NOT NULL,
  `flag` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`customer_id`, `title`, `customer_name`, `rank`, `address`, `image_url`, `telephone`, `flag`) VALUES
(1, 'Mr.', 'Hirantha', 1, 'Kurunegala', '/com/hirantha/icons/man.png', '0702693523', 1),
(2, 'Mr.', 'Hasitha', 1, '1233', '/com/hirantha/icons/man.png', '1233333333', 1);

-- --------------------------------------------------------

--
-- Table structure for table `inovice_items`
--

CREATE TABLE `inovice_items` (
  `item_id` int(11) NOT NULL,
  `invoice_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cost_per_item` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `inovice_items`
--

INSERT INTO `inovice_items` (`item_id`, `invoice_id`, `quantity`, `cost_per_item`) VALUES
(1, 1, 10, '2000.00'),
(1, 3, 20, '2000.00'),
(2, 2, 5, '434.00');

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `supplier_name` varchar(60) NOT NULL,
  `supplier_address` varchar(100) NOT NULL,
  `supplier_invoice_number` varchar(20) NOT NULL,
  `total_bill_cost` decimal(10,2) NOT NULL,
  `cash_flag` tinyint(1) NOT NULL,
  `prepared_admin_id` int(3) NOT NULL,
  `accepted_admin_id` int(3) NOT NULL,
  `checked_admin_id` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `invoices`
--

INSERT INTO `invoices` (`invoice_id`, `date`, `supplier_name`, `supplier_address`, `supplier_invoice_number`, `total_bill_cost`, `cash_flag`, `prepared_admin_id`, `accepted_admin_id`, `checked_admin_id`) VALUES
(1, '2018-12-04', 'Supplier', 'wariyapola', '2000', '20000.00', 1, 1, 1, 1),
(2, '2018-12-05', 'Www', 'www', 'www', '2170.00', 1, 1, 1, 1),
(3, '2018-12-05', 'F', 'f', 'f', '40000.00', 1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int(4) NOT NULL,
  `item_name` varchar(30) NOT NULL,
  `category` varchar(20) NOT NULL,
  `unit` varchar(15) NOT NULL,
  `receipt_price` decimal(10,2) NOT NULL,
  `marked_price` decimal(10,2) NOT NULL,
  `selling_price` decimal(10,2) NOT NULL,
  `percentage_flag` tinyint(1) NOT NULL,
  `rank1` decimal(10,2) NOT NULL,
  `rank2` decimal(10,2) NOT NULL,
  `rank3` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_id`, `item_name`, `category`, `unit`, `receipt_price`, `marked_price`, `selling_price`, `percentage_flag`, `rank1`, `rank2`, `rank3`) VALUES
(1, 'Test Item', 'Tools', 'kg', '2000.00', '2500.00', '2300.00', 0, '100.00', '80.00', '70.00'),
(2, 'Ffaffa', 'Safsafsafsa', 'sfasfsaf', '434.00', '432.00', '431.00', 0, '43.00', '43.00', '43.00');

-- --------------------------------------------------------

--
-- Table structure for table `outgoings`
--

CREATE TABLE `outgoings` (
  `outgoing_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `date` date NOT NULL,
  `total_bill_cost` decimal(10,2) NOT NULL,
  `prepared_admin_id` int(11) NOT NULL,
  `accepted_admin_id` int(11) NOT NULL,
  `checked_admin_id` int(11) NOT NULL,
  `vehicle` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `outgoings`
--

INSERT INTO `outgoings` (`outgoing_id`, `customer_id`, `date`, `total_bill_cost`, `prepared_admin_id`, `accepted_admin_id`, `checked_admin_id`, `vehicle`) VALUES
(1, 1, '2018-12-04', '38000.00', 1, 1, 1, '');

-- --------------------------------------------------------

--
-- Table structure for table `outgoing_items`
--

CREATE TABLE `outgoing_items` (
  `item_id` int(11) NOT NULL,
  `outgoing_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `percentage` tinyint(1) NOT NULL,
  `discount` decimal(10,2) NOT NULL,
  `cost_per_items` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `outgoing_items`
--

INSERT INTO `outgoing_items` (`item_id`, `outgoing_id`, `quantity`, `percentage`, `discount`, `cost_per_items`) VALUES
(1, 1, 20, 0, '2000.00', '2000.00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`);

--
-- Indexes for table `cheques`
--
ALTER TABLE `cheques`
  ADD PRIMARY KEY (`invoice_id`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`);

--
-- Indexes for table `inovice_items`
--
ALTER TABLE `inovice_items`
  ADD PRIMARY KEY (`item_id`,`invoice_id`),
  ADD KEY `invoice_id` (`invoice_id`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`invoice_id`),
  ADD KEY `prepared_admin_id` (`prepared_admin_id`),
  ADD KEY `accepted_admin_id` (`accepted_admin_id`),
  ADD KEY `checked_admin_id` (`checked_admin_id`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `outgoings`
--
ALTER TABLE `outgoings`
  ADD PRIMARY KEY (`outgoing_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `perpared_admin_id` (`prepared_admin_id`),
  ADD KEY `accepted_admin_id` (`accepted_admin_id`),
  ADD KEY `checked_admin_id` (`checked_admin_id`);

--
-- Indexes for table `outgoing_items`
--
ALTER TABLE `outgoing_items`
  ADD PRIMARY KEY (`item_id`,`outgoing_id`),
  ADD KEY `outgoing_items_ibfk_2` (`outgoing_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `cheques`
--
ALTER TABLE `cheques`
  MODIFY `invoice_id` int(4) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `invoice_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `item_id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `outgoings`
--
ALTER TABLE `outgoings`
  MODIFY `outgoing_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `cheques`
--
ALTER TABLE `cheques`
  ADD CONSTRAINT `cheques_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `inovice_items`
--
ALTER TABLE `inovice_items`
  ADD CONSTRAINT `inovice_items_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `inovice_items_ibfk_3` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`prepared_admin_id`) REFERENCES `admins` (`admin_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `invoices_ibfk_2` FOREIGN KEY (`accepted_admin_id`) REFERENCES `admins` (`admin_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `invoices_ibfk_3` FOREIGN KEY (`checked_admin_id`) REFERENCES `admins` (`admin_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `outgoings`
--
ALTER TABLE `outgoings`
  ADD CONSTRAINT `outgoings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `outgoings_ibfk_2` FOREIGN KEY (`prepared_admin_id`) REFERENCES `admins` (`admin_id`),
  ADD CONSTRAINT `outgoings_ibfk_3` FOREIGN KEY (`accepted_admin_id`) REFERENCES `admins` (`admin_id`),
  ADD CONSTRAINT `outgoings_ibfk_4` FOREIGN KEY (`checked_admin_id`) REFERENCES `admins` (`admin_id`);

--
-- Constraints for table `outgoing_items`
--
ALTER TABLE `outgoing_items`
  ADD CONSTRAINT `outgoing_items_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `outgoing_items_ibfk_2` FOREIGN KEY (`outgoing_id`) REFERENCES `outgoings` (`outgoing_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
