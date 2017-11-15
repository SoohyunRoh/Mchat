Mchat - Chatting Single page app
======================
## 1. Components
WAS: Tomcat 8.5<br>
DB: MySQL 5.7<br>
Persistance Framework: MyBatis 3.2<BR>
Application Framework: Spring Framework 4.0<br>
Build automation tool: Maven<br>
Javascript Framework: AngularJS 1.5.7<br>
Software configuration management: None (Solo project)<br>
CSS Framework: Materialize 0.98.2<Br>
+WebSocket<BR>
<br>
  
## 2. Source explain
See wiki.

## 3. Server launching problems.
I tried to upload the project to Amazon AWS and start an instance. However, the browser can't send **websocket** message to the server, because Listener at Amazon Load Balancer is not open for websocket (It makes 400 error). So, I changed setting 442 port protocol to TCP(secure) and HTTPS. Both protocols need SSL certification, and Amazone provides "AWS Certificate Manager(ACM)." But I don't have any domain, so I can't get the ACM.
<bR>
Therefore, wiki shows source explain on localhost. If I do other interesting projects, I will buy a domain for that and this project.
