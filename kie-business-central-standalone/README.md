# KIE Business Central

```
mvn clean package
```

```
cd target\wildfly-19.1.0.Final\bin
```

execute `add-user` script in `bin` to add an application user with role `admin,manager,user,analyst,developer,kie-server`

And run the `standalone` script, After it starts go to `http://localhost:8080/business-central` and login with application user created
