package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;

public class ApacheCSVParser extends CSVFileParser{

	@Override
	public ArrayList<FilamentBean> getFilamentBeans(File importedFile) throws CSVFileParserException {
		CSVParser csvFileParser = null;
		FileReader fileReader = null;
		try {	
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(super.FILAMENTS_FILE_HEADERS);
			fileReader = new FileReader(importedFile);
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			ArrayList<FilamentBean> filamentBeans = new ArrayList<>();
			// Nota: nel ciclo viene saltata la riga contenente gli header
			for (int i = 1; i < csvRecords.size(); ++i) {
				CSVRecord record = (CSVRecord)csvRecords.get(i);
				FilamentBean filamentBean = new FilamentBean();
				filamentBean.setName(record.get("NAME"));
				filamentBean.setNumber(Integer.parseInt(record.get("IDFIL")));
				filamentBean.setContrast(Double.parseDouble(record.get("CONTRAST")));
				filamentBean.setEllipticity(Double.parseDouble(record.get("ELLIPTICITY")));
				filamentBean.setInstrumentName(record.get("INSTRUMENT"));
				filamentBeans.add(filamentBean);
			}
			return filamentBeans;
		} catch (IOException | IllegalArgumentException e) {
			throw new CSVFileParserException(e.getMessage(), e.getCause());
		} finally {
			if (csvFileParser != null) {
				try {
					csvFileParser.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
		}
	}
}
