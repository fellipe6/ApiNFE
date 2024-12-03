package br.com.nazasoftapinfe;

import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@SpringBootTest
class ApiNfeApplicationTests {

	public static void main(String[] args) throws IOException {
		System.out.println(fileToByte("/home/fellipe/Documentos/172816178_SA_CENTRO_REGIONAL_INTEGRADO_DE_ONCOLOGIA_CRIO_07990336000198.pfx/"));
	}
	private static String fileToByte(String urlArquivo) throws IOException{
		byte[] fileContent = Files.readAllBytes(new File(urlArquivo).toPath());
		return Base64.getEncoder().encodeToString(fileContent);
	}
}
