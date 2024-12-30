package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.LogEntry;
import br.com.nazasoftapinfe.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Enumeration;



@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void salvarLog(String level, String message, String exception,String clientIp) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(level);
        logEntry.setMessage(message);
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setException(exception);
        logEntry.setIp(getServerIp()); // Obtém o IP do servidor
        logEntry.setIp(clientIp);

        logRepository.save(logEntry);
    }

    private String getServerIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();

            // Se não for localhost, retorna diretamente
            if (!localHost.isLoopbackAddress()) {
                return localHost.getHostAddress();
            }

            // Percorre interfaces de rede para encontrar um IP real
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Ignora interfaces de loopback e inativas
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) { // Filtra IPv4
                        return address.getHostAddress();
                    }
                }
            }

            return "IP do servidor não encontrado";
        } catch (UnknownHostException | SocketException e) {
            return "Erro ao obter IP do servidor";
        }
    }
}