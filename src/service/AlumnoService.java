package service;

import util.*;
import model.Alumno;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;

import javax.xml.transform.dom.DOMSource; // 🔹 línea adicionada
// ya no necesitas ByteArrayInputStream

@ServiceMode(Service.Mode.MESSAGE)
public class AlumnoService implements Provider<SOAPMessage> {

    @Override
    public SOAPMessage invoke(SOAPMessage request) {

        try {
            SOAPBody body = request.getSOAPBody();
            SOAPElement alumnoElement = (SOAPElement) body.getFirstChild();

            JAXBContext context = JAXBContext.newInstance(Alumno.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // 🔹 Aquí la línea corregida:
            Alumno alumno = (Alumno) unmarshaller.unmarshal(new DOMSource(alumnoElement));

            String xml = alumno.toString();
            String hexEnvio = HexUtil.toHex(xml);
            String hexRespuesta = DummyServer.enviar(hexEnvio);

            DBUtil.guardar(hexEnvio, hexRespuesta);

            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage response = factory.createMessage();
            SOAPBody responseBody = response.getSOAPBody();

            SOAPElement result = responseBody.addChildElement("resultado");
            result.addTextNode("PROCESADO OK");

            response.saveChanges();
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error procesando SOAP", e);
        }
    }
}