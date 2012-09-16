package shop;

import java.io.IOException;

import org.databene.benerator.StorageSystem;
import org.databene.commons.IOUtil;
import org.databene.commons.ReaderLineIterator;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.platform.csv.CSVEntityExporter;
import org.databene.platform.db.DBSystem;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVTableExporterDemo {
    
    private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String JDBC_URL = "jdbc:hsqldb:mem:benerator";
    private static final String USER = "sa";
    private static final String PASSWORD = null;

    private static Logger logger = LoggerFactory.getLogger(CSVTableExporterDemo.class);
    
    public static void main(String[] args) throws IOException {
    	// first we create a table with some data to export
        DBSystem db = new DBSystem(null, JDBC_URL, JDBC_DRIVER, USER, PASSWORD, new DataModel());
        try {
	        db.execute("create table db_data (" + 
	        			"    id   int," +
	        			"    name varchar(30) NOT NULL," +
	        			"    PRIMARY KEY  (id)" +
	        			")");
	        db.execute("insert into db_data values (1, 'alpha')");
	        db.execute("insert into db_data values (2, 'beta')");
	        db.execute("insert into db_data values (3, 'gamma')");
	        db.setFetchSize(100);
	        // ...and then we export it
	        exportTableAsCSV(db, "db_data.csv");
	        logger.info("...done!");
	        printFile("db_data.csv");
        } finally {
            db.execute("drop table db_data");
        }
    }

	private static void exportTableAsCSV(StorageSystem db, String filename) {
        DataSource<Entity> entities = db.queryEntities("db_data", null, null);
        DataIterator<Entity> iterator = null;
        try {
			iterator = entities.iterator();
		    DataContainer<Entity> container = iterator.next(new DataContainer<Entity>());
			Entity cursor = container.getData();
		    CSVEntityExporter exporter = new CSVEntityExporter(filename, cursor.descriptor());
			try {
		        logger.info("exporting data, please wait...");
		        exporter.startProductConsumption(cursor);
		        while ((container = iterator.next(container)) != null)
		            exporter.startProductConsumption(container.getData());
			} finally {
				exporter.close();
			}
		} finally {
	        IOUtil.close(iterator);
		}
    }
    
    private static void printFile(String filename) throws IOException {
    	System.out.println("Content of file " + filename + ":");
	    ReaderLineIterator iterator = null;
	    try {
			iterator = new ReaderLineIterator(IOUtil.getReaderForURI(filename));
			while (iterator.hasNext())
				System.out.println(iterator.next());
		} finally {
		    IOUtil.close(iterator);
		}
    }

}
