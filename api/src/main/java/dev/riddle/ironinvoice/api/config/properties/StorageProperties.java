package dev.riddle.ironinvoice.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(

	@DefaultValue("/data/uploads")
	Path uploadsRoot,

	@DefaultValue("10485760")
	long maxBytes,

	@DefaultValue("10")
	int sampleLimit
) {}
