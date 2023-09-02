package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal servidor HTTP
 */
public class HttpServer {

    public static Map<String, MethodGet> URIstoAttend = new HashMap<String, MethodGet>();

    public static void addURIstoAttend(String uri, MethodGet method) {
        URIstoAttend.put(uri, method);
    }

    public static void setURIstoAttend(Map URIstoAttend) {
        HttpServer.URIstoAttend = URIstoAttend;
    }

    public static void main(String[] args) {

        HttpServer.get("/hello", (request, response) -> {
            //
            return "Hello World :)";
        });
        HttpServer.get("/", (request, response) -> {
            //
            return getIndexResponse();
        });
        HttpServer.get("/index.html", (request, response) -> {
            //
            return "/index.html";
        });
        HttpServer.get("/escuelaing.png", (request, response) -> {
            //
            return "/escuelaing.png";
        });
        HttpServer.get("/nala.jpg", (request, response) -> {
            //
            return "/nala.jpg";
        });
        HttpServer.get("/index.css", (request, response) -> {
            //
            return "/index.css";
        });
        HttpServer.get("/profe.jpg", (request, response) -> {
            //
            return "/profe.jpg";
        });
        HttpServer.get("/sistemas.jpg", (request, response) -> {
            //
            return "/sistemas.jpg";
        });

        try {
            HttpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retorna una página HTML.
     *
     * @return Respuesta HTTP con la página de inicio.
     */
    public static String getIndexResponse() {
        String response = "HTTP/1.1 200 OK"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>MICROFRAMEWORKS WEB</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>MICROFRAMEWORKS WEB</h1>\n" +
                "    </body>\n" +
                "</html>";
        return response;
    }

    /**
     * Método principal para iniciar el servidor HTTP.
     *
     * @throws IOException Si ocurre un error al configurar o aceptar conexiones.
     */
    public static void start() throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        while (!serverSocket.isClosed()) {
            try {
                System.out.println("Operando MICROFRAMEWORKS WEB ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine = "";
            boolean firstLine = true;
            String uriString = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (firstLine) {
                    firstLine = false;
                    uriString = inputLine.split(" ")[1];

                }
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println("URI: " + uriString);
            String responseBody = "";

            for (String uriInCatalog : URIstoAttend.keySet()) {
                if (uriString.equals(uriInCatalog)) {
                    WebRequest request = new WebRequest();
                    WebResponse response = new WebResponse();
                    System.out.println(URIstoAttend.get(uriInCatalog).getMethod(request, response));
                    System.out.println(uriInCatalog);
                    if (URIstoAttend.get(uriInCatalog).getMethod(request, response).equals(uriInCatalog)) {
                        outputLine = searchFileInPublic(uriString, responseBody, outputLine, clientSocket);
                    } else {
                        URIstoAttend.get(uriInCatalog).getMethod(request, response);
                        responseBody = URIstoAttend.get(uriInCatalog).getMethod(request, response);
                        System.out.println("ResponseBody: " + responseBody);
                        outputLine = getLine(responseBody);
                    }
                }
            }

            out.println(outputLine);
            out.close();
            in.close();
        }
        clientSocket.close();
        serverSocket.close();
    }

    private static String searchFileInPublic(String uriString, String responseBody, String outputLine, Socket clientSocket) throws IOException {
        if (uriString != null && !getFile(uriString).equals("Not Found")) {
            responseBody = getFile(uriString);
            outputLine = getLine(responseBody);
        } else if (uriString != null && uriString.split("\\.")[1].equals("jpg") ||
                uriString.split("\\.")[1].equals("png")) {
            OutputStream outputStream = clientSocket.getOutputStream();
            File file = new File("src/main/resources/public/" + uriString);
            BufferedImage bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            ImageIO.write(bufferedImage, uriString.split("\\.")[1], byteArrayOutputStream);
            outputLine = getImg("");
            dataOutputStream.writeBytes(outputLine);
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            System.out.println(outputLine);

        }
        return outputLine;
    }

    /**
     * Método para obtener un archivo estático
     *
     * @param route String de la ruta para buscar fichero
     * @return los datos del fichero en un String
     */
    public static String getFile(String route) {
        Path file = FileSystems.getDefault().getPath("src/main/resources/public", route);
        Charset charset = Charset.forName("US-ASCII");
        String web = new String();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                web += line + "\n";
            }
        } catch (IOException x) {
            web = "Not Found";
        }
        return web;
    }


    public static String getLine(String responseBody) {
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html \r\n"
                + "\r\n"
                + "\n"
                + responseBody;
    }

    private static String getImg(String responseBody) {
        System.out.println("response Body" + responseBody);
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: image/jpg \r\n"
                + "\r\n";
    }

    public static void get(String uri, MethodGet methodGet) {
        addURIstoAttend(uri, methodGet);
    }
}

