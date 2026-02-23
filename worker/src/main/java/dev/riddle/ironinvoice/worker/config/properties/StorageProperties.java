package dev.riddle.ironinvoice.worker.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(

	@DefaultValue("/data/uploads")
	Path uploadsRoot
) {}
