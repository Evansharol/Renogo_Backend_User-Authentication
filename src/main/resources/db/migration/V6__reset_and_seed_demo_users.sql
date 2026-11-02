TRUNCATE TABLE users RESTART IDENTITY;

INSERT INTO users (user_id, name, email, role, password, status)
VALUES
    ('A001', 'Amelia Martin', 'amelia.martin@renogo.com', 'admin',
     'pbkdf2-sha256$310000$OllsuLUGE4gb3dc2w3V08Q==$1CWXzfuR5fZsczvDUOG8UWvLjFxMgjKgChuz9Wy7RLA=', 'Active'),
    ('D001', 'Dinesh Kumar', 'sofia.garcia@renogo.com', 'dealer',
     'pbkdf2-sha256$310000$2dgXpvATEz6PSlbMm1URvg==$Y0yZd1pr+JM/lNQXIhThKZV9Fhb4qnOQrv8MGqI5b7Y=', 'Active'),
    ('M001', 'Noah Wilson', 'shin@renogo.com', 'manufacturer',
     'pbkdf2-sha256$310000$WqhjkBtnJKcCrB5dSVuwoQ==$UwXKtRzrlsJPrqoiLO5eqFFoiLaaVQLOy2w4dRbaHAo=', 'Active'),
    ('F001', 'Louis Bernard', 'louis.bernard@renogo.com', 'financehead',
     'pbkdf2-sha256$310000$Ya3oFLsRvqGH0hO1a3kKrg==$9vRoxYxv6z1pUPckhN39O+nAowgaLfxoJWawCoIvN2U=', 'Active'),
    ('SM001', 'Rajesh Kumar', 'nirmala@renogo.com', 'salesmanager',
     'pbkdf2-sha256$310000$yihg9RQx5Nsr5ZV7Etnrcg==$FaNN1ZyULjclp4SbGmH0dhmRASSsTbjcCW7XzoL1Ahs=', 'Active'),
    ('DM001', 'Karthik Raja', 'riyaz@renogo.com', 'dealermanager',
     'pbkdf2-sha256$310000$hH5XMg/LNIEkXqBOBvJpiw==$fJdxnUJCfj7gzOAzwm3EtCSBPl+NlRiHWW0H4Z5OZFA=', 'Active');
