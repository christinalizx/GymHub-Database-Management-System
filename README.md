# CS5200-Final-Project

## gym_users
### Create
Inside the "register" function.

### Read, Update, Delete
All inside "User Information" section.
Note, everytime I try to update the gym membership, it will generate an error:
```text
com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`gymhubdb`.`gym_users`, CONSTRAINT `gym_users_gym_id_fk` FOREIGN KEY (`gym_id`) REFERENCES `gyms` (`gym_id`) ON DELETE CASCADE)
	at ...
```
I think it's possible that when you try other methods involving updating something that is a foreign key, it may pop up. 

## Forums
So far only has spinner displaying what forums we have.
