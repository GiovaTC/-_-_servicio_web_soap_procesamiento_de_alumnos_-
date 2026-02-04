package service;

import util.*;
import model.Alumno;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;

import javax.xml.transform.dom.DOMSource;

@ServiceMode(Service.Mode.MESSAGE)
public class AlumnoService implements Provider<SOAPMessage> {

    @Override
    public SOAPMessage invoke(SOAPMessage request) {
        try {
            // 🔹 Log para depuración
            System.out.println("➡️ Procesando request SOAP en AlumnoService...");
            request.writeTo(System.out);

            SOAPBody body = request.getSOAPBody();
            if (body == null || body.getFirstChild() == null) {
                throw new RuntimeException("El SOAPBody está vacío o no contiene elementos.");
            }

            SOAPElement alumnoElement = (SOAPElement) body.getFirstChild();

            JAXBContext context = JAXBContext.newInstance(Alumno.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // 🔹 Unmarshal directo desde el DOM del SOAPElement
            Alumno alumno = (Alumno) unmarshaller.unmarshal(new DOMSource(alumnoElement));

            System.out.println("✅ Alumno unmarshalled: " + alumno);

            // 🔹 Procesamiento de negocio
            String xml = alumno.toString();
            String hexEnvio = HexUtil.toHex(xml);
            String hexRespuesta = DummyServer.enviar(hexEnvio);

            DBUtil.guardar(hexEnvio, hexRespuesta);

            // 🔹 Construcción de la respuesta SOAP completa
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage response = factory.createMessage();
            SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
            SOAPBody responseBody = envelope.getBody();

            // Elemento de respuesta según WSDL
            SOAPElement responseElement = responseBody.addChildElement(
                    "procesarAlumnoResponse", "ns1", "http://service.alumno/"
            );
            responseElement.addChildElement("resultado").addTextNode("PROCESADO OK");

            response.saveChanges();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error procesando SOAP", e);
        }
    }
}