# Socket in Java

Qui proponiamo alcuni esempi di socket in java. 

**0)** Info sul socket
```
SocketInfo.java
```

**1)** Il primo esempio potremmo definirlo un prototipo di client web.
```
DateClient.java
```
Pecedentemente abbiamo messo 
```
date.php
```
nella cartella del server web Apache.

**2)** Nel secondo esempio ancora un client ma questa volta per netcat
```
NcClient.java
```
Usa netcat come server (opzione -l).

**3)** I primi server
```
DateServer.java
DateTimeServer.java
Echo.java
```
il secondo usa netcat come client.

**4)** Un esempio completo con client e server
```
CapitalizeServer.java
CapitalizeClient.java
```
**5)** Copiare a byte, con bufferizzazione, un file da un host ad un altro via tcp
```
ServerFiles.java
ClientFile.java
```



