package dev.riddle.ironinvoice.worker.uploads.application;

import dev.riddle.ironinvoice.shared.uploads.contracts.CsvScan;
import dev.riddle.ironinvoice.shared.invoices.InvoiceRowDetails;
import dev.riddle.ironinvoice.worker.config.properties.StorageProperties;
import dev.riddle.ironinvoice.shared.uploads.exceptions.InvalidCsvException;
import dev.riddle.ironinvoice.worker.uploads.application.exceptions.UploadNotFoundException;
import dev.riddle.ironinvoice.worker.uploads.persistence.UploadEntity;
import dev.riddle.ironinvoice.worker.uploads.persistence.UploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadJobService {

	private final UploadRepository uploadRepository;
	private final StorageProperties storageProperties;

	public UploadEntity getUpload(UUID userId, UUID uploadId) throws UploadNotFoundException {
		return uploadRepository
			.findByIdAndCreatedBy(uploadId, userId)
			.orElseThrow(() -> new UploadNotFoundException("Upload not found"));
	}

	public CsvScan parseCsv(String storageKey) throws InvalidCsvException, Exception {
		log.info("Parsing CSV: {}", storageKey);

		Path uploadsRoot = storageProperties.uploadsRoot();

		if (
			storageKey.contains("/") ||
				storageKey.contains("\\") ||
				storageKey.contains("..")
		) {
			throw new InvalidCsvException(InvalidCsvException.Reason.INVALID_FILE, "Invalid storage key");
		}

		Path destination = uploadsRoot
			.resolve(storageKey)
			.toAbsolutePath()
			.normalize();

		log.info("Destination: {}", destination);

		try (
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					Files.newInputStream(destination),
					StandardCharsets.UTF_8
				));

			CSVParser parser = CSVFormat.DEFAULT
				.builder()
				.setHeader()
				.setSkipHeaderRecord(true)
				.setTrim(true)
				.get()
				.parse(reader)
		) {
			List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
			headers.removeIf(header -> header == null || header.isBlank());

			if (headers.isEmpty()) {
				throw new InvalidCsvException(InvalidCsvException.Reason.INVALID_HEADERS, "CSV file must include valid headers");
			}

			int rowCount = 0;
			List<InvoiceRowDetails> rows = new ArrayList<>();

			for (CSVRecord record : parser) {
				rowCount++;

				Map<String, String> row = new HashMap<>();

				for (String header : headers) {
					row.put(header, record.isMapped(header) ? record.get(header) : "");
				}

				rows.add(new InvoiceRowDetails(rowCount, row));
			}

			log.info("# of rows added: {}", rowCount);

			return new CsvScan(headers, rowCount, rows);

		} catch (IllegalArgumentException ex) {
			throw new InvalidCsvException(InvalidCsvException.Reason.INVALID_FILE, "Invalid CSV file");

		} catch (IOException ex) {
			throw new InvalidCsvException(InvalidCsvException.Reason.PARSE_ERROR, "Faile to parse CSV file");
		}
	}
}
