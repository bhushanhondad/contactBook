# contactBook

Developed a CRUD based APIs for a contact book app.


- APIs support adding/editing/deleting contacts.

- Can be searched by name and email address.

- Search supports pagination and return 10 items by default per page.

- Add unit tests and Integration tests for each functionality.

- Add basic authentication for the app userName and password is take as header as plain text and vailidate with DB


# Language Used: Java
# DB Used: Mysql
# ORM Used: Hibernate

Tables created User Table:

mysql> select * from user;
+-----------+------------+
| userName  | password   |
+-----------+------------+
| demouser  | password   |
| demouser1 | password1  |
| messi     | barcelon10 |
+-----------+------------+
3 rows in set (0.00 sec)


Contacts Table:
+---------------------------+------------+---------------+
| emailId                   | userName   | belongsToUser |
+---------------------------+------------+---------------+
| demouser1@demouser1.com   | demouser   | demouser1     |
| demouser10@demouser10.com | demouser   | demouser1     |
| demouser11@demouser11.com | demouser11 | demouser      |
| demouser12@demouser12.com | demouser11 | demouser      |
| demouser13@demouser13.com | demouser11 | demouser      |
| demouser14@demouser14.com | demouser11 | demouser      |
| demouser15@demouser15.com | demouser11 | demouser      |
| demouser16@demouser16.com | demouser11 | demouser      |
| demouser17@demouser17.com | demouser11 | demouser      |
| demouser18@demouser18.com | demouser11 | demouser      |
| demouser19@demouser19.com | demouser11 | demouser      |
| demouser2@demouser2.com   | demouser   | demouser1     |
| demouser20@demouser20.com | demouser11 | demouser      |
| demouser21@demouser21.com | demouser11 | demouser      |
| demouser22@demouser22.com | demouser11 | demouser      |
| demouser23@demouser23.com | demouser11 | demouser      |
| demouser24@demouser24.com | demouser11 | demouser      |
| demouser3@demouser3.com   | demouser   | demouser1     |
| demouser4@demouser4.com   | demouser   | demouser1     |
| demouser5@demouser5.com   | demouser   | demouser1     |
| demouser6@demouser6.com   | demouser   | demouser1     |
| demouser7@demouser7.com   | demouser   | demouser1     |
| demouser8@demouser8.com   | demouser   | demouser1     |
| demouser9@demouser9.com   | demouser   | demouser1     |
| test1@test1.com           | xyzxyzxyz  | demouser      |
| test10@test10.com         | demouser10 | demouser      |
| test2@test2.com           | demouser1  | demouser      |
| test3@test3.com           | demouser3  | demouser      |
| test4@test4.com           | demouser4  | demouser      |
| test5@test5.com           | demouser5  | demouser      |
| test6@test6.com           | demouser6  | demouser      |
| test7@test7.com           | demouser7  | demouser      |
| test8@test8.com           | demouser8  | demouser      |
| test9@test9.com           | demouser9  | demouser      |
+---------------------------+------------+---------------+

