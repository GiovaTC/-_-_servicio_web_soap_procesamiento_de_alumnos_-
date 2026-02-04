package publisher;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.soap.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;

public class Publicador {

    public static void main(String[] args) throws Exception {

        // 🔎 DIAGNÓSTICO CLAVE: verificar si la WSDL está en el classpath
        URL wsdlUrl = Publicador.class.getResource("/AlumnoService.wsdl");
        System.out.println("Ruta WSDL detectada: " + wsdlUrl);

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/AlumnoService", new SoapHandler());
        server.setExecutor(null); // executor por defecto
        server.start();

        System.out.println("SOAP PURO publicado en:");
        System.out.println("http://localhost:8080/AlumnoService");
        System.out.println("WSDL disponible en:");
        System.out.println("http://localhost:8080/AlumnoService?wsdl");
    }

    static class SoapHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // 🔹 Si piden el WSDL
            if (exchange.getRequestURI().getQuery() != null &&
                    exchange.getRequestURI().getQuery().equalsIgnoreCase("wsdl")) {

                InputStream wsdl =
                        Publicador.class
                                .getResourceAsStream("/AlumnoService.wsdl");

                if (wsdl == null) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                    return;
                }

                byte[] bytes = wsdl.readAllBytes();

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

                SOAPElement value = result.addChildElement("resultado");
                value.addTextNode("PROCESADO OK");

                response.saveChanges();

                exchange.getResponseHeaders()
                        .set("Content-Type", "text/xml; charset=utf-8");

                exchange.sendResponseHeaders(200, 0);
                response.writeTo(exchange.getResponseBody());
                exchange.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
}
