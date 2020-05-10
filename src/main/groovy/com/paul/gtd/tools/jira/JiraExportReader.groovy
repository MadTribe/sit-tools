package com.paul.gtd.tools.jira;

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import static org.apache.poi.ss.usermodel.Cell.*
import java.nio.file.Paths
import groovy.json.JsonOutput
import static com.paul.gtd.tools.Display.*;

public class JiraExportReader {
    private int headerRow = 3;
    private FieldMapping fieldMapping = new FieldMapping();

    def readExport(String file){

       def issues = [];
       Paths.get(file).withInputStream { input ->

          def workbook = new XSSFWorkbook(input)
          def sheet = workbook.getSheetAt(0)

          int cellIdx  = 0;
          for (cell in sheet.getRow(headerRow).cellIterator()) {
              def cellName = cell.toString();
              def fieldReader = fieldMapping.configFieldReader(cellIdx, cellName)

              cellIdx++
              println cellName;
          }

          def currentRowIdx = headerRow + 1;



          def currentRow = sheet.getRow(currentRowIdx++);
          while (currentRow != null){
              def rowData = [:];
              issues << rowData;
              cellIdx  = 0;
              for (cell in currentRow.cellIterator()) {
                  def cellValue = cell.toString();
                  def fieldName = fieldMapping.fieldName[cellIdx]
                  def fieldReader = fieldMapping.fieldReader[cellIdx]

                  rowData[fieldName] = fieldReader(cellValue);


                  cellIdx++

              }
              currentRow = sheet.getRow(currentRowIdx++);
          }



      }

      return issues;

    }




}
