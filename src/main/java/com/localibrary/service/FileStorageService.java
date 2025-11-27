package com.localibrary.service;

import com.localibrary.enums.TipoUpload;
import com.localibrary.exception.StorageException;
import com.localibrary.util.Constants;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;

    // Injeta o diretório raiz (definido no application.properties)
    public FileStorageService(@Value("${app.upload.dir:" + Constants.UPLOAD_DIR + "}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        initDirectories();
    }

    // Cria a estrutura de pastas ao iniciar: /uploads/capas, /uploads/autores, etc.
    private void initDirectories() {
        try {
            Files.createDirectories(rootLocation);
            for (TipoUpload tipo : TipoUpload.values()) {
                Files.createDirectories(rootLocation.resolve(tipo.getDiretorio()));
            }
        } catch (IOException ex) {
            throw new StorageException("Não foi possível criar os diretórios de upload.", ex);
        }
    }

    public String storeFile(MultipartFile file, TipoUpload tipo) {
        // Protege contra filename nulo
        String originalFilenameRaw = file.getOriginalFilename();
        if (originalFilenameRaw == null || originalFilenameRaw.isBlank()) {
            throw new StorageException(Constants.MSG_NOME_ARQUIVO_AUSENTE);
        }

        // Verifica tamanho máximo
        if (file.getSize() > Constants.MAX_FILE_SIZE) {
            throw new StorageException("Arquivo maior que o tamanho máximo permitido: " + Constants.MAX_FILE_SIZE + " bytes");
        }

        // Verifica tipo permitido (quando disponível)
        String contentType = file.getContentType();
        if (contentType != null) {
            boolean allowed = false;
            for (String allowedType : Constants.ALLOWED_IMAGE_TYPES) {
                if (allowedType.equalsIgnoreCase(contentType)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new StorageException("Tipo de arquivo não suportado: " + contentType);
            }
        }

        // Normaliza o nome do arquivo
        String originalFilename = StringUtils.cleanPath(originalFilenameRaw);

        // Validação de segurança básica
        if (originalFilename.contains("..")) {
            throw new StorageException("Nome de arquivo inválido: " + originalFilename);
        }

        // Gera nome único: uuid.jpg (usando concatenação implícita para evitar warning desnecessário)
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        String fileName = UUID.randomUUID() + extension;

        // Define o caminho final: uploads/capas/uuid.jpg
        Path targetLocation = this.rootLocation.resolve(tipo.getDiretorio()).resolve(fileName);

        try {
            // Redimensiona e salva
            Thumbnails.of(file.getInputStream())
                    .size(tipo.getLargura(), tipo.getAltura())
                    .outputQuality(0.9) // 90% de qualidade (bom equilíbrio)
                    .toFile(targetLocation.toFile());

            // Retorna o caminho relativo para salvar no banco (ex: capas/uuid.jpg)
            return tipo.getDiretorio() + "/" + fileName;

        } catch (IOException ex) {
            throw new StorageException("Falha ao armazenar arquivo " + fileName, ex);
        }
    }
}