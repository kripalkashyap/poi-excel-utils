package org.hellojavaer.poi.excel.utils.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.hellojavaer.poi.excel.utils.ExcelProcessController;
import org.hellojavaer.poi.excel.utils.ExcelUtils;
import org.hellojavaer.poi.excel.utils.TestBean;
import org.hellojavaer.poi.excel.utils.TestEnum;
import org.hellojavaer.poi.excel.utils.read.ExcelCellValue;
import org.hellojavaer.poi.excel.utils.read.ExcelReadCellProcessor;
import org.hellojavaer.poi.excel.utils.read.ExcelReadCellValueMapping;
import org.hellojavaer.poi.excel.utils.read.ExcelReadContext;
import org.hellojavaer.poi.excel.utils.read.ExcelReadException;
import org.hellojavaer.poi.excel.utils.read.ExcelReadFieldMapping;
import org.hellojavaer.poi.excel.utils.read.ExcelReadRowProcessor;
import org.hellojavaer.poi.excel.utils.read.ExcelReadSheetProcessor;

/**
 * @author <a href="mailto:hellojavaer@gmail.com">zoukaiming</a>
 */
public class WriteDemo2 {

    private static List<TestBean> testDataCache;

    public static void main(String[] args) throws IOException {
        InputStream excelTemplate = WriteDemo2.class.getResourceAsStream("/excel/xlsx/template_file2.xlsx");
        URL url = WriteDemo2.class.getResource("/");
        final String outputFilePath = url.getPath() + "output_file2.xlsx";
        File outputFile = new File(outputFilePath);
        outputFile.createNewFile();
        FileOutputStream output = new FileOutputStream(outputFile);

        final AtomicLong rowIndex = new AtomicLong(0);
        ExcelWriteSheetProcessor<TestBean> sheetProcessor = new ExcelWriteSheetProcessor<TestBean>() {

            @Override
            public void beforeProcess(ExcelWriteContext<TestBean> context) {
                System.out.println("write excel start!");
            }

            @Override
            public List<TestBean> getDataList(ExcelWriteContext<TestBean> context) {
                int pageSize = 10;
                List<TestBean> list = pageQuery(rowIndex.longValue(), pageSize);
                rowIndex.getAndAdd(pageSize);
                return list;
            }

            @Override
            public void onException(ExcelWriteContext<TestBean> context, RuntimeException e) {
                if (e instanceof ExcelWriteException) {
                    ExcelWriteException ewe = (ExcelWriteException) e;
                    if (ewe.getCode() == ExcelWriteException.CODE_OF_FIELD_VALUE_NOT_MATCHED) {
                        System.out.println("at row:" + (ewe.getRowIndex() + 1) + " column:" + ewe.getColStrIndex()
                                           + ", data doesn't match.");
                    } else {
                        System.out.println("at row:" + (ewe.getRowIndex() + 1) + " column:" + ewe.getColStrIndex()
                                           + ", process error. detail message is: " + ewe.getMessage());
                    }
                } else {
                    throw e;
                }
            }

            @Override
            public void afterProcess(ExcelWriteContext<TestBean> context) {
                context.setCellValue(2, 0, "Test Output");
                context.setCellValue(4, 0, "zoukaiming");
                context.setCellValue(6, 0, "hellojavaer@gmail.com");
                context.setCellValue(8, 0, new Date());
                System.out.println("write excel end!");
                System.out.println("output file path is " + outputFilePath);
            }
        };
        ExcelWriteFieldMapping fieldMapping = new ExcelWriteFieldMapping();
        fieldMapping.put("C", "byteField");
        fieldMapping.put("D", "shortField");
        fieldMapping.put("E", "intField");
        fieldMapping.put("F", "longField");
        fieldMapping.put("G", "floatField");
        fieldMapping.put("H", "doubleField");
        fieldMapping.put("I", "boolField");
        fieldMapping.put("J", "stringField");
        fieldMapping.put("K", "dateField");
        fieldMapping.put("L", "enumField1", new ExcelWriteCellProcessor<TestBean>() {

            public Cell process(ExcelWriteContext<TestBean> context, TestBean t, Cell cell) {
                if (t.getEnumField1() == null) {
                    cell.setCellValue("Please select");
                }
                return cell;
            }
        });
        ExcelWriteCellValueMapping kValueMapping = new ExcelWriteCellValueMapping();
        kValueMapping.put(null, "Please select");
        kValueMapping.put(TestEnum.AA.toString(), "Option1");
        kValueMapping.put(TestEnum.BB.toString(), "Option2");
        kValueMapping.put(TestEnum.CC.toString(), "Option3");
        fieldMapping.put("M", "enumField2", kValueMapping);

        sheetProcessor.setSheetIndex(0);
        sheetProcessor.setRowStartIndex(1);
        sheetProcessor.setFieldMapping(fieldMapping);
        sheetProcessor.setTemplateRowIndex(1);

        ExcelUtils.write(excelTemplate, output, sheetProcessor);
    }

