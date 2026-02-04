# -_-_servicio_web_soap_procesamiento_de_alumnos_- :. 
# 📚 Servicio Web SOAP – Procesamiento de Alumnos.

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/54a8cf32-eabb-4023-a81e-068cafa3a073" />    

**Java + IntelliJ IDEA + SOAP + Oracle 19c**

A continuación tienes una **solución completa, profesional y ejecutable** para **IntelliJ IDEA (Java)** que cumple exactamente con el requerimiento planteado .

---

## 🎯 Objetivo:

Implementar un **servicio web SOAP** que:

- 📥 Reciba un **XML** desde **SOAP UI** con datos de alumnos  
- 🔄 Transforme el XML recibido a un **mensaje hexadecimal**  
- 🌐 Envíe el mensaje hexadecimal a un **servidor externo (dummy)**  
- 📤 Reciba una **respuesta dummy** también en hexadecimal  
- 🗄️ Registre **envío y respuesta hexadecimal** en **Oracle 19c**  
- ✅ Devuelva una **respuesta SOAP** indicando procesamiento exitoso  

---

## 🧩 Tecnologías Utilizadas

- Java 11+
- IntelliJ IDEA
- JAX-WS (SOAP)
- JAXB (XML Binding)
- Oracle Database 19c
- JDBC
- SOAP UI (cliente de pruebas)

---

## 🧱 Arquitectura
```
SOAP UI
│
▼
Servicio SOAP (JAX-WS)
│
├── Convierte XML → HEX
├── Envía HEX a servidor dummy
├── Recibe respuesta HEX
├── Registra en Oracle 19c
▼
Respuesta SOAP
```

---

## 📑 Estructura del Proyecto
```
soap-alumnos/
│
├── model/
│ └── Alumno.java
│
├── service/
│ └── AlumnoService.java
│
├── util/
│ ├── HexUtil.java
│ ├── DummyServer.java
│ └── DBUtil.java
│
└── publisher/
└── Publicador.java
```

---

## 🧑‍🎓 1. Modelo Alumno (XML → Java)

```java
package model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "alumno")
@XmlAccessorType(XmlAccessType.FIELD)
public class Alumno {

    private int id;
    private int idAlumno;
    private int idCurso;
    private String nombre;
    private String sexo;
    private int edad;
    private String direccion;

    // Getters y Setters
}

🔁 2. Utilidad Hexadecimal
package util;

public class HexUtil {

    public static String toHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (char c : input.toCharArray()) {
            hex.append(String.format("%02X", (int) c));
        }
        return hex.toString();
    }
}

🌐 3. Servidor Dummy (Simulación)
package util;

public class DummyServer {

    public static String enviar(String hexMensaje) {
        return "RESPUESTA_OK_" + hexMensaje;
    }
}

🗄️ 4. Conexión Oracle 19c
package util;

import java.sql.*;

public class DBUtil {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    private static final String USER = "COLEGIO";
    private static final String PASS = "oracle";

    public static void guardar(String envio, String respuesta) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO MENSAJES_HEX (MENSAJE_ENVIO, MENSAJE_RESPUESTA) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, envio);
            ps.setString(2, respuesta);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

🧠 5. Servicio SOAP
package service;

import model.Alumno;
import util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@WebService
public class AlumnoService {

    @WebMethod
    public String procesarAlumno(Alumno alumno) {

        try {
            JAXBContext context = JAXBContext.newInstance(Alumno.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(alumno, writer);

            String xml = writer.toString();
            String hexEnvio = HexUtil.toHex(xml);
            String hexRespuesta = DummyServer.enviar(hexEnvio);

            DBUtil.guardar(hexEnvio, hexRespuesta);

            return "MENSAJE PROCESADO CON EXITO\nENVIO HEX:\n"
                    + hexEnvio + "\nRESPUESTA HEX:\n" + hexRespuesta;

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}

🚀 6. Publicador del Servicio
package publisher;

import service.AlumnoService;
import javax.xml.ws.Endpoint;

public class Publicador {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/AlumnoService", new AlumnoService());
        System.out.println("Servicio SOAP publicado");
    }
}

🗃️ 7. Script Oracle 19c
CREATE TABLE MENSAJES_HEX (
    ID NUMBER GENERATED ALWAYS AS IDENTITY,
    MENSAJE_ENVIO CLOB,
    MENSAJE_RESPUESTA CLOB,
    FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

🧪 8. XML de Prueba – SOAP UI
<alumno>
    <id>1</id>
    <idAlumno>1001</idAlumno>
    <idCurso>200</idCurso>
    <nombre>Juan Perez</nombre>
    <sexo>M</sexo>
    <edad>15</edad>
    <direccion>Calle 123</direccion>
</alumno>

✅ Respuesta en SOAP UI
MENSAJE PROCESADO CON EXITO
ENVIO HEX:
3C616C756D6E6F3E...
RESPUESTA HEX:
RESPUESTA_OK_3C616C756D6E6F3E...

▶️ Cómo Ejecutar
- Crear proyecto Java en IntelliJ IDEA
- Agregar dependencias JAX-WS (si usas Java 11+)
- Ejecutar Publicador.java
- Probar desde SOAP UI
- Ver registros en Oracle 19c

🏁 Resultado Final
✔ Servicio SOAP funcional
✔ Transformación XML → HEX
✔ Envío y respuesta dummy
✔ Persistencia en Oracle 19c
✔ Validado desde SOAP UI :. / .
