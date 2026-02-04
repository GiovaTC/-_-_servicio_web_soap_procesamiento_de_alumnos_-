package service;

import model.Alumno;
import util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class AlumnoService {

    // Operación SOAP expuesta por Endpoint (sin anotaciones)
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
