package org.example;

/**
 * La interfaz MethodGet define un contrato para las clases que desean implementar
 * un método GET en una aplicación web.
 */
public interface MethodGet {
    /**
     * Este método se utiliza para manejar las solicitudes HTTP GET.
     *
     * @param request  Objeto de tipo WebRequest que representa la solicitud HTTP entrante.
     * @param response Objeto de tipo WebResponse que representa la respuesta HTTP que se enviará.
     * @return Una cadena que contiene la respuesta generada para la solicitud GET.
     */
    public String getMethod(WebRequest request, WebResponse response);
}
