package codehows.com.daehoint.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExcelData {

    private Map<String, String> cellData; //<"C4", "생산부">

    public ExcelData() {
        cellData = new HashMap<>();
    }

    public void addData(String cellAddress, String value) {
        this.cellData.put(cellAddress, value);
    }

    public String getData(String cellAddress) {
        return this.cellData.get(cellAddress);
    }

    public Set<String> getDataSet() {
        return cellData.keySet();
    }
}
