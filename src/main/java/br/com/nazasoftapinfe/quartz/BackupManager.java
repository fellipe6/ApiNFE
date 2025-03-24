package br.com.nazasoftapinfe.quartz;

import br.com.nazasoftapinfe.service.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Component
public class BackupManager {
    private final LogService logService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public BackupManager(LogService logService) {
        this.logService = logService;
    }

    @Scheduled(cron = "0 0 */2 * * ?") // Executa a cada 2 horas
    public void executarBackup() {
        try {
            // Extrai o nome do banco de dados da URL
            String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

            // Obtém o diretório onde o JAR está rodando
            String rootPath = System.getProperty("user.dir");
            String backupDirPath = rootPath + File.separator + "backup";

            // Cria a pasta de backup se não existir
            File backupDir = new File(backupDirPath);
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs();
                if (created) {
                    System.out.println("Diretório de backup criado: " + backupDirPath);
                    logService.salvarLog("INFO", "Diretório de backup criado: " + backupDirPath,null,null);
                } else {
                    System.err.println("Falha ao criar diretório de backup.");
                    logService.salvarLog("ERROR", "Falha ao criar diretório de backup.",null,null);
                    return;
                }
            }

            // Define o nome do arquivo de backup com timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFile = backupDirPath + File.separator + "backup_" + timestamp + ".sql";

            // Monta o comando para executar o backup
            String command = String.format("PGPASSWORD=%s pg_dump -h localhost -U %s -d %s -F p -f %s",
                    dbPassword, dbUser, dbName, backupFile);

            // Executa o comando
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            // Captura saída de erro
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup salvo em: " + backupFile);
                logService.salvarLog("INFO", "Backup salvo em: " + backupFile,null,null);
            } else {
                System.err.println("Erro ao realizar o backup.");
                logService.salvarLog("ERROR", "Erro ao realizar o backup.",null,null);
                System.err.println("Saída do erro: \n" + errorOutput.toString());
                logService.salvarLog("ERROR", "Saída do erro: \n" + errorOutput.toString(),null,null);
            }

            // Após o backup, excluir arquivos antigos (mais de 7 dias)
            excluirBackupsAntigos(backupDirPath);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void excluirBackupsAntigos(String backupDirPath) {
        try {
            File backupDir = new File(backupDirPath);
            File[] arquivos = backupDir.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    // Verifica se o arquivo é um arquivo de backup (com extensão .sql)
                    if (arquivo.isFile() && arquivo.getName().endsWith(".sql")) {
                        // Converte o tempo de modificação para Instant
                        Instant lastModifiedTime = Instant.ofEpochMilli(arquivo.lastModified());
                        Instant now = Instant.now();

                        // Calcula a diferença em dias
                        long diasDiferenca = ChronoUnit.DAYS.between(lastModifiedTime, now);

                        // Se o arquivo tiver mais de 7 dias, exclui
                        if (diasDiferenca > 7) {
                            boolean excluido = arquivo.delete();
                            if (excluido) {
                                System.out.println("Backup excluído: " + arquivo.getName());
                                logService.salvarLog("INFO", "Backup excluído: " + arquivo.getName(),null,null);
                            } else {
                                System.err.println("Falha ao excluir o backup: " + arquivo.getName());
                                logService.salvarLog("ERROR", "Falha ao excluir o backup: " + arquivo.getName(),null,null);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
