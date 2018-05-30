package it.uniroma2.dicii.bd.progetto.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value = {CalculateSBDistanceTest.class, CalculateSFDistanceTest.class, FilamentsImportTest.class,
		LoginTest.class, SearchFilamentByContrastEllipticityTest.class, SearchFilamentByNameIdTest.class, 
		SearchFilamentByNumOfSegmentsTest.class, SearchFilamentByRegionTest.class, SegmentPointsImportTest.class,
		TestBorderPointImport.class, TestInsertSatellite.class, TestInstrumentImport.class, TestSearchStarByFilament.class,
		TestSearchStarByRegion.class, TestStarImport.class, UserRegistrationTest.class})

public class TestSuite {}
