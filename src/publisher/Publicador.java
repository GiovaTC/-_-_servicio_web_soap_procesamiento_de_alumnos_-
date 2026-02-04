package publisher;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.soap.*;
import java.io.*;
import java.net.InetSocketAddress;

public class Publicador {

    // 🔹 RUTA ABSOLUTA DEL WSDL
    private static final String WSDL_PATH =
            "C:/Users/USUARIO/IdeaProjects/soap_alumnos/src/resources/AlumnoService.wsdl";

    public static void main(String[] args) throws Exception {

        File wsdlFile = new File(WSDL_PATH);
        System.out.println("Ruta WSDL usada: " + wsdlFile.getAbsolutePath());

        if (!wsdlFile.exists()) {
            System.err.println("❌ ERROR: WSDL no encontrada");
            return;
        }

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/AlumnoService", new SoapHandler());
        server.start();

        System.out.println("SOAP PURO publicado en:");
        System.out.println("http://localhost:8080/AlumnoService");
        System.out.println("WSDL disponible en:");
        System.out.println("http://localhost:8080/AlumnoService?wsdl");
    }

    static class SoapHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String query = exchange.getRequestURI().getQuery();

            // 🔹 WSDL
            if (query != null && query.toLowerCase().contains("wsdl")) {

                File wsdlFile = new File(WSDL_PATH);

                byte[] bytes = java.nio.file.Files.readAllBytes(wsdlFile.toPath());

                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
                return;
            }

            // 🔹 SOAP Request normal
            try {
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage request =
                        factory.createMessage(null, exchange.getRequestBody());

                SOAPMessage response = factory.createMessage();
                SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();

                SOAPBodyElement result =
                        body.addBodyElement(
                                envelope.createName(
                                        "procesarAlumnoResponse",
                                        "ns1",
                                        "http://service.alumno/"
                                )
                        );

                result.addChildElement("resultado")
                        .addTextNode("PROCESADO OK");

                response.saveChanges();

                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, 0);
                response.writeTo(exchange.getResponseBody());
                exchange.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }
    }
}
