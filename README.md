# Bus-Ticketing-System-Back-End

INITIAL DATABASE SCRIPT:
DROP DATABASE IF EXISTS ticketing_system;
CREATE DATABASE IF NOT EXISTS ticketing_system DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

USE ticketing_system;

CREATE USER IF NOT EXISTS 'DB_Ticketing_System_Project'@'localhost' IDENTIFIED BY 'Tn65z6&dDObh@YJRRt39OwhV';
GRANT ALL ON ticketing_system.* TO 'DB_Ticketing_System_Project'@'localhost';
