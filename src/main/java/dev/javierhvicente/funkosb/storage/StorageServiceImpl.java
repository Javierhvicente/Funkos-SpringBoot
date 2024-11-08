package dev.javierhvicente.funkosb.storage;

import dev.javierhvicente.funkosb.storage.controller.StorageController;
import dev.javierhvicente.funkosb.storage.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    private final Path fileStorageLocation;

    public StorageServiceImpl(@Value("funkos-image") String path) {
        this.fileStorageLocation = Paths.get(path);
    }

    @Override
    public void init() {
        log.info("Inicializando almacenamiento");
        try {
            Files.createDirectories(fileStorageLocation);
        }catch (IOException e) {
            throw new StorageException.StorageInternal("No se puede inicializar el almacenamiento " + e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFileName = filename.replace("." + extension, "");
        String storedFileName = System.currentTimeMillis() + "_" + justFileName + "." + extension;
        try {
            if(file.isEmpty()){
                throw new StorageException.StorageBadRequest("Fichero vacío" + filename);
            }
            if(filename.contains("..")){
                throw new StorageException.StorageBadRequest("Nombre de fichero ilegal: " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                log.info("Almacenando fichero: " + filename + " como " + storedFileName);
                Files.copy(inputStream, this.fileStorageLocation.resolve(storedFileName),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFileName;
            }
        }catch (IOException e) {
            throw new StorageException.StorageInternal("Fallo al almacenar fichero " + e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        log.info("Cargando todos los ficheros almacenaods");
        try {
            return Files.walk(this.fileStorageLocation, 1)
                   .filter(path -> !path.equals(this.fileStorageLocation))
                   .map(this.fileStorageLocation::relativize);
        }catch (IOException e){
            throw new StorageException.StorageInternal("Fallo al cargar ficheros " + e);
        }
    }

    @Override
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return fileStorageLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero " + filename);
        try {
            Path filePath = load(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else {
                throw new StorageException.StorageFileNotFound("No se puede leer fichero: " + filename);
            }
        }catch (MalformedURLException e){
            throw new StorageException.StorageFileNotFound("No se puede leer el fichero: " + filename + " " + e);
        }
    }

    @Override
    public void delete(String fileName) {
        String justFilename = StringUtils.getFilename(fileName);
        try {
            log.info("Eliminando fichero " + justFilename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        }catch (IOException e){
            throw new StorageException.StorageInternal("Fallo al eliminar fichero " + fileName + " " + e);
        }
    }

    @Override
    public void deleteAll() {
        log.info("Eliminando los ficheros almacenados");
        FileSystemUtils.deleteRecursively(fileStorageLocation.toFile());
    }

    @Override
    public String getUrl(String filename) {
        log.info("Obteniendo URL del fichero " + filename);
        return MvcUriComponentsBuilder
                .fromMethodName(StorageController.class, "serverFile" ,filename, null)
                .build().toUriString();
    }
}
