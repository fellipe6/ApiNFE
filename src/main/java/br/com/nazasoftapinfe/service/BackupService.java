package br.com.nazasoftapinfe.service;


import br.com.nazasoftapinfe.quartz.BackupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BackupService {

    private final BackupManager backupManager;
    private final LogService logService;

    @Autowired
    public BackupService(BackupManager backupManager, LogService logService) {
        this.backupManager = backupManager;
        this.logService = logService;
    }

    @Scheduled(cron = "0 0 */2 * * ?") // Executa a cada 2 horas
   //@Scheduled(cron = "0 * * * * ?") // Executa a cada 1 minuto
    public void executarBackup() {
        // Realiza o backup
        backupManager.executarBackup();
        logService.salvarLog("INFO", "Backup realizado com sucesso.", null, null);

        // Após o backup, excluir arquivos antigos (mais de 7 dias)
        String backupDirPath = System.getProperty("user.dir") + "/backup";
        backupManager.excluirBackupsAntigos(backupDirPath);
        logService.salvarLog("INFO", "Backups antigos excluídos.", null, null);

    }
}