    private static List<TestBean> pageQuery(long rowIndex, int pageSize) {
        if (testDataCache == null) {
            testDataCache = getInputData();
        }
        if (rowIndex >= testDataCache.size()) {
            return null;
        } else {
            int endIndex = (int) (rowIndex + pageSize - 1);
            if (endIndex > testDataCache.size() - 1) {
                endIndex = testDataCache.size() - 1;
            }
            return testDataCache.subList((int) rowIndex, endIndex);
        }
    }

    private static List<TestBean> getInputData() {
        final List<TestBean> re = new ArrayList<TestBean>();
        InputStream in = WriteDemo2.class.getResourceAsStream("/excel/xlsx/data_file2.xlsx");
        ExcelReadSheetProcessor<TestBean> sheetProcessor = new ExcelReadSheetProcessor<TestBean>() {

            @Override
            public void beforeProcess(ExcelReadContext<TestBean> context) {

            }

            @Override
            public void process(ExcelReadContext<TestBean> context, List<TestBean> list) {
                re.addAll(list);
            }

            @Override
            public void onExcepton(ExcelReadContext<TestBean> context, RuntimeException e) {
                if (e instanceof ExcelReadException) {
                    ExcelReadException ere = (ExcelReadException) e;
                    if (ere.getCode() == ExcelReadException.CODE_OF_CELL_VALUE_REQUIRED) {
                        System.out.println("at row:" + (ere.getRowIndex() + 1) + " column:" + ere.getColStrIndex()
                                           + ", data cant't be null.");
                    } else if (ere.getCode() == ExcelReadException.CODE_OF_CELL_VALUE_NOT_MATCHED) {
                        System.out.println("at row:" + (ere.getRowIndex() + 1) + " column:" + ere.getColStrIndex()
                                           + ", data doesn't match.");
                    } else if (ere.getCode() == ExcelReadException.CODE_OF_CELL_ERROR) {
                        System.out.println("at row:" + (ere.getRowIndex() + 1) + " column:" + ere.getColStrIndex()
                                           + ", cell error.");
                    } else {
                        System.out.println("at row:" + (ere.getRowIndex() + 1) + " column:" + ere.getColStrIndex()
                                           + ", process error. detail message is: " + ere.getMessage());
                    }
                } else {
                    throw e;
                }
            }

            @Override
            public void afterProcess(ExcelReadContext<TestBean> context) {

            }
        };

        ExcelReadFieldMapping fieldMapping = new ExcelReadFieldMapping();
        fieldMapping.put("A", "byteField");
        fieldMapping.put("B", "shortField");
        fieldMapping.put("C", "intField");
        fieldMapping.put("D", "longField");
        fieldMapping.put("E", "floatField");
        fieldMapping.put("F", "doubleField");
        fieldMapping.put("G", "boolField");
        fieldMapping.put("H", "stringField");
        fieldMapping.put("I", "dateField");

        fieldMapping.put("J", "enumField1", new ExcelReadCellProcessor() {

            public Object process(ExcelReadContext<?> context, Cell cell, ExcelCellValue cellValue) {
                String str = cellValue.getStringValue();
                if (StringUtils.isBlank(str)) {
                    return null;
                } else {
                    return str;
                }
            }
        });

        ExcelReadCellValueMapping valueMapping = new ExcelReadCellValueMapping();
        valueMapping.put("Please select", null);
        valueMapping.put("Option1", TestEnum.AA.toString());
        valueMapping.put("Option2", TestEnum.BB.toString());
        valueMapping.put("Option3", TestEnum.CC.toString());
        fieldMapping.put("K", "enumField2", valueMapping, false);

        sheetProcessor.setSheetIndex(0);// required.it can be replaced with 'setSheetName(sheetName)';
        sheetProcessor.setRowStartIndex(1);//
        sheetProcessor.setTargetClass(TestBean.class);// required
        sheetProcessor.setFieldMapping(fieldMapping);// required
        sheetProcessor.setRowProcessor(new ExcelReadRowProcessor<TestBean>() {

            public TestBean process(ExcelProcessController controller, ExcelReadContext<TestBean> context, Row row,
                                    TestBean t) {
                return t;
            }
        });

        ExcelUtils.read(in, sheetProcessor);
        return re;
    }
}