package service;

import model.Alumno;
import util.DBUtil;
import util.DummyServer;
import util.HexUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

@ServiceMode(Service.Mode.MESSAGE)
public class AlumnoService implements Provider<SOAPMessage> {

    @Override
    public SOAPMessage invoke(SOAPMessage request) {
        try {
            System.out.println("➡️ Procesando request SOAP en AlumnoService...");
            request.writeTo(System.out);

            SOAPBody body = request.getSOAPBody();

            SOAPElement alumnoElement =
                    (SOAPElement) body.getChildElements().next();

            JAXBContext context = JAXBContext.newInstance(Alumno.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Alumno alumno =
                    (Alumno) unmarshaller.unmarshal(new DOMSource(alumnoElement));

            System.out.println("✅ Alumno unmarshalled: " + alumno);

            // Lógica de negocio
            String xml = alumno.toString();
            String hexEnvio = HexUtil.toHex(xml);
            String hexRespuesta = DummyServer.enviar(hexEnvio);
            DBUtil.guardar(hexEnvio, hexRespuesta);

            // 🔴 SOAP 1.1 EXPLÍCITO (CLAVE)
            MessageFactory factory =
                    MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            SOAPMessage response = factory.createMessage();
            SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
            SOAPBody responseBody = envelope.getBody();

            SOAPElement responseElement = responseBody.addChildElement(
                    "procesarAlumnoResponse", "ns1", "http://service.alumno/"
            );

            responseElement.addChildElement("resultado")
                    .addTextNode("PROCESADO OK");

            response.saveChanges();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error procesando SOAP", e);
        }
    }
}
