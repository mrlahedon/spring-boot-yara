package com.hedon.springbootyara.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageRepository{

    private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

    @Override
    public void init() {
        try {
			Files.createDirectories(rootLocation);
			System.out.println("Lokasi file init : "+ this.rootLocation.toString());
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
    }

    @Override
    public void store(MultipartFile file) {
        try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
    }

    @Override
    public Stream<Path> loadAll() {
        try {
			System.out.println("Lokasi file loadALL : "+ this.rootLocation.toString());

			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

	@Override
	public void deleteFile(String filename) {
		Path fileToDeletePath = load(filename);
		FileSystemUtils.deleteRecursively(fileToDeletePath.toFile());

	}

	@Override
	public void copyFile(String filePath) {
		Path fileToCopyPath = Paths.get(filePath);
		Path destPath = Paths.get("." + File.separator + fileToCopyPath.getFileName().toString());
		
		if(destPath.toFile().exists()) {
			System.out.println("Nama file : "+ fileToCopyPath.getFileName().toString() + " , " + destPath.toString());
		}

		try {
			if(!destPath.toFile().exists()) {
				Files.copy(fileToCopyPath, destPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to copy files", e);
		}
	}
    
}